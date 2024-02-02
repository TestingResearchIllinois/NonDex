package edu.illinois.nondex.gradle.tasks;

import com.google.common.collect.LinkedListMultimap;
import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Level;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Utils;
import com.google.common.collect.ListMultimap;
import edu.illinois.nondex.gradle.internal.DebugExecuter;
import org.apache.commons.lang3.tuple.Pair;
import org.gradle.api.internal.tasks.testing.JvmTestExecutionSpec;
import org.gradle.api.internal.tasks.testing.TestExecuter;
import org.gradle.api.tasks.testing.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NonDexDebug extends AbstractNonDexTest {

    static final String NAME = "nondexDebug";
    private final List<String> executions = new LinkedList<>();
    private final ListMultimap<String, Configuration> testsFailing = LinkedListMultimap.create();
    private DebugExecuter debugExecuter;

    public static String getNAME() { return NAME; }

    public NonDexDebug() {
        setDescription("Debug with NonDex");
        setGroup("NonDex");
        DebugExecuter debugExecuter = this.createNondexDebugExecuter();
        this.setNondexDebugAsTestExecuter(debugExecuter);
        this.debugExecuter = debugExecuter;
    }

    @Override
    public void executeTests() {
        super.setUpNondexTesting();
        this.parseExecutions();
        this.parseTests();

        Map<String, String> testToRepro = new HashMap<>();

        for (String test : this.testsFailing.keySet()) {
            DebugTask debugging = new DebugTask(test, this.testsFailing.get(test));
            String repro = debugging.debug();
            testToRepro.put(test, repro);
        }

        if (!this.testsFailing.isEmpty()) {
            Logger.getGlobal().log(Level.WARNING, "*********");
            for (Map.Entry<String, String> test : testToRepro.entrySet()) {
                Logger.getGlobal().log(Level.WARNING,"REPRO for " + test.getKey() + ":" + String.format("%n")
                        + "gradle nondexTest " + test.getValue());
            }
        } else {
            Logger.getGlobal().log(Level.INFO, "There were no tests that previously failed with NonDex shuffling");
        }
    }

    private void parseTests() {
        for (String execution : this.executions) {
            if (execution.startsWith("clean_")) {
                continue;
            }
            Properties props = Utils.openPropertiesFrom(Paths.get(getProject().getProjectDir().getAbsolutePath(),
                    ConfigurationDefaults.DEFAULT_NONDEX_DIR, execution,
                    ConfigurationDefaults.CONFIGURATION_FILE));
            Configuration config = Configuration.parseArgs(props);
            for (String test : config.getFailedTests()) {
                this.testsFailing.put(test, config);
            }
        }
    }

    private void parseExecutions() {
        File run = Paths.get(getProject().getProjectDir().getAbsolutePath(),
                        ConfigurationDefaults.DEFAULT_NONDEX_DIR, getNondexRunId()).toFile();

        try (BufferedReader br = new BufferedReader(new FileReader(run))) {
            String line;
            while ((line = br.readLine()) != null) {
                this.executions.add(line.trim());
            }
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Could not open run file to parse executions", ex);
        }
    }

    private void runSpecifiedTests(String test) {
        List<String> testFilter = new ArrayList<>();
        testFilter.add(test);
        this.setTestNameIncludePatterns(testFilter);
    }

    private DebugExecuter createNondexDebugExecuter() {
        try {
            Method getExecuter = Test.class.getDeclaredMethod("createTestExecuter");
            getExecuter.setAccessible(true);
            TestExecuter<JvmTestExecutionSpec> delegate = (TestExecuter<JvmTestExecutionSpec>) getExecuter.invoke(this);
            return new DebugExecuter(this, delegate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setNondexDebugAsTestExecuter(DebugExecuter nondexDebugExecuter) {
        try {
            Method setTestExecuter = Test.class.getDeclaredMethod("setTestExecuter", TestExecuter.class);
            setTestExecuter.setAccessible(true);
            setTestExecuter.invoke(this, nondexDebugExecuter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class DebugTask {

        private String test;
        private final List<Configuration> failingConfigurations;

        private DebugTask(String test, List<Configuration> failingConfigurations) {
            this.test = test;
            this.failingConfigurations = failingConfigurations;
        }

        private String debug() {
            if (this.failingConfigurations.isEmpty()) {
                throw new RuntimeException("Tests need to first fail with NonDex to be debugged");
            }

            String singleTest = this.test;
            String testClass = this.test.substring(0, test.lastIndexOf('.'));
            for (String testFilterPatterns : new String[] { singleTest, testClass, "" }) {
                // Tests may have parenthesis or square brackets in the name
                // We need to remove them so that Gradle can find the test
                int parenthesisIndex = testFilterPatterns.indexOf('(');
                if (parenthesisIndex != -1) {
                    testFilterPatterns = testFilterPatterns.substring(0, parenthesisIndex);
                }
                int bracketIndex = testFilterPatterns.indexOf('[');
                if (bracketIndex != -1) {
                    testFilterPatterns = testFilterPatterns.substring(0, bracketIndex);
                }
                this.test = testFilterPatterns;
                NonDexDebug.this.runSpecifiedTests(this.test);
                String result = this.tryDebugSeeds();
                if (result != null) {
                    return result;
                }
            }
            return "cannot reproduce. may be flaky due to other causes";
        }

        private String tryDebugSeeds() {
            List<Configuration> debuggedOnes = this.debugWithConfigurations(this.failingConfigurations);

            if (debuggedOnes.size() > 0) {
                return makeResultString(debuggedOnes);
            }

            // The seeds that failed with the full test-suite no longer fail
            // Searching for different seeds
            Logger.getGlobal().log(Level.FINE, "TRYING NEW SEEDS");
            List<Configuration> retryWithOtherSeeds = this.createNewSeedsToRetry();
            debuggedOnes = this.debugWithConfigurations(retryWithOtherSeeds);

            if (debuggedOnes.size() > 0) {
                return makeResultString(debuggedOnes);
            }

            return null;
        }

        private String makeResultString(List<Configuration> debuggedOnes) {
            StringBuilder sb = new StringBuilder();
            for (Configuration config : debuggedOnes) {
                if (config == null) {
                    continue;
                }
                sb.append(config.toArgLine());
                sb.append("\nDEBUG RESULTS FOR ");
                sb.append(config.testName);
                sb.append(" AND SEED: ");
                sb.append(config.seed);
                sb.append(" AT: ");
                sb.append(config.getDebugPath());
                sb.append('\n');
            }
            return sb.toString();
        }

        private List<Configuration> createNewSeedsToRetry() {
            Configuration someFailingConfig = this.failingConfigurations.iterator().next();
            int newSeed = someFailingConfig.seed * ConfigurationDefaults.SEED_FACTOR;
            List<Configuration> retryWithOtherSeeds = new LinkedList<>();
            for (int i = 0; i < 10; i++) {
                Configuration newConfig = new Configuration(someFailingConfig.mode,
                        Utils.computeIthSeed(i, false, newSeed),
                        someFailingConfig.filter, someFailingConfig.start, someFailingConfig.end,
                        someFailingConfig.nondexDir, someFailingConfig.nondexJarDir,
                        someFailingConfig.testName, someFailingConfig.executionId,
                        someFailingConfig.loggingLevel);
                retryWithOtherSeeds.add(newConfig);
            }
            return retryWithOtherSeeds;
        }

        private List<Configuration> debugWithConfigurations(List<Configuration> failingConfigurations) {
            List<Configuration> allDebuggedConfigs = new LinkedList<>();
            for (Configuration config : failingConfigurations) {
                Configuration dryConfig;
                if ((dryConfig = this.failsOnDry(config)) != null) {
                    // Get all debugged points and just add them to the full list
                    List<Configuration> debuggedConfigs = this.startDebugBinary(dryConfig);
                    allDebuggedConfigs.addAll(debuggedConfigs);
                }
            }

            return allDebuggedConfigs;
        }

        private Configuration failsOnDry(Configuration config) {
            return this.failsWithConfig(config, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        private List<Configuration> startDebugBinary(Configuration config) {
            List<Configuration> allFailingConfigurations = new LinkedList<>();

            List<Pair<Pair<Long, Long>, Configuration>> pairs = new LinkedList<>();
            pairs.add(Pair.of(Pair.of(0L, (long) config.getInvocationCount()), config));

            Configuration failingConfiguration;
            while (pairs.size() > 0) {
                Pair<Pair<Long, Long>, Configuration> pair = pairs.remove(0);
                Pair<Long, Long> range = pair.getLeft();
                failingConfiguration = pair.getRight();
                long start = range.getLeft();
                long end = range.getRight();

                if (start < end) {
                    Logger.getGlobal().log(Level.INFO, "Debugging binary for " + this.test + " " + start + " : " + end);

                    boolean binarySuccess = false;
                    long midPoint = (start + end) / 2;
                    if ((failingConfiguration = this.failsWithConfig(config, start, midPoint)) != null) {
                        pairs.add(Pair.of(Pair.of(start, midPoint), failingConfiguration));
                        binarySuccess = true;
                    }
                    if ((failingConfiguration = this.failsWithConfig(config, midPoint + 1, end)) != null) {
                        pairs.add(Pair.of(Pair.of(midPoint + 1, end), failingConfiguration));
                        binarySuccess = true;
                    }

                    // If both halves fail, try the entire range
                    if (!binarySuccess) {
                        Logger.getGlobal().log(Level.SEVERE, "Binary splitting did not work. Going to linear");
                        allFailingConfigurations.addAll(this.startDebugLinear(config, start, end));
                    }
                } else {
                    // Since start <= end is always true, this branch means start == end, so reached end
                    if (failingConfiguration != null) {
                        allFailingConfigurations.add(this.reportDebugInfo(failingConfiguration));
                    }
                }
            }

            return allFailingConfigurations;
        }

        private Configuration reportDebugInfo(Configuration failingConfiguration) {
            return this.failsWithConfig(failingConfiguration, failingConfiguration.start, failingConfiguration.end, true);
        }

        private List<Configuration> startDebugLinear(Configuration config, long start, long end) {
            List<Configuration> allFailingConfigurations = new LinkedList<>();

            List<Pair<Pair<Long, Long>, Configuration>> pairs = new LinkedList<>();
            pairs.add(Pair.of(Pair.of(start, end),
                    config));

            Configuration failingConfiguration;
            while (pairs.size() > 0) {
                Pair<Pair<Long, Long>, Configuration> pair = pairs.remove(0);
                Pair<Long, Long> range = pair.getLeft();
                failingConfiguration = pair.getRight();
                long localStart = range.getLeft();
                long localEnd = range.getRight();

                if (localStart < localEnd) {
                    Logger.getGlobal().log(Level.INFO, "Debugging linear for " + this.test + " "
                            + localStart + " : " + localEnd);

                    boolean found = false;
                    if ((failingConfiguration = this.failsWithConfig(config, localStart, localEnd - 1)) != null) {
                        pairs.add(Pair.of(Pair.of(localStart, localEnd - 1), failingConfiguration));
                        found = true;
                    }
                    if ((failingConfiguration = this.failsWithConfig(config, localStart + 1, localEnd)) != null) {
                        pairs.add(Pair.of(Pair.of(localStart + 1, localEnd), failingConfiguration));
                        found = true;
                    }

                    if (!found) {
                        Logger.getGlobal().log(Level.FINE, "Refining did not work. Does not fail with linear on range "
                                + localStart + " : " + localEnd + ".");
                    }
                } else {
                    // Since start <= end is always true, this branch means start == end, so reached end
                    if (failingConfiguration != null) {
                        allFailingConfigurations.add(this.reportDebugInfo(failingConfiguration));
                    }
                }
            }
            return allFailingConfigurations;
        }

        private Configuration failsWithConfig(Configuration config, long start, long end) {
            return this.failsWithConfig(config, start, end, false);
        }

        private Configuration failsWithConfig(Configuration config, long start, long end, boolean print) {
            Configuration configCopy = new Configuration(config.mode, config.seed, config.filter, start,
                    end, config.nondexDir, config.nondexJarDir, this.test, Utils.getFreshExecutionId(),
                    Logger.getGlobal().getLoggingLevel(), print);
            NonDexDebug.this.debugExecuter.setConfiguration(configCopy);
            NonDexDebug.super.executeTests();
            if (!configCopy.getFailedTests().isEmpty()) {
                return configCopy;
            }
            return null;
        }
    }
}
