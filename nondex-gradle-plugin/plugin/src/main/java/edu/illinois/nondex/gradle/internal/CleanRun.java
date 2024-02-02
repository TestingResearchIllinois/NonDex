package edu.illinois.nondex.gradle.internal;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.Level;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Utils;
import edu.illinois.nondex.gradle.tasks.AbstractNonDexTest;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.tasks.testing.JvmTestExecutionSpec;
import org.gradle.api.internal.tasks.testing.TestExecuter;
import org.gradle.api.internal.tasks.testing.TestFramework;
import org.gradle.process.JavaForkOptions;
import org.gradle.util.GradleVersion;
import org.gradle.util.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CleanRun {

    protected Configuration configuration;
    protected final String executionId;
    protected JvmTestExecutionSpec originalSpec;
    protected AbstractNonDexTest nondexTestTask;
    private final TestExecuter<JvmTestExecutionSpec> delegate;
    private NonDexTestProcessor nondexTestProcessor;

    protected CleanRun(AbstractNonDexTest nondexTestTask, TestExecuter<JvmTestExecutionSpec> delegate, JvmTestExecutionSpec originalSpec,
                       NonDexTestProcessor nondexTestProcessor, String executionId, String nondexDir) {
        this.nondexTestTask = nondexTestTask;
        this.delegate = delegate;
        this.nondexTestProcessor = nondexTestProcessor;
        this.executionId = executionId;
        this.configuration = new Configuration(executionId, nondexDir);
        this.originalSpec = this.createJvmExecutionSpecWithArgs(setupArgline(), originalSpec);
    }

    public CleanRun(AbstractNonDexTest nondexTestTask, TestExecuter<JvmTestExecutionSpec> delegate, JvmTestExecutionSpec originalSpec,
                    NonDexTestProcessor nondexTestProcessor, String nondexDir) {
        this(nondexTestTask, delegate, originalSpec, nondexTestProcessor, "clean_" + Utils.getFreshExecutionId(), nondexDir);
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public NonDexTestProcessor run() {
        Logger.getGlobal().log(Level.CONFIG, this.configuration.toString());
        this.delegate.execute(this.originalSpec, this.nondexTestProcessor);
        this.setFailures();
        return this.nondexTestProcessor;
    }

    private void setFailures() {
        Set<String> failingTests = this.nondexTestProcessor.getFailingTests();
        this.configuration.setFailures(failingTests);
    }

    protected JvmTestExecutionSpec createJvmExecutionSpecWithArgs(List<String> args, JvmTestExecutionSpec originalSpec) {
        JavaForkOptions option = originalSpec.getJavaForkOptions();
        option.setAllJvmArgs(args);
        GradleVersion curGradleVersion = GradleVersion.current().getBaseVersion();
        try {
            if (curGradleVersion.compareTo(GradleVersion.version("8.0")) >= 0) {
                return JvmTestExecutionSpec.class.getConstructor(new Class[]{
                    TestFramework.class,
                    Iterable.class,
                    Iterable.class,
                    FileTree.class,
                    Boolean.class,
                    FileCollection.class,
                    String.class,
                    Path.class,
                    Long.class,
                    JavaForkOptions.class,
                    Integer.class,
                    Set.class,
                    Boolean.class
                }).newInstance(originalSpec.getTestFramework(),
                    originalSpec.getClasspath(),
                    originalSpec.getModulePath(),
                    originalSpec.getCandidateClassFiles(),
                    originalSpec.isScanForTestClasses(),
                    originalSpec.getTestClassesDirs(),
                    originalSpec.getPath(),
                    originalSpec.getIdentityPath(),
                    originalSpec.getForkEvery(),
                    option,
                    originalSpec.getMaxParallelForks(),
                    originalSpec.getPreviousFailedTestClasses(),
                    originalSpec.getTestIsModule());
            } else if (curGradleVersion.compareTo(GradleVersion.version("6.4")) >= 0) {
                return JvmTestExecutionSpec.class.getConstructor(new Class[]{
                    TestFramework.class,
                    Iterable.class,
                    Iterable.class,
                    FileTree.class,
                    Boolean.class,
                    FileCollection.class,
                    String.class,
                    Path.class,
                    Long.class,
                    JavaForkOptions.class,
                    Integer.class,
                    Set.class
                }).newInstance(originalSpec.getTestFramework(),
                    originalSpec.getClasspath(),
                    originalSpec.getModulePath(),
                    originalSpec.getCandidateClassFiles(),
                    originalSpec.isScanForTestClasses(),
                    originalSpec.getTestClassesDirs(),
                    originalSpec.getPath(),
                    originalSpec.getIdentityPath(),
                    originalSpec.getForkEvery(),
                    option,
                    originalSpec.getMaxParallelForks(),
                    originalSpec.getPreviousFailedTestClasses());
            } else {
                return JvmTestExecutionSpec.class.getConstructor(new Class[]{
                    TestFramework.class,
                    Iterable.class,
                    FileTree.class,
                    Boolean.class,
                    FileCollection.class,
                    String.class,
                    Path.class,
                    Long.class,
                    JavaForkOptions.class,
                    Integer.class,
                    Set.class
                }).newInstance(originalSpec.getTestFramework(),
                    originalSpec.getClasspath(),
                    originalSpec.getCandidateClassFiles(),
                    originalSpec.isScanForTestClasses(),
                    originalSpec.getTestClassesDirs(),
                    originalSpec.getPath(),
                    originalSpec.getIdentityPath(),
                    originalSpec.getForkEvery(),
                    option,
                    originalSpec.getMaxParallelForks(),
                    originalSpec.getPreviousFailedTestClasses());
            } 
        } catch (Exception e) {
            return originalSpec;
        }
    }

    protected List<String> setupArgline() {
        List<String> argline = new LinkedList<>();
        argline.addAll(nondexTestTask.getOriginalArgLine());
        return argline;
    }
}
