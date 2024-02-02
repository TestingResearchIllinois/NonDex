package edu.illinois.nondex.gradle.plugin;

import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.api.Project;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NonDexGradlePluginTest {
    @Test
    void pluginRegistersATask() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("edu.illinois.nondex");
        assertNotNull(project.getTasks().findByName("nondexTest"));
        assertNotNull(project.getTasks().findByName("nondexClean"));
        assertNotNull(project.getTasks().findByName("nondexDebug"));
    }
}
