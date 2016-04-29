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

import java.util.Set;
import java.util.logging.Level;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.Logger;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

public class DebugTask {

    private String test;
    private Plugin surefire;
    private String originalArgLine;
    private MavenProject mavenProject;
    private MavenSession mavenSession;
    private BuildPluginManager pluginManager;
    private Set<Configuration> failingConfigurations;

    public DebugTask(String test, Plugin surefire, String originalArgLine, MavenProject mavenProject, 
            MavenSession mavenSession, BuildPluginManager pluginManager, 
            Set<Configuration> failingConfigurations) {
        this.test = test;
        this.surefire = surefire;
        this.originalArgLine = originalArgLine;
        this.mavenProject = mavenProject;
        this.mavenSession = mavenSession;
        this.pluginManager = pluginManager;
        this.failingConfigurations = failingConfigurations;
    }
    
    public Pair<Integer, Integer> debug() throws MojoExecutionException {
        for (Configuration config : failingConfigurations) {
            return startDebug(config);
        }
        return null;
    }
    
    public Pair<Integer, Integer> startDebug(Configuration config) {
        int start = 0;
        int end = config.getInvocationCount();
        while (start < end) {
            Logger.getGlobal().log(Level.INFO, "Debugging for " + this.test + " " + start + " : " + end);

            int midPoint = (start + end) / 2;
            if (startDebug(config, start, midPoint)) {
                end = midPoint;
                continue;
            } else if (startDebug(config, midPoint + 1, end)) {
                start = midPoint + 1;
                continue;
            } else {
                Logger.getGlobal().log(Level.FINE, "Splitting did not work. Does not fail on any half.");
            }
        }
        return Pair.of(start, end);
    }
    
    public boolean startDebug(Configuration config, int start, int end) {
        try {
            NonDexSurefireExecution execution = new NonDexSurefireExecution(config, 
                    start, end, test, surefire, originalArgLine, mavenProject, mavenSession, pluginManager);
            execution.run();
        } catch (Throwable thr) {
            return true;
        }
        return false;
    }
}
