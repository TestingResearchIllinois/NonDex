package edu.illinois.nondex.gradle.internal;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Level;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Utils;
import edu.illinois.nondex.gradle.tasks.AbstractNonDexTest;
import org.gradle.api.internal.tasks.testing.JvmTestExecutionSpec;
import org.gradle.api.internal.tasks.testing.TestExecuter;
import org.gradle.api.internal.tasks.testing.TestResultProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NonDexTestExecuter implements TestExecuter<JvmTestExecutionSpec> {

    private final TestExecuter<JvmTestExecutionSpec> delegate;
    private final List<NonDexRun> nonDexRuns = new LinkedList<>();
    private final List<CleanRun> cleanRuns = new ArrayList<>();
    private final AbstractNonDexTest nondexTestTask;

    public NonDexTestExecuter(AbstractNonDexTest nondexTestTask, TestExecuter<JvmTestExecutionSpec> delegate) {
        this.nondexTestTask = nondexTestTask;
        this.delegate = delegate;
    }

    @Override
    public void execute(JvmTestExecutionSpec spec, TestResultProcessor testResultProcessor) {
        Logger.getGlobal().log(Level.INFO, "The original argline is: " + nondexTestTask.getOriginalArgLine());
        NonDexTestProcessor cleanRunProcessor = new NonDexTestProcessor(testResultProcessor);

        for (int currentRun = 0; currentRun < nondexTestTask.getNondexRunsWithoutShuffling(); ++currentRun) {
            cleanRunProcessor.reset(currentRun + 1 == nondexTestTask.getNondexRunsWithoutShuffling());
            CleanRun cleanRun = new CleanRun(nondexTestTask, this.delegate, spec, cleanRunProcessor,
                    nondexTestTask.getProject().getProjectDir().getAbsolutePath() + File.separator + ConfigurationDefaults.DEFAULT_NONDEX_DIR);
            this.cleanRuns.add(cleanRun);
            cleanRun.run();
        }

        NonDexTestProcessor nondexRunProcessor = new NonDexTestProcessor(testResultProcessor);
        for (int currentRun = 0; currentRun < nondexTestTask.getNondexRuns(); ++currentRun) {
            nondexRunProcessor.reset(currentRun + 1 == nondexTestTask.getNondexRuns());
            NonDexRun nondexRun = new NonDexRun(nondexTestTask, Utils.computeIthSeed(currentRun,
                    nondexTestTask.getNondexRerun(), nondexTestTask.getNondexSeed()),
                    this.delegate, spec, nondexRunProcessor,
                    nondexTestTask.getProject().getProjectDir() + File.separator + ConfigurationDefaults.DEFAULT_NONDEX_DIR,
                    nondexTestTask.getProject().getProjectDir() + File.separator + ConfigurationDefaults.DEFAULT_NONDEX_JAR_DIR);
            this.nonDexRuns.add(nondexRun);
            nondexRun.run();
            this.writeCurrentRunInfo(nondexRun);
        }

        for (CleanRun cleanRun : this.cleanRuns) {
            this.writeCurrentRunInfo(cleanRun);
            this.postProcessExecutions(cleanRun);
        }

        Configuration config = this.nonDexRuns.get(0).getConfiguration();

        boolean hasFailingTests = printAndGetSummary(config);

        try {
            Files.copy(config.getRunFilePath(), config.getLatestRunFilePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Could not copy current run info to latest", ex);
        }

        Logger.getGlobal().log(Level.INFO, "[NonDex] The id of this run is: " + config.executionId);

        if (hasFailingTests) {
            throw new RuntimeException("There were failing tests");
        }
    }

    @Override
    public void stopNow() {
        delegate.stopNow();
    }

    private void postProcessExecutions(CleanRun cleanRun) {
        Collection<String> failedInClean = cleanRun.getConfiguration().getFailedTests();
        for (NonDexRun nondexRun : this.nonDexRuns) {
            nondexRun.getConfiguration().filterTests(failedInClean);
        }
    }

    private void writeCurrentRunInfo(CleanRun run) {
        try {
            Files.write(this.nonDexRuns.get(0).getConfiguration().getRunFilePath(),
                    (run.getConfiguration().executionId + String.format("%n")).getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            Logger.getGlobal().log(Level.SEVERE, "Cannot write execution id to current run file", ex);
        }
    }

    private boolean printAndGetSummary(Configuration config) {
        Set<String> allFailures = new LinkedHashSet<>();
        Map<String, Integer> countsOfFailingTestsWithoutShuffling = new LinkedHashMap<>();
        boolean failsWithoutShuffling = false;
        Logger.getGlobal().log(Level.INFO, "NonDex SUMMARY:");
        for (CleanRun run : this.nonDexRuns) {
            this.printExecutionResults(allFailures, run);
        }
        for (int i = 0; i < cleanRuns.size(); ++i) {
            CleanRun cleanRun = this.cleanRuns.get(i);
            Collection<String> failedTests = cleanRun.getConfiguration().getFailedTests();
            if (!failedTests.isEmpty()) {
                failsWithoutShuffling = true;
                if (nondexTestTask.getNondexRunsWithoutShuffling() == 1) {
                    Logger.getGlobal().log(Level.INFO, "The following tests failed in the clean run:");
                } else {
                    Logger.getGlobal().log(Level.INFO, "In run #" + String.valueOf(i + 1)
                            + " without NonDex shuffling, the following tests failed:");
                }
                for (String test : failedTests) {
                    Logger.getGlobal().log(Level.WARNING, test);
                    int count = countsOfFailingTestsWithoutShuffling.containsKey(test)
                            ? countsOfFailingTestsWithoutShuffling.get(test) : 0;
                    countsOfFailingTestsWithoutShuffling.put(test, count + 1);
                }
            }
        }
        if (failsWithoutShuffling && (nondexTestTask.getNondexRunsWithoutShuffling() > 1)) {
            Logger.getGlobal().log(Level.INFO, "------------------");
            Logger.getGlobal().log(Level.INFO, "The following tests are failing in clean runs without NonDex shuffling");
            for (Map.Entry<String, Integer> entry : countsOfFailingTestsWithoutShuffling.entrySet()) {
                Logger.getGlobal().log(Level.INFO, "Test: " + entry.getKey());
                Logger.getGlobal().log(Level.INFO, "Fails in " + entry.getValue() + " out of "
                        + nondexTestTask.getNondexRunsWithoutShuffling() + " clean runs.");
            }
        } else if (!failsWithoutShuffling) {
            Logger.getGlobal().log(Level.INFO, "All tests pass without NonDex shuffling");
        }

        Logger.getGlobal().log(Level.INFO, "####################");
        Logger.getGlobal().log(Level.INFO, "Across all seeds:");
        for (String test : allFailures) {
            Logger.getGlobal().log(Level.INFO, test);
        }

        this.generateHtml(allFailures, config);

        return failsWithoutShuffling || !allFailures.isEmpty();
    }

    private void generateHtml(Set<String> allFailures, Configuration config) {
        String head = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Test Results</title>"
                + "<style>"
                + "table { border-collapse: collapse; width: 100%; }"
                + "th { height: 50%; }"
                + "th, td { padding: 10px; text-align: left; }"
                + "tr:nth-child(even) {background-color:#f2f2f2;}"
                + ".x { color: red; font-size: 150%;}"
                + ".✓ { color: green; font-size: 150%;}"
                + "</style>"
                + "</head>";
        StringBuilder html = new StringBuilder(head + "<body>" + "<table>");

        html.append("<thead><tr>").append("<th>Test Name</th>");
        for (int iter = 0; iter < this.nonDexRuns.size(); iter++) {
            html.append("<th>").append(this.nonDexRuns.get(iter).getConfiguration().seed).append("</th>");
        }
        html.append("</tr></thead>").append("<tbody>");
        for (String failure : allFailures) {
            html.append("<tr><td>").append(failure).append("</td>");
            for (CleanRun run : this.nonDexRuns) {
                boolean testDidFail = false;
                for (String test : run.getConfiguration().getFailedTests()) {
                    if (test.equals(failure)) {
                        testDidFail = true;
                    }
                }
                if (testDidFail) {
                    html.append("<td class=\"x\">&#10006;</td>");
                } else {
                    html.append("<td class=\"✓\">&#10004;</td>");
                }
            }
            html.append("</tr>");
        }
        html.append("</tbody></table></body></html>");

        File nondexDir = config.getNondexDir().toFile();
        File htmlFile = new File(nondexDir, "test_results.html");
        try {
            PrintWriter htmlPrinter = new PrintWriter(htmlFile);
            htmlPrinter.print(html);
            htmlPrinter.close();
        } catch (FileNotFoundException ex) {
            Logger.getGlobal().log(Level.INFO, "File Missing.  But that shouldn't happen...");
        }
        Logger.getGlobal().log(Level.INFO, "Test results can be found at: ");
        Logger.getGlobal().log(Level.INFO, "file://" + htmlFile.getPath());
    }

    private void printExecutionResults(Set<String> allFailures, CleanRun run) {
        Logger.getGlobal().log(Level.INFO, "*********");
        Logger.getGlobal().log(Level.INFO, "gradle nondexTest" + run.getConfiguration().toArgLine());
        Collection<String> failedTests = run.getConfiguration().getFailedTests();
        if (failedTests.isEmpty()) {
            Logger.getGlobal().log(Level.INFO, "No Test Failed with this configuration.");
        }
        for (String test : failedTests) {
            allFailures.add(test);
            Logger.getGlobal().log(Level.WARNING, test);
        }
        Logger.getGlobal().log(Level.INFO, "*********");
    }
}
