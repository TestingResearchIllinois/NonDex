package edu.illinois.nondex.gradle.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NonDexGradlePluginFunctionalTest {

    private File projectDirectory;

    @AfterEach
    public void cleanUp() {
        GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("clean", "nondexClean")
            .build();
        File gradleCache = new File(projectDirectory, ".gradle");
        deleteAllFiles(gradleCache);
    }

    private void deleteAllFiles(File currentFile) {
        if (!currentFile.isDirectory()) {
            currentFile.delete();
            return;
        }
        for (File childFiles : currentFile.listFiles()) {
            deleteAllFiles(childFiles);
        }
        currentFile.delete();
    }

    @Test
    public void testCleanWhenProjectHasWhiteSpace_thenBuildSuccess() {
        projectDirectory = new File("src/functionalTest/resources/clean-it with-whitespace-in-path");
        GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("nondexTest", "nondexClean")
            .build();

        File nondexDir = new File(projectDirectory, ".nondex");
        Assertions.assertFalse(nondexDir.exists());
    }

    @Test
    public void testCleanWhenProjectHasNoWhiteSpace_thenBuildSuccess() {
        projectDirectory = new File("src/functionalTest/resources/clean-it");
        GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("nondexTest", "nondexClean")
            .build();

        File nondexDir = new File(projectDirectory, ".nondex");
        Assertions.assertFalse(nondexDir.exists());
    }

    @Test
    public void testNondexWhenProjectHasFailingTest_thenBuildFailure() {
        projectDirectory = new File("src/functionalTest/resources/failing-it");
        GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("nondexTest")
            .buildAndFail();

        File nondexDir = new File(projectDirectory, ".nondex");
        Assertions.assertTrue(nondexDir.isDirectory());
        Assertions.assertEquals(0, (nondexDir.listFiles().length - 2) % 5);
    }

    @Test
    public void testNondexWithExistingArgline_thenBuildSuccess() {
        projectDirectory = new File("src/functionalTest/resources/argline-it");
        GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("nondexTest", "nondexClean")
            .build();

        File nondexDir = new File(projectDirectory, ".nondex");
        Assertions.assertFalse(nondexDir.exists());
    }

    @Test
    public void testNondexWithSimpleProjectWithHashSet_thenBuildFailure() {
        projectDirectory = new File("src/functionalTest/resources/simple-it");
        GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("nondexTest")
            .buildAndFail();

        File nondexDir = new File(projectDirectory, ".nondex");
        Assertions.assertTrue(nondexDir.isDirectory());
        Assertions.assertEquals(0, (nondexDir.listFiles().length - 2) % 5);
    }

    @Test
    public void testNondexWithSimpleMultiModuleProject_thenBuildSuccess() {
        projectDirectory = new File("src/functionalTest/resources/simple-multimodule-it");
        GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("nondexTest")
            .build();

        File nondexDir = new File(projectDirectory, "module1/.nondex");
        Assertions.assertTrue(nondexDir.isDirectory());
        nondexDir = new File(projectDirectory, "module2/.nondex");
        Assertions.assertTrue(nondexDir.isDirectory());
    }

    @Test
    public void testNondexComprehensvieWithFailingTests_thenBuildFailure() throws IOException {
        projectDirectory = new File("src/functionalTest/resources/comprehensive-it");
        GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("nondexTest")
            .buildAndFail();

        File nondexDir = new File(projectDirectory, ".nondex");
        Assertions.assertTrue(nondexDir.isDirectory());
        for (File f : nondexDir.listFiles()) {
            File failures = new File(f, "failures");
            if (failures.exists()) {
                if (f.getName().startsWith("clean")) {
                    // in the clean phase all tests should fail, meaning at least 40
                    Assertions.assertTrue(Files.readAllLines(failures.toPath(), Charset.defaultCharset()).size() > 40);
                } else {
                    // in the nondex phase with shuffling all tests should pass, meaning no lines
                    Assertions.assertTrue(Files.readAllLines(failures.toPath(), Charset.defaultCharset()).isEmpty());
                }
            }
        }
    }

    @Test
    public void testExcludedTestsWithNondex_thenBuildSuccess() {
        projectDirectory = new File("src/functionalTest/resources/excluded-tests-it");
        GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("nondexTest")
            .build();
        
        File nondexDir = new File(projectDirectory, "module1/.nondex");
        Assertions.assertTrue(nondexDir.isDirectory());
        nondexDir = new File(projectDirectory, "module2/.nondex");
        Assertions.assertTrue(nondexDir.isDirectory());
    }
}
