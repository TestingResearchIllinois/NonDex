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
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.regex.Pattern;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Mode;
import edu.illinois.nondex.common.Utils;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.util.xml.Xpp3Dom;

public class NonDexSurefireExecution extends CleanSurefireExecution {

    private NonDexSurefireExecution(Plugin surefire, String originalArgLine, MavenProject mavenProject,
                                    MavenSession mavenSession, BuildPluginManager pluginManager, String nondexDir) {
        super(surefire, originalArgLine, Utils.getFreshExecutionId(),
                mavenProject, mavenSession, pluginManager, nondexDir);
    }

    public NonDexSurefireExecution(Mode mode, int seed, Pattern filter, long start, long end, String nondexDir,
            String nondexJarDir, Plugin surefire, String originalArgLine, MavenProject mavenProject,
            MavenSession mavenSession, BuildPluginManager pluginManager) {
        this(surefire, originalArgLine, mavenProject, mavenSession, pluginManager, nondexDir);
        this.configuration = new Configuration(mode, seed, filter, start, end, nondexDir, nondexJarDir, null,
                this.executionId, Logger.getGlobal().getLoggingLevel());
    }

    public NonDexSurefireExecution(Configuration config, long start, long end, boolean print, String test, Plugin surefire,
            String originalArgLine, MavenProject mavenProject, MavenSession mavenSession,
            BuildPluginManager pluginManager) {

        this(surefire, originalArgLine, mavenProject, mavenSession, pluginManager, config.nondexDir);
        this.configuration = new Configuration(config.mode, config.seed, config.filter, start,
                end, config.nondexDir, config.nondexJarDir, test, this.executionId,
                Logger.getGlobal().getLoggingLevel(), print);
    }

    @Override
    protected void setupArgline() {
        String localRepo = this.mavenSession.getSettings().getLocalRepository();
        String pathToNondex = this.getPathToNondexJar(localRepo);
        String annotationsModuleName = "nondex-annotations";
        // Only modify test in configuration if not null, because that means is debugging
        Xpp3Dom configElement = (Xpp3Dom)this.surefire.getConfiguration();
        if (configElement != null) {
            configElement.getChild("test").setValue(this.configuration.testName);
        }
        Logger.getGlobal().log(Level.FINE, "Running surefire with: " + this.configuration.toArgLine());
        this.mavenProject.getProperties().setProperty("argLine",
                "" + "-Xbootclasspath/p:" + pathToNondex + ":" + Paths.get(mavenSession.getLocalRepository().getBasedir(),
                        "edu", "illinois", annotationsModuleName, ConfigurationDefaults.VERSION,
                        annotationsModuleName + "-" + ConfigurationDefaults.VERSION + ".jar")
                        + " " + this.originalArgLine + " " + this.configuration.toArgLine());

    }

    @Override
    protected Xpp3Dom applyNonDexConfig(Xpp3Dom configuration) {
        return addExcludedGroups(super.applyNonDexConfig(configuration));
    }

    private Xpp3Dom addExcludedGroups(Xpp3Dom configNode) {
        for (Xpp3Dom config : configNode.getChildren()) {
            if ("excludedGroups".equals(config.getName())) {
                Logger.getGlobal().log(Level.INFO, "Adding excluded groups to existing ones");
                String current = config.getValue();
                current = "," + current;
                // It seems there is an error if you have the variable
                // in the excludedGroups string concatenated (in any
                // position) to the concrete class we are adding to
                // the excludedGroups
                // ${excludedGroups} appears when
                // there is no excludedGroups specified in the pom
                // and potentially in other situations
                current = current.replace(",${excludedGroups}", "");
                config.setValue("edu.illinois.NonDexIgnore" + current);
                return configNode;
            }
        }
        Logger.getGlobal().log(Level.INFO, "Adding excluded groups to newly created one");
        configNode.addChild(this.makeNode("excludedGroups", "edu.illinois.NonDexIgnore"));
        return configNode;
    }

    private String getPathToNondexJar(String localRepo) {
        String result = Paths.get(this.configuration.nondexJarDir, ConfigurationDefaults.INSTRUMENTATION_JAR)
            + File.pathSeparator + Paths.get(localRepo, "edu", "illinois", "nondex-common", ConfigurationDefaults.VERSION,
                              "nondex-common-" + ConfigurationDefaults.VERSION + ".jar");
        Logger.getGlobal().log(Level.FINE, "The nondex path is: " + result);
        return "\"" + result + "\"";
    }
}
