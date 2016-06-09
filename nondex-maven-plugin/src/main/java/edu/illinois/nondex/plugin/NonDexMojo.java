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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashSet;
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
public class NonDexMojo extends AbstractNondexMojo {

    private List<NonDexSurefireExecution> executions = new LinkedList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        MojoExecutionException allExceptions = null;
        CleanSurefireExecution cleanExec = new CleanSurefireExecution(
                this.surefire, this.originalArgLine, this.mavenProject,
                    this.mavenSession, this.pluginManager);

        allExceptions = this.executeSurefireExecution(allExceptions, cleanExec);

        for (int i = 0; i < this.numRuns; i++) {
            NonDexSurefireExecution execution =
                    new NonDexSurefireExecution(this.mode, this.computeIthSeed(i),
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

        this.printSummary(cleanExec);

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
        if (failedInClean.isEmpty()) {
            return;
        }
        for (NonDexSurefireExecution exec : this.executions) {
            exec.getConfiguration().filterTests(failedInClean);
        }
    }

    private MojoExecutionException executeSurefireExecution(MojoExecutionException allExceptions,
            CleanSurefireExecution execution) {
        try {
            execution.run();
        } catch (MojoExecutionException ex) {
            allExceptions = (MojoExecutionException) Utils.linkException(ex, allExceptions);
        }
        return allExceptions;
    }

    private int computeIthSeed(int ithSeed) {
        return Utils.computeIthSeed(ithSeed, this.rerun, this.seed);
    }

    private void printSummary(CleanSurefireExecution cleanExec) {
        Set<String> allFailures = new HashSet<>();
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
                    (execution.getConfiguration().executionId + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Cannot write execution id to current run file", ex);
        }
    }

}
