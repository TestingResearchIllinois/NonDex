package edu.illinois.nondex.gradle.tasks;

import edu.illinois.nondex.common.ConfigurationDefaults;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NonDexClean extends DefaultTask {

    static final String NAME = "nondexClean";

    public static String getNAME() { return NAME; }

    public NonDexClean() {
        setDescription("Clean Nondex jar and directory");
        setGroup("NonDex");
    }

    @TaskAction
    void cleanNonDexFiles() {

        String userDirectory = getProject().getProjectDir().getAbsolutePath();

        Path nondexArtifactsPath = Paths.get(userDirectory, ConfigurationDefaults.DEFAULT_NONDEX_DIR);

        Path nondexJarPath = Paths.get(userDirectory,
                ConfigurationDefaults.DEFAULT_NONDEX_JAR_DIR,
                ConfigurationDefaults.INSTRUMENTATION_JAR);

        File artifactsDir = nondexArtifactsPath.toFile();
        File nondexJar = nondexJarPath.toFile();

        if (nondexJar.exists()) {
            delete(nondexJar);
        }

        if (artifactsDir.exists()) {
            delete(artifactsDir);
        }
    }

    void delete(File file) {
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                delete(childFile);
            }
        }
        file.delete();
    }
}
