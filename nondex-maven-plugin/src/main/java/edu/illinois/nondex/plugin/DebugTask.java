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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Utils;

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
    private Configuration lastFailingConfig;

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

    public String debug() throws MojoExecutionException {
        Pair<Integer, Integer> limits = Pair.of(Integer.MIN_VALUE, Integer.MAX_VALUE);
        Configuration debConfig = null;

        //The test must have failed if it's being debugged, ergo there should exist a failing configuration

        assert (!failingConfigurations.isEmpty());

        limits = debugWithConfigurations(limits, this.failingConfigurations);

        if (this.lastFailingConfig != null) {
            return this.lastFailingConfig.toArgLine();
        }

        // The seeds that failed with the full test-suite no longer fail
        // Searching for different seeds
        Set<Configuration> retryWOtherSeeds = createNewSeedsToRetry();
        limits = debugWithConfigurations(limits, retryWOtherSeeds);

        if (this.lastFailingConfig != null) {
            return this.lastFailingConfig.toArgLine();
        }

        return "cannot reproduce. may be flaky due to other causes";
    }

    private Set<Configuration> createNewSeedsToRetry() {
        Configuration someFailingConfig = failingConfigurations.iterator().next();
        int newSeed = someFailingConfig.seed * ConfigurationDefaults.SEED_FACTOR;
        Set<Configuration> retryWOtherSeeds = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Configuration newConfig = new Configuration(someFailingConfig.mode,
                    Utils.computeIthSeed(i, false, newSeed),
                    someFailingConfig.filter, someFailingConfig.start, someFailingConfig.end,
                    someFailingConfig.testName, someFailingConfig.executionId);
            retryWOtherSeeds.add(newConfig);
        }
        return retryWOtherSeeds;
    }

    private Pair<Integer, Integer> debugWithConfigurations(Pair<Integer, Integer> limits,
            Set<Configuration> failingConfigurations) {
        Configuration debConfig = null;
        for (Configuration config : failingConfigurations) {
            if (failsOnDry(config)) {
                Pair<Integer, Integer> newLimits = startDebugBinary(config);
                if (newLimits.getRight() - newLimits.getLeft() < limits.getRight() - limits.getLeft()) {
                    limits = newLimits;
                    debConfig = this.lastFailingConfig;
                    if (newLimits.getRight() - newLimits.getLeft() == 0) {
                        break;
                    }
                }
            }
        }
        this.lastFailingConfig = debConfig;
        return limits;
    }

    private boolean failsOnDry(Configuration config) {
        return true;
    }

    public Pair<Integer, Integer> startDebugBinary(Configuration config) {
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
                Logger.getGlobal().log(Level.FINE, "Binary splitting did not work. Going to linear");
                return startDebugLinear(config, start, end);
            }
        }
        return Pair.of(start, end);
    }

    public Pair<Integer, Integer> startDebugLinear(Configuration config, int start, int end) {
        while (start < end) {
            Logger.getGlobal().log(Level.INFO, "Debugging for " + this.test + " " + start + " : " + end);

            if (startDebug(config, start, end - 1)) {
                end = end - 1;
                continue;
            } else if (startDebug(config, start + 1, end)) {
                start = start + 1;
                continue;
            } else {
                Logger.getGlobal().log(Level.FINE, "Splitting did not work. Does not fail with linear.");
                break;
            }
        }
        return Pair.of(start, end);
    }

    public boolean startDebug(Configuration config, int start, int end) {
        NonDexSurefireExecution execution = new NonDexSurefireExecution(config,
                    start, end, test, surefire, originalArgLine, mavenProject, mavenSession, pluginManager);
        try {
            execution.run();
        } catch (Throwable thr) {
            this.lastFailingConfig = execution.getConfiguration();
            return true;
        }
        return false;
    }
}
