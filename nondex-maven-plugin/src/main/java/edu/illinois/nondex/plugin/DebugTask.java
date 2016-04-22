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

import edu.illinois.nondex.common.Mode;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;

public class DebugTask {

    private Mode mode;
    private String test;
    private int seed;
    private String filter;
    private Plugin surefire;
    private String originalArgLine;
    private MavenProject mavenProject;
    private MavenSession mavenSession;
    private BuildPluginManager pluginManager;

    public DebugTask(String test, Mode mode, int seed, String filter, Plugin surefire, String originalArgLine,
                     MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager pluginManager) {
        this.test = test;
        this.mode = mode;
        this.seed = seed;
        this.filter = filter;
        this.surefire = surefire;
        this.originalArgLine = originalArgLine;
        this.mavenProject = mavenProject;
        this.mavenSession = mavenSession;
        this.pluginManager = pluginManager;
    }
    
    public void debug() {
        NonDexSurefireExecution execution = new NonDexSurefireExecution(mode, seed, filter, surefire, originalArgLine,
                                                                        mavenProject, mavenSession, pluginManager);
        
    }

}
