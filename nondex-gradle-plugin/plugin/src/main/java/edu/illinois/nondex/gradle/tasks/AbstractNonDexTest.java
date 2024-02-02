package edu.illinois.nondex.gradle.tasks;

import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Level;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Mode;
import edu.illinois.nondex.common.Utils;
import edu.illinois.nondex.instr.Instrumenter;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.provider.DefaultProperty;
import org.gradle.api.internal.provider.PropertyHost;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskInstantiationException;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.testing.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public abstract class AbstractNonDexTest extends Test {

    protected int nondexSeed = Integer.parseInt(ConfigurationDefaults.DEFAULT_SEED_STR);
    @Option(option = ConfigurationDefaults.PROPERTY_SEED, description = "The seed that is used for randomization during shuffling.")
    public void setNondexSeed(String seed) {
        this.nondexSeed = Integer.parseInt(seed);
    }
    @Input
    public int getNondexSeed() { return nondexSeed; }

    protected Mode nondexMode = ConfigurationDefaults.DEFAULT_MODE;
    @Option(option = ConfigurationDefaults.PROPERTY_MODE, description = "The degree/level of shuffling that should be carried out.")
    public void setNondexMode(Mode mode) {
        this.nondexMode = mode;
    }
    @Input
    public Mode getNondexMode() { return nondexMode; }

    protected String nondexFilter =  ConfigurationDefaults.DEFAULT_FILTER;
    @Option(option = ConfigurationDefaults.PROPERTY_FILTER, description = "Regex filter used to whitelist the sources of non-determinism to explore.")
    public void setNondexFilter(String filter) {
        this.nondexFilter = filter;
    }
    @Input
    public String getNondexFilter() { return nondexFilter; }

    protected long nondexStart = ConfigurationDefaults.DEFAULT_START;
    @Option(option = ConfigurationDefaults.PROPERTY_START, description = "Starting point of the range of invocations to use during the debug phase. The minimum value is 0 and the maximum can be obtained from first running the nondex phase. If start and end have the same value, then only the invocation at that value is shuffled.")
    public void setNondexStart(String start) {
        this.nondexStart = Long.parseLong(start);
    }
    @Input
    public long getNondexStart() { return nondexStart; }

    protected long nondexEnd = ConfigurationDefaults.DEFAULT_END;
    @Option(option = ConfigurationDefaults.PROPERTY_END, description = "Ending point of the range of invocations to use during the debug phase. The minimum value is 0 and the maximum can be obtained from first running the nondex phase. If start and end have the same value, then only the invocation at that value is shuffled.")
    public void setNondexEnd(String end) {
        this.nondexEnd = Long.parseLong(end);
    }
    @Input
    public long getNondexEnd() { return nondexEnd; }

    protected int nondexRunsWithoutShuffling = ConfigurationDefaults.DEFAULT_NUM_RUNS_WITHOUT_SHUFFLING;
    @Option(option = ConfigurationDefaults.PROPERTY_NUM_RUNS_WITHOUT_SHUFFLING, description ="The number of clean test runs without NonDex shuffling. This feature may be useful if tests are flaky (NOD / NIO) by themselves.")
    public void setNondexRunsWithoutShuffling(String numRunsWithoutShuffling) {
        this.nondexRunsWithoutShuffling = Integer.parseInt(numRunsWithoutShuffling);
    }
    @Input
    public int getNondexRunsWithoutShuffling() { return nondexRunsWithoutShuffling; }

    protected int nondexRuns = ConfigurationDefaults.DEFAULT_NUM_RUNS;
    @Option(option = ConfigurationDefaults.PROPERTY_NUM_RUNS, description = "The number of seeds to use for shuffle. NonDex will obtain other seeds apart from the specified (or default) seed from the current run and some internal factor.")
    public void setNondexRuns(String numRuns) {
        this.nondexRuns = Integer.parseInt(numRuns);
    }
    @Input
    public int getNondexRuns() { return nondexRuns; }

    protected boolean nondexRerun = false;
    @Option(option = ConfigurationDefaults.PROPERTY_RERUN, description = "Set this to \"true\" to rerun multiple times with only the specified (or default) seed. The number of reruns is equal to numRuns.")
    public void setNondexRerun(boolean rerun) {
        //this.nondexRerun = rerun.equalsIgnoreCase("true");
        this.nondexRerun = rerun;
    }
    @Input
    public boolean getNondexRerun() { return nondexRerun; }

    protected String nondexExecutionId = ConfigurationDefaults.PROPERTY_DEFAULT_EXECUTION_ID;
    @Option(option = ConfigurationDefaults.PROPERTY_EXECUTION_ID, description = "Unique ID for the current nondex execution.")
    public void setNondexExecutionId(String executionId) {
        this.nondexExecutionId = executionId;
    }
    @Input
    public String getNondexExecutionId() { return nondexExecutionId; }

    protected String nondexRunId = ConfigurationDefaults.PROPERTY_DEFAULT_RUN_ID;
    @Option(option = ConfigurationDefaults.PROPERTY_RUN_ID, description = "Select which run to perform debugging on. Default is the latest.")
    public void setNondexRunId(String runId) {
        this.nondexRunId = runId;
    }
    @Input
    public String getNondexRunId() { return nondexRunId; }

    protected String nondexLoggingLevel = ConfigurationDefaults.DEFAULT_LOGGING_LEVEL;
    @Option(option = ConfigurationDefaults.PROPERTY_LOGGING_LEVEL, description = "Same as the levels defined in java.util.logging.Level.")
    public void setNondexLoggingLevel(String loggingLevel) {
        this.nondexLoggingLevel = loggingLevel;
    }
    @Input
    public String getNondexLoggingLevel() { return nondexLoggingLevel; }

    @Internal
    public Property<Boolean> getDryRun() {
        DefaultProperty<Boolean> dryRun = new DefaultProperty<Boolean>(PropertyHost.NO_OP, Boolean.class);
        dryRun.set(false);
        return dryRun;
    }

    @Internal
    protected Test testTask;
    Test getTestTask() { return testTask; }

    @Internal
    protected List<String> originalArgLine;
    public List<String> getOriginalArgLine() { return originalArgLine; }

    protected void setUpNondexTesting() {
        Logger.getGlobal().setLoggingLevel(Level.parse(this.nondexLoggingLevel));
        String rtPathStr = "";
        if (Utils.checkJDK8()) {
            Path rtPath;
            rtPath = Utils.getRtJarLocation();
            if (rtPath == null) {
                Logger.getGlobal().log(Level.SEVERE, "Cannot find the rt.jar!");
                throw new TaskInstantiationException("Cannot find the rt.jar!");
            }
            rtPathStr = rtPath.toString();
        }

        try {
            File fileForJar = Paths.get(getProject().getProjectDir().getAbsolutePath(),
                    ConfigurationDefaults.DEFAULT_NONDEX_JAR_DIR).toFile();

            fileForJar.mkdirs();
            Instrumenter.instrument(rtPathStr, Paths.get(fileForJar.getAbsolutePath(),
                    ConfigurationDefaults.INSTRUMENTATION_JAR).toString());
        } catch (IOException | NoSuchAlgorithmException exc) {
            exc.printStackTrace();
        }

        this.testTask = (Test) getProject().getTasks().findByName("test");

        if (testTask == null) {
            Logger.getGlobal().log(Level.SEVERE, "Cannot find a test task for this project");
            throw new TaskInstantiationException("Cannot find a test task for this project");
        }

        this.originalArgLine = testTask.getAllJvmArgs();
    }

    @Override
    public FileTree getCandidateClassFiles() {
        FileTree candidateFiles = null;
        try {
            candidateFiles = super.getCandidateClassFiles();
        } catch (NullPointerException e) {
            Logger.getGlobal().log(Level.SEVERE, "Cannot find tests for project " + getProject().getPath());
        }
        return candidateFiles;
    }
}
