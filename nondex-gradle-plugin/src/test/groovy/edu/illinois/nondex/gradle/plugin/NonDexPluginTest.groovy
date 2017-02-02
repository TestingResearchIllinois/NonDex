package edu.illinois.nondex.gradle.plugin

import edu.illinois.nondex.gradle.plugin.tasks.NonDexHelp
import edu.illinois.nondex.gradle.plugin.tasks.NonDexTest
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue


class NonDexPluginTest {
    @Test
    public void testAddTasksToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'edu.illinois.nondex'

        assertTrue(project.tasks.nondexHelp instanceof NonDexHelp)
        assertTrue(project.tasks.nondexTest instanceof NonDexTest)
    }
}
