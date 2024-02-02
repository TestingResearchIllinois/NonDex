package edu.illinois.nondex.gradle.internal;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.gradle.tasks.AbstractNonDexTest;
import org.gradle.api.internal.tasks.testing.JvmTestExecutionSpec;
import org.gradle.api.internal.tasks.testing.TestExecuter;
import org.gradle.api.internal.tasks.testing.TestResultProcessor;

public class DebugExecuter implements TestExecuter<JvmTestExecutionSpec> {

    private final AbstractNonDexTest nondexTestTask;
    private final TestExecuter<JvmTestExecutionSpec> delegate;
    private Configuration configuration;

    public DebugExecuter(AbstractNonDexTest nondexTestTask, TestExecuter<JvmTestExecutionSpec> delegate) {
        this.nondexTestTask = nondexTestTask;
        this.delegate = delegate;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void execute(JvmTestExecutionSpec spec, TestResultProcessor testResultProcessor) {
        NonDexTestProcessor nondexTestProcessor = new NonDexTestProcessor(testResultProcessor);
        NonDexRun nondexRun  = new NonDexRun(configuration, nondexTestTask, this.delegate, spec, nondexTestProcessor);
        nondexRun.run();
    }

    @Override
    public void stopNow() {
        delegate.stopNow();
    }
}
