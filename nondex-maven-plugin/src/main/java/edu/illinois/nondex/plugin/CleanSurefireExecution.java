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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Utils;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.surefire.report.ReportTestCase;
import org.apache.maven.plugins.surefire.report.ReportTestSuite;
import org.apache.maven.plugins.surefire.report.SurefireReportParser;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor;

public class CleanSurefireExecution {

    protected Configuration configuration;
    protected final String executionId;

    protected Plugin surefire;
    protected MavenProject mavenProject;
    protected MavenSession mavenSession;
    protected BuildPluginManager pluginManager;

    protected String originalArgLine;

    protected CleanSurefireExecution(Plugin surefire, String originalArgLine, String executionId,
            MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager pluginManager,
            String nondexDir) {
        this.executionId = executionId;
        this.surefire = surefire;
        this.originalArgLine = originalArgLine;
        this.mavenProject = mavenProject;
        this.mavenSession = mavenSession;
        this.pluginManager = pluginManager;
        this.configuration = new Configuration(executionId, nondexDir);
    }

    public CleanSurefireExecution(Plugin surefire, String originalArgLine, MavenProject mavenProject,
            MavenSession mavenSession, BuildPluginManager pluginManager, String nondexDir) {
        this(surefire, originalArgLine, "clean_" + Utils.getFreshExecutionId(), mavenProject, mavenSession, pluginManager,
                nondexDir);
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void run() throws MojoExecutionException {
        try {
            Xpp3Dom domNode = this.applyNonDexConfig((Xpp3Dom) this.surefire.getConfiguration());
            this.setupArgline(domNode);
            Logger.getGlobal().log(Level.FINE, "Config node passed: " + domNode.toString());
            Logger.getGlobal().log(Level.FINE, this.mavenProject + "\n" + this.mavenSession + "\n" + this.pluginManager);
            Logger.getGlobal().log(Level.CONFIG, this.configuration.toString());
            Logger.getGlobal().log(Level.FINE, "Surefire config: " + this.surefire + "  " + MojoExecutor.goal("test")
                                   + " " + domNode + " "
                                   + MojoExecutor.executionEnvironment(this.mavenProject, this.mavenSession,
                                                                       this.pluginManager));
            MojoExecutor.executeMojo(this.surefire, MojoExecutor.goal("test"),
                    domNode,
                    MojoExecutor.executionEnvironment(this.mavenProject, this.mavenSession, this.pluginManager));
        } catch (MojoExecutionException mojoException) {
            Logger.getGlobal().log(Level.INFO, "Surefire failed when running tests for " + this.configuration.executionId);

            SurefireReportParser parser = new SurefireReportParser(
                    Arrays.asList(this.configuration.getExecutionDir().toFile()), Locale.getDefault());
            try {
                Set<String> failingTests = new LinkedHashSet<>();
                for (ReportTestSuite report : parser.parseXMLReportFiles()) {
                    for (ReportTestCase testCase : report.getTestCases()) {
                        // Record if failed, but not skipped
                        if (testCase.hasFailure() && !"skipped".equals(testCase.getFailureType())) {
                            failingTests.add(testCase.getFullClassName() + '#' + testCase.getName());
                        }
                    }
                }
                this.configuration.setFailures(failingTests);
            } catch (MavenReportException ex) {
                throw new MojoExecutionException("Failed to parse mvn reports!");
            }
            throw mojoException;
        } catch (Throwable tr) {
            Logger.getGlobal().log(Level.SEVERE, "Some exception that is highly unexpected: ", tr);
            throw tr;
        }
    }

    protected void setupArgline(Xpp3Dom configNode) {
        // create the NonDex argLine for surefire based on the current configuration
        // this adds things like where to save test reports, what directory NonDex
        // should store results in, what seed and mode should be used.
        String argLineToSet =  this.configuration.toArgLine();
        boolean added = false;
        for (Xpp3Dom config : configNode.getChildren()) {
            if ("argLine".equals(config.getName())) {
                Logger.getGlobal().log(Level.INFO, "Adding NonDex argLine to existing argLine specified by the project");
                String current = config.getValue();

                config.setValue(argLineToSet + " " + current);
                added = true;
                break;
            }
        }
        if (!added) {
            Logger.getGlobal().log(Level.INFO, "Creating new argline for Surefire");
            configNode.addChild(this.makeNode("argLine", argLineToSet));
        }

        // originalArgLine is the argLine set from Maven, not through the surefire config
        // if such an argLine exists, we modify that one also
        this.mavenProject.getProperties().setProperty("argLine",
                this.originalArgLine + " " + argLineToSet);
    }

    protected Xpp3Dom applyNonDexConfig(Xpp3Dom configuration) {
        Xpp3Dom configNode = configuration;
        if (configNode == null) {
            configNode = new Xpp3Dom("configuration");
        }

        return setReportOutputDirectory(configNode);
    }

    protected Xpp3Dom setReportOutputDirectory(Xpp3Dom configNode) {
        configNode = this.addAttributeToConfig(configNode, "reportsDirectory",
                this.configuration.getExecutionDir().toString());
        configNode = this.addAttributeToConfig(configNode, "disableXmlReport", "false");
        return configNode;
    }

    private Xpp3Dom addAttributeToConfig(Xpp3Dom configNode, String nodeName, String value) {
        for (Xpp3Dom config : configNode.getChildren()) {
            if (nodeName.equals(config.getName())) {
                config.setValue(value);
                return configNode;
            }
        }

        configNode.addChild(this.makeNode(nodeName, value));
        return configNode;
    }

    protected Xpp3Dom makeNode(String nodeName, String value) {
        Xpp3Dom node = new Xpp3Dom(nodeName);
        node.setValue(value);
        return node;
    }
}
