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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Mode;

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
    
    @Parameter(property = ConfigurationDefaults.PROPERTY_NUM_RERUNS, 
            defaultValue = ConfigurationDefaults.DEFAULT_NUM_RERUNS_STR)
    protected int numReruns;
    
    @Parameter(property = ConfigurationDefaults.PROPERTY_EXECUTION_ID, 
            defaultValue = ConfigurationDefaults.PROPERTY_DEFAULT_EXECUTION_ID)
    protected String executionId;
    
    @Parameter(property = ConfigurationDefaults.PROPERTY_RUN_ID, 
            defaultValue = ConfigurationDefaults.PROPERTY_DEFAULT_RUN_ID)
    protected String runId;
    
    @Parameter(property = ConfigurationDefaults.LOGGING_LEVEL, 
            defaultValue = ConfigurationDefaults.PROPERTY_DEFAULT_LOGGING_LEVEL)
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
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        Logger.getGlobal().setLoggineLevel(Level.parse(this.loggingLevel));
        this.surefire = lookupPlugin("org.apache.maven.plugins:maven-surefire-plugin");
        
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
