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

import java.util.logging.Level;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Utils;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.twdata.maven.mojoexecutor.MojoExecutor;

public class CleanSurefireExecution {

    protected static boolean isFirstRun = true;
    protected Configuration configuration;
    protected final String executionId;

    protected Plugin surefire;
    protected MavenProject mavenProject;
    protected MavenSession mavenSession;
    protected BuildPluginManager pluginManager;

    protected String originalArgLine;

    protected CleanSurefireExecution(Plugin surefire, String originalArgLine, String executionId,
            MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager pluginManager) {
        this.executionId = executionId;
        this.surefire = surefire;
        this.originalArgLine = originalArgLine;
        this.mavenProject = mavenProject;
        this.mavenSession = mavenSession;
        this.pluginManager = pluginManager;
        this.configuration = new Configuration(executionId);
    }

    public CleanSurefireExecution(Plugin surefire, String originalArgLine,
            MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager pluginManager) {
        this(surefire, originalArgLine, "clean_" + Utils.getFreshExecutionId(), mavenProject, mavenSession, pluginManager);
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void run() throws MojoExecutionException {
        this.setupArgline();
        try {
            Logger.getGlobal().log(Level.CONFIG, this.configuration.toString());
            MojoExecutor.executeMojo(this.surefire, MojoExecutor.goal("test"),
                    this.createListenerConfiguration((Xpp3Dom) this.surefire.getConfiguration()),
                    MojoExecutor.executionEnvironment(this.mavenProject, this.mavenSession, this.pluginManager));
        } catch (MojoExecutionException mojoException) {
            Logger.getGlobal().log(Level.INFO, "Surefire failed when running tests for " + this.configuration.executionId);
            throw mojoException;
        }

    }


    protected void setupArgline() {
        Logger.getGlobal().log(Level.FINE, "Running clean surefire.");
        this.mavenProject.getProperties().setProperty("argLine",
                this.originalArgLine + " " + this.configuration.toArgLine());
    }

    private Xpp3Dom createListenerConfiguration(Xpp3Dom configuration) {
        Xpp3Dom configNode = configuration;
        if (configNode == null) {
            configNode = new Xpp3Dom("configuration");
        }

        if (!CleanSurefireExecution.isFirstRun) {
            return configNode;
        }

        CleanSurefireExecution.isFirstRun = false;
        Logger.getGlobal().log(Level.SEVERE, "Adding listener");
        Xpp3Dom properties = this.createChildIfNotExists(configNode, "properties");

        if (properties.getChild("property") != null) {
            for (Xpp3Dom property : properties.getChildren()) {
                if ("property".equals(property.getName()) && "listener".equals(property.getChild("name").getValue())) {
                    String value = property.getChild("value").getValue();
                    value = value + ",edu.illinois.nondex.plugin.TestStatusListener";
                    property.getChild("value").setValue(value);
                    return configNode;
                }
            }
        }

        properties.addChild(this.makeListenerNode());
        return configNode;
    }

    private Xpp3Dom makeListenerNode() {
        Xpp3Dom result = new Xpp3Dom("property");
        Xpp3Dom name = new Xpp3Dom("name");
        name.setValue("listener");
        Xpp3Dom value = new Xpp3Dom("value");
        value.setValue("edu.illinois.nondex.plugin.TestStatusListener");

        result.addChild(name);
        result.addChild(value);

        return result;
    }

    private Xpp3Dom createChildIfNotExists(Xpp3Dom configuration, String child) {
        Xpp3Dom childNode = configuration.getChild(child);
        if (childNode == null) {
            childNode = new Xpp3Dom(child);
            configuration.addChild(childNode);
        }
        return childNode;
    }
}
