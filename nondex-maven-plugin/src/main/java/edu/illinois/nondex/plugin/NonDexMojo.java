/*
The MIT License (MIT)
Copyright (c) 2015 Alex Gyori
Copyright (c) 2015 Owolabi Legunsen
Copyright (c) 2015 Darko Marinov
Copyright (c) 2015 August Shi


Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package edu.illinois.nondex.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Utils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "nondex", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
public class NonDexMojo extends AbstractNonDexMojo {

    private List<NonDexSurefireExecution> executions = new LinkedList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        Logger.getGlobal().log(Level.INFO, "The original argline is: " + this.originalArgLine);
        MojoExecutionException allExceptions = null;
        CleanSurefireExecution cleanExec = new CleanSurefireExecution(
                this.surefire, this.originalArgLine, this.mavenProject,
                    this.mavenSession, this.pluginManager,
                    Paths.get(this.baseDir.getAbsolutePath(), ConfigurationDefaults.DEFAULT_NONDEX_DIR).toString());

        // If we add clean exceptions to allExceptions then the build fails if anything fails without nondex.
        // Everything in nondex-test is expected to fail without nondex so we throw away the result here.
        this.executeSurefireExecution(allExceptions, cleanExec);

        for (int i = 0; i < this.numRuns; i++) {
            NonDexSurefireExecution execution =
                    new NonDexSurefireExecution(this.mode, this.computeIthSeed(i), this.selectTest,
                            Pattern.compile(this.filter), this.start, this.end,
                            Paths.get(this.baseDir.getAbsolutePath(), ConfigurationDefaults.DEFAULT_NONDEX_DIR).toString(),
                            Paths.get(this.baseDir.getAbsolutePath(), ConfigurationDefaults.DEFAULT_NONDEX_JAR_DIR)
                                .toString(),
                            this.surefire, this.originalArgLine, this.mavenProject,
                            this.mavenSession, this.pluginManager);
            this.executions.add(execution);
            allExceptions = this.executeSurefireExecution(allExceptions, execution);
            this.writeCurrentRunInfo(execution);
        }

        this.writeCurrentRunInfo(cleanExec);
        this.postProcessExecutions(cleanExec);

        Configuration config = this.executions.get(0).getConfiguration();

        this.printSummary(cleanExec, config);

        try {
            Files.copy(config.getRunFilePath(), config.getLatestRunFilePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Could not copy current run info to latest", ex);
        }

        this.getLog().info("[NonDex] The id of this run is: " + this.executions.get(0).getConfiguration().executionId);
        if (allExceptions != null) {
            throw allExceptions;
        }

    }

    private void postProcessExecutions(CleanSurefireExecution cleanExec) {
        Collection<String> failedInClean = cleanExec.getConfiguration().getFailedTests();

        for (NonDexSurefireExecution exec : this.executions) {
            exec.getConfiguration().filterTests(failedInClean);
        }
    }

    private MojoExecutionException executeSurefireExecution(MojoExecutionException allExceptions,
            CleanSurefireExecution execution) {
        try {
            execution.run();
        } catch (MojoExecutionException ex) {
            return (MojoExecutionException) Utils.linkException(ex, allExceptions);
        }
        return allExceptions;
    }

    private int computeIthSeed(int ithSeed) {
        return Utils.computeIthSeed(ithSeed, this.rerun, this.seed);
    }

    private void printSummary(CleanSurefireExecution cleanExec, Configuration config) {
        Set<String> allFailures = new LinkedHashSet<>();
        this.getLog().info("NonDex SUMMARY:");
        for (CleanSurefireExecution exec : this.executions) {
            this.printExecutionResults(allFailures, exec);
        }

        if (!cleanExec.getConfiguration().getFailedTests().isEmpty()) {
            this.getLog().info("Tests are failing without NonDex.");
            this.printExecutionResults(allFailures, cleanExec);
        }
        allFailures.removeAll(cleanExec.getConfiguration().getFailedTests());

        this.getLog().info("Across all seeds:");
        for (String test : allFailures) {
            this.getLog().info(test);
        }

        this.generateHtml(allFailures, config);
    }

    private void generateHtml(Set<String> allFailures, Configuration config) {
        String head = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Test Results</title>"
                + "<style>"
                + "table { border-collapse: collapse; width: 100%; }"
                + "th { height: 50%; }"
                + "th, td { padding: 10px; text-align: left; }"
                + "tr:nth-child(even) {background-color:#f2f2f2;}"
                + ".x { color: red; font-size: 150%;}"
                + ".✓ { color: green; font-size: 150%;}"
                + "</style>"
                + "</head>";
        String html = head + "<body>" + "<table>";

        html += "<thead><tr>";
        html += "<th>Test Name</th>";
        for (int iter = 0; iter < this.executions.size(); iter++) {
            html += "<th>";
            html += "" + this.executions.get(iter).getConfiguration().seed;
            html += "</th>";
        }
        html += "</tr></thead>";
        html += "<tbody>";
        for (String failure : allFailures) {
            html += "<tr><td>" + failure + "</td>";
            for (CleanSurefireExecution exec : this.executions) {
                boolean testDidFail = false;
                for (String test : exec.getConfiguration().getFailedTests()) {
                    if (test.equals(failure)) {
                        testDidFail = true;
                    }
                }
                if (testDidFail) {
                    html += "<td class=\"x\">&#10006;</td>";
                } else {
                    html += "<td class=\"✓\">&#10004;</td>";
                }
            }
            html += "</tr>";
        }
        html += "</tbody></table></body></html>";

        File nondexDir = config.getNondexDir().toFile();
        File htmlFile = new File(nondexDir, "test_results.html");
        try {
            PrintWriter htmlPrinter = new PrintWriter(htmlFile);
            htmlPrinter.print(html);
            htmlPrinter.close();
        } catch (FileNotFoundException ex) {
            this.getLog().info("File Missing.  But that shouldn't happen...");
        }
        this.getLog().info("Test results can be found at: ");
        this.getLog().info("file://" + htmlFile.getPath());
    }

    private void printExecutionResults(Set<String> allFailures, CleanSurefireExecution exec) {
        this.getLog().info("*********");
        this.getLog().info("mvn nondex:nondex " + exec.getConfiguration().toArgLine());
        Collection<String> failedTests = exec.getConfiguration().getFailedTests();
        if (failedTests.isEmpty()) {
            this.getLog().info("No Test Failed with this configuration.");
        }
        for (String test : failedTests) {
            allFailures.add(test);
            this.getLog().warn(test);
        }
        this.getLog().info("*********");
    }

    private void writeCurrentRunInfo(CleanSurefireExecution execution) {
        try {
            // TODO(gyori): This is quite ugly, you grabbing here the first from a list to establish a run id.
            Files.write(this.executions.get(0).getConfiguration().getRunFilePath(),
                        (execution.getConfiguration().executionId + String.format("%n")).getBytes(),
                         StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Cannot write execution id to current run file", ex);
        }
    }

}
