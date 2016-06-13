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

import java.util.LinkedHashSet;
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
        Pair<Long, Long> limits = Pair.of(Long.MIN_VALUE, Long.MAX_VALUE);

        //The test must have failed if it's being debugged, ergo there should exist a failing configuration

        assert (!this.failingConfigurations.isEmpty());

        Configuration failingOne = this.debugWithConfigurations(this.failingConfigurations);

        Logger.getGlobal().log(Level.SEVERE, "limits : " + limits.getLeft() + "  " + limits.getRight());

        if (failingOne != null) {
            return failingOne.toArgLine() + "\nDEBUG RESULTS FOR " + failingOne.testName + " AT: "
                + failingOne.getDebugPath();
        }

        // The seeds that failed with the full test-suite no longer fail
        // Searching for different seeds
        Set<Configuration> retryWOtherSeeds = this.createNewSeedsToRetry();
        failingOne = this.debugWithConfigurations(retryWOtherSeeds);

        if (failingOne != null) {
            return failingOne.toArgLine() + "\nDEBUG RESULTS FOR " + failingOne.testName + " AT: "
                + failingOne.getDebugPath();
        }

        return "cannot reproduce. may be flaky due to other causes";
    }

    private Set<Configuration> createNewSeedsToRetry() {
        Configuration someFailingConfig = this.failingConfigurations.iterator().next();
        int newSeed = someFailingConfig.seed * ConfigurationDefaults.SEED_FACTOR;
        Set<Configuration> retryWOtherSeeds = new LinkedHashSet<>();
        for (int i = 0; i < 10; i++) {
            Configuration newConfig = new Configuration(someFailingConfig.mode,
                    Utils.computeIthSeed(i, false, newSeed),
                    someFailingConfig.filter, someFailingConfig.start, someFailingConfig.end,
                    someFailingConfig.nondexDir, someFailingConfig.nondexJarDir,
                    someFailingConfig.testName, someFailingConfig.executionId);
            retryWOtherSeeds.add(newConfig);
        }
        return retryWOtherSeeds;
    }

    private Configuration debugWithConfigurations(Set<Configuration> failingConfigurations) {
        Configuration debConfig = null;
        for (Configuration config : failingConfigurations) {
            if (this.failsOnDry(config) != null) {
                Configuration failingConfig = this.startDebugBinary(config);

                // If debugged down to single choice point, then go ahead and return that
                if (failingConfig != null && failingConfig.numChoices() == 0) {
                    return failingConfig;
                }
                // Otherwise should go on until finding better one
                if (debConfig == null || failingConfig.hasFewerChoicePoints(debConfig)) {
                    debConfig = failingConfig;
                }
            }
        }

        return debConfig;
    }

    private Configuration failsOnDry(Configuration config) {
        return this.failsWithConfig(config, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public Configuration startDebugBinary(Configuration config) {
        long start = 0;
        long end = config.getInvocationCount();
        Configuration failingConfiguration = null;
        while (start < end) {
            Logger.getGlobal().log(Level.INFO, "Debugging binary for " + this.test + " " + start + " : " + end);

            long midPoint = (start + end) / 2;
            if ((failingConfiguration = this.failsWithConfig(config, start, midPoint)) != null) {
                end = midPoint;
                continue;
            } else if ((failingConfiguration = this.failsWithConfig(config, midPoint + 1, end)) != null) {
                start = midPoint + 1;
                continue;
            } else {
                Logger.getGlobal().log(Level.FINE, "Binary splitting did not work. Going to linear");
                failingConfiguration = this.startDebugLinear(config, start, end);
                break;
            }
        }
        if (failingConfiguration != null) {
            this.reportDebugInfo(failingConfiguration);
        }
        return failingConfiguration;
    }

    private Configuration reportDebugInfo(Configuration failingConfiguration) {
        return this.failsWithConfig(failingConfiguration, failingConfiguration.start, failingConfiguration.end, true);
    }

    public Configuration startDebugLinear(Configuration config, long start, long end) {
        Configuration failingConfiguration = null;
        long localStart = start;
        long localEnd = end;
        while (localStart < localEnd) {
            Logger.getGlobal().log(Level.INFO, "Debugging linear for " + this.test + " " + localStart + " : " + localEnd);

            if ((failingConfiguration = this.failsWithConfig(config, localStart, localEnd - 1)) != null) {
                localEnd = localEnd - 1;
                continue;
            } else if ((failingConfiguration = this.failsWithConfig(config, localStart + 1, localEnd)) != null) {
                localStart = localStart + 1;
                continue;
            } else {
                Logger.getGlobal().log(Level.FINE, "Refining did not work. Does not fail with linear.");
                break;
            }
        }
        return failingConfiguration;
    }

    private Configuration failsWithConfig(Configuration config, long start, long end) {
        return this.failsWithConfig(config, start, end, false);
    }

    private Configuration failsWithConfig(Configuration config, long start, long end, boolean print) {
        NonDexSurefireExecution execution = new NonDexSurefireExecution(config,
                    start, end, print, this.test, this.surefire, this.originalArgLine, this.mavenProject,
                    this.mavenSession, this.pluginManager);
        try {
            execution.run();
        } catch (Throwable thr) {
            return execution.getConfiguration();
        }
        return null;
    }
}
