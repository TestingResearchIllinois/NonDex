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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Mode;
import edu.illinois.nondex.common.Utils;
import edu.illinois.nondex.instr.Instrumenter;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Execute(phase = LifecyclePhase.TEST_COMPILE)
public abstract class AbstractNondexMojo extends AbstractMojo {
    // NonDex Mojo specific properties
    @Parameter(property = ConfigurationDefaults.PROPERTY_SEED, defaultValue = ConfigurationDefaults.DEFAULT_SEED_STR)
    protected int seed;

    @Parameter(property = ConfigurationDefaults.PROPERTY_MODE, defaultValue = ConfigurationDefaults.DEFAULT_MODE_STR)
    protected Mode mode;

    @Parameter(property = ConfigurationDefaults.PROPERTY_FILTER, defaultValue = ConfigurationDefaults.DEFAULT_FILTER)
    protected String filter;

    @Parameter(property = ConfigurationDefaults.PROPERTY_START, defaultValue = ConfigurationDefaults.DEFAULT_START_STR)
    protected long start;

    @Parameter(property = ConfigurationDefaults.PROPERTY_START, defaultValue = ConfigurationDefaults.DEFAULT_END_STR)
    protected long end;

    @Parameter(property = ConfigurationDefaults.PROPERTY_NUM_RUNS,
            defaultValue = ConfigurationDefaults.DEFAULT_NUM_RUNS_STR)
    protected int numRuns;

    @Parameter(property = ConfigurationDefaults.PROPERTY_RERUN,
            defaultValue = ConfigurationDefaults.DEFAULT_RERUN_STR)
    protected boolean rerun;

    @Parameter(property = ConfigurationDefaults.PROPERTY_EXECUTION_ID,
            defaultValue = ConfigurationDefaults.PROPERTY_DEFAULT_EXECUTION_ID)
    protected String executionId;

    @Parameter(property = ConfigurationDefaults.PROPERTY_RUN_ID,
            defaultValue = ConfigurationDefaults.PROPERTY_DEFAULT_RUN_ID)
    protected String runId;

    @Parameter(property = ConfigurationDefaults.PROPERTY_LOGGING_LEVEL,
            defaultValue = ConfigurationDefaults.DEFAULT_LOGGING_LEVEL)
    protected String loggingLevel;

    // Generic properties
    @Parameter(property = "project")
    protected MavenProject mavenProject;
    @Parameter(defaultValue = "${project.build.directory}")
    protected String projectBuildDir;
    @Parameter(defaultValue = "${basedir}")
    protected File basedir;
    @Parameter(property = "goal", alias = "mojo")
    protected String goal;
    @Component
    protected MavenSession mavenSession;
    @Component
    protected BuildPluginManager pluginManager;

    protected Plugin surefire;
    protected String originalArgLine;


    private Path rtJarPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Logger.getGlobal().setLoggineLevel(Level.parse(this.loggingLevel));
        String javaHome = System.getenv().get("JAVA_HOME");
        if (javaHome == null) {
            Logger.getGlobal().log(Level.SEVERE, "JAVA_HOME is not set!");
            throw new MojoExecutionException("JAVA_HOME is not set!");
        }

        this.rtJarPath = Utils.getRtJarLocation(javaHome);
        if (this.rtJarPath == null) {
            Logger.getGlobal().log(Level.SEVERE, "Cannot find the rt.jar!");
            throw new MojoExecutionException("Cannot find the rt.jar!");
        }

        try {
            Paths.get(ConfigurationDefaults.NONDEX_DIR, this.executionId).toFile().mkdirs();
            Instrumenter.instrument(rtJarPath.toString(), ConfigurationDefaults.NONDEX_DIR + "/"
                    + ConfigurationDefaults.INSTRUMENTATION_JAR);
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        this.surefire = this.lookupPlugin("org.apache.maven.plugins:maven-surefire-plugin");

        Properties localProperties = this.mavenProject.getProperties();
        this.originalArgLine = localProperties.getProperty("argLine", "");
    }

    private Plugin lookupPlugin(String paramString) {
        List<Plugin> localList = this.mavenProject.getBuildPlugins();
        Iterator<Plugin> localIterator = localList.iterator();
        while (localIterator.hasNext()) {
            Plugin localPlugin = localIterator.next();
            if (paramString.equalsIgnoreCase(localPlugin.getKey())) {
                return localPlugin;
            }
        }
        return null;
    }
}
