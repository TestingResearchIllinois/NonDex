/*
The MIT License (MIT)
Copyright (c) 2015 University of Illinois

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Mode;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "nondex", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class NonDexMojo extends AbstractMojo {
    // NonDex Mojo specific properties
    @Parameter(property = ConfigurationDefaults.PROPERTY_DEFAULT_SEED, defaultValue = ConfigurationDefaults.DEFAULT_SEED_STR)
    private int seed;
    @Parameter(property = ConfigurationDefaults.PROPERTY_DEFAULT_MODE, defaultValue = ConfigurationDefaults.DEFAULT_MODE_STR)
    private Mode mode;
    @Parameter(property = ConfigurationDefaults.PROPERTY_DEFAULT_FILTER, defaultValue = ConfigurationDefaults.DEFAULT_FILTER)
    private String filter;
    // Generic properties
    @Parameter(property = "project")
    protected MavenProject mavenProject;
    @Parameter(defaultValue = "${project.build.directory}")
    protected String projectBuildDir;
    @Parameter(defaultValue = "${basedir}")
    protected File basedir;
    @Parameter(property = "goal", alias = "mojo")
    private String goal;
    @Component
    private MavenSession mavenSession;
    @Component
    private BuildPluginManager pluginManager;

    private Plugin surefire;
    private String originalArgLine;

    

    public void execute() throws MojoExecutionException {
        getLog().info("Running NonDex!");
        
        this.surefire = lookupPlugin("org.apache.maven.plugins:maven-surefire-plugin");
        Properties localProperties = this.mavenProject.getProperties();
        this.originalArgLine = localProperties.getProperty("argLine", "");

        NonDexSurefireExecution execution = new NonDexSurefireExecution(mode, seed, filter, surefire, originalArgLine,
                mavenProject, mavenSession, pluginManager);
        execution.run();
        Collection<String> failedTests = execution.getFailedTests();
        for (String test : failedTests) {
            DebugTask debugging = new DebugTask(test, mode, seed, filter, surefire, originalArgLine,
                    mavenProject, mavenSession, pluginManager);
        }
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
