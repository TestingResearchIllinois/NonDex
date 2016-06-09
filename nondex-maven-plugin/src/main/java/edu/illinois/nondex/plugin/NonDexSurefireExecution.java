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

public class NonDexSurefireExecution extends CleanSurefireExecution {

    private NonDexSurefireExecution(Plugin surefire, String originalArgLine,
            MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager pluginManager) {
        super(surefire, originalArgLine, Utils.getFreshExecutionId(), mavenProject, mavenSession, pluginManager);
    }

    public NonDexSurefireExecution(Mode mode, int seed, Pattern filter, long start, long end, String nondexDir,
            String nondexJarDir, Plugin surefire, String originalArgLine, MavenProject mavenProject,
            MavenSession mavenSession, BuildPluginManager pluginManager) {
        this(surefire, originalArgLine, mavenProject, mavenSession, pluginManager);
        this.configuration = new Configuration(mode, seed, filter, start, end, nondexDir, nondexJarDir, null,
                this.executionId);
    }

    public NonDexSurefireExecution(Configuration config, int start, int end, String test, Plugin surefire,
            String originalArgLine, MavenProject mavenProject, MavenSession mavenSession,
            BuildPluginManager pluginManager) {

        this(surefire, originalArgLine, mavenProject, mavenSession, pluginManager);
        this.configuration = new Configuration(config.mode, config.seed, config.filter, start,
                end, config.nondexDir, config.nondexJarDir, test, this.executionId);
    }

    @Override
    protected void setupArgline() {
        String localRepo = this.mavenSession.getSettings().getLocalRepository();
        String pathToNondex = this.getPathToNondexJar(localRepo);
        Logger.getGlobal().log(Level.FINE, "Running surefire with: " + this.configuration.toArgLine());
        this.mavenProject.getProperties().setProperty("argLine",
                "" + "-Xbootclasspath/p:" + pathToNondex + " " + this.originalArgLine + " "
                + this.configuration.toArgLine());

    }

    private String getPathToNondexJar(String localRepo) {
        return this.configuration.nondexJarDir + "/" + ConfigurationDefaults.INSTRUMENTATION_JAR
                + ":" + Paths.get(localRepo, "edu/illinois/nondex-common/" + ConfigurationDefaults.VERSION
                + "/nondex-common-" + ConfigurationDefaults.VERSION + ".jar").toString();
    }

    public static void resetFirstRun() {
        CleanSurefireExecution.isFirstRun = true;
    }
}
