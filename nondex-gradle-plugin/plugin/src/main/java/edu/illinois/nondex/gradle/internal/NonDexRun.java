package edu.illinois.nondex.gradle.internal;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.Utils;
import edu.illinois.nondex.gradle.tasks.AbstractNonDexTest;
import org.gradle.api.internal.tasks.testing.JvmTestExecutionSpec;
import org.gradle.api.internal.tasks.testing.TestExecuter;
import org.gradle.wrapper.GradleUserHomeLookup;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static edu.illinois.nondex.gradle.constants.NonDexGradlePluginConstants.NONDEX_COMMON_SHA1;
import static edu.illinois.nondex.gradle.constants.NonDexGradlePluginConstants.NONDEX_VERSION;

public class NonDexRun extends CleanRun {

    private NonDexRun(TestExecuter<JvmTestExecutionSpec> delegate, JvmTestExecutionSpec originalSpec,
                      NonDexTestProcessor testResultProcessor, String nondexDir, AbstractNonDexTest nondexTestTask) {
        super(nondexTestTask, delegate, originalSpec, testResultProcessor, Utils.getFreshExecutionId(), nondexDir);
    }

    public NonDexRun(AbstractNonDexTest nondexTestTask, int seed, TestExecuter<JvmTestExecutionSpec> delegate,
                     JvmTestExecutionSpec originalSpec, NonDexTestProcessor testResultProcessor, String nondexDir, String nondexJarDir) {
        this(delegate, originalSpec, testResultProcessor, nondexDir, nondexTestTask);
        this.configuration = new Configuration(nondexTestTask.getNondexMode(), seed, Pattern.compile(nondexTestTask.getNondexFilter()),
                nondexTestTask.getNondexStart(), nondexTestTask.getNondexEnd(), nondexDir, nondexJarDir, null,
                this.executionId, Logger.getGlobal().getLoggingLevel());
        this.originalSpec = this.createJvmExecutionSpecWithArgs(this.setupArgline(), this.originalSpec);
    }

    public NonDexRun(Configuration configuration, AbstractNonDexTest nondexTestTask,
                     TestExecuter<JvmTestExecutionSpec> delegate, JvmTestExecutionSpec originalSpec, NonDexTestProcessor testResultProcessor) {
        this(delegate, originalSpec, testResultProcessor, configuration.nondexDir, nondexTestTask);
        this.configuration = configuration;
        this.originalSpec = this.createJvmExecutionSpecWithArgs(this.setupArgline(), this.originalSpec);
    }


    @Override
    protected List<String> setupArgline() {
        String pathToNondex = getPathToNonDexJar();
        List<String> argline = new LinkedList<>();
        if (!Utils.checkJDKBefore8()) {
            argline.add("--patch-module=java.base=" + pathToNondex);
            argline.add("--add-exports=java.base/edu.illinois.nondex.common=ALL-UNNAMED");
            argline.add("--add-exports=java.base/edu.illinois.nondex.shuffling=ALL-UNNAMED");
        } else {
            argline.add("-Xbootclasspath/p:" + pathToNondex);
        }
        argline.add("-D" + ConfigurationDefaults.PROPERTY_EXECUTION_ID + "=" + configuration.executionId);
        argline.add("-D" + ConfigurationDefaults.PROPERTY_SEED + "=" + configuration.seed);
        argline.add("-D" + ConfigurationDefaults.PROPERTY_MODE + "=" + configuration.mode);
        argline.add("-D" + ConfigurationDefaults.PROPERTY_START + "=" + configuration.start);
        argline.add("-D" + ConfigurationDefaults.PROPERTY_END + "=" + configuration.end);
        argline.add("-D" + ConfigurationDefaults.PROPERTY_PRINT_STACK + "=" + configuration.shouldPrintStackTrace);
        argline.add("-D" + ConfigurationDefaults.PROPERTY_NONDEX_DIR + "=" + configuration.nondexDir);
        argline.add("-D" + ConfigurationDefaults.PROPERTY_NONDEX_JAR_DIR + "=" + configuration.nondexJarDir);
        argline.add("-D" + ConfigurationDefaults.PROPERTY_LOGGING_LEVEL + "=" + configuration.loggingLevel);
        argline.add("-D" + ConfigurationDefaults.PROPERTY_FILTER + "=" + configuration.filter);
        if (configuration.testName != null) {
            argline.add("-Dtest=" + configuration.testName);
        }
        argline.addAll(super.setupArgline());
        return mergePatchModuleArguments(argline);
    }

    /**
     * Merges duplicate '--patch-module=java.base' arguments by combining their paths into one argument.
     * 
     * This method iterates through the provided list of arguments, identifies any duplicate '--patch-module=java.base'
     * entries, extracts and merges the paths, and constructs a single '--patch-module=java.base' argument with the 
     * merged paths. It also returns a new list containing all original arguments, but with the merged '--patch-module=java.base' 
     * argument if any duplicates were found.
     *
     * @param argline The list of arguments to process.
     * @return A new list of arguments with merged '--patch-module=java.base' arguments and all other arguments unchanged.
     */
    private List<String> mergePatchModuleArguments(List<String> argline) {
        List<String> mergedArgline = new LinkedList<>();
        Set<String> javaBasePaths = new LinkedHashSet<>();

        for (String arg : argline) {
            if (arg.startsWith("--patch-module=java.base=")) {
                // Extract paths from the --patch-module argument and merge them
                String[] paths = arg.split("=")[2].split(":");
                for (String path : paths) {
                    javaBasePaths.add(path);
                }
            } else {
                mergedArgline.add(arg);
            }
        }

        if (!javaBasePaths.isEmpty()) {
            String mergedPatchModule = "--patch-module=java.base=" + String.join(":", javaBasePaths);
            mergedArgline.add(mergedPatchModule);
        }
        return mergedArgline;
    }


    private String getPathToNonDexJar() {
        File gradleHomeLocation = GradleUserHomeLookup.gradleUserHome();
        // Gradle uses the SHA1 key of the file as the directory name
        String result = Paths.get(this.configuration.nondexJarDir,
                ConfigurationDefaults.INSTRUMENTATION_JAR) + File.pathSeparator + Paths.get(gradleHomeLocation.toString(),
                "caches", "modules-2", "files-2.1", "edu.illinois", "nondex-common", NONDEX_VERSION,
                NONDEX_COMMON_SHA1, "nondex-common-" + NONDEX_VERSION + ".jar");
        return result;
    }
}
