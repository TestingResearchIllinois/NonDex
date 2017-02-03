package edu.illinois.nondex.gradle.plugin

import edu.illinois.nondex.gradle.plugin.tasks.NonDexHelp
import edu.illinois.nondex.gradle.plugin.tasks.NonDexTest
import org.gradle.api.Plugin
import org.gradle.api.Project

class NonDexPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.tasks.create(NonDexTest.NAME, NonDexTest).init()
        project.tasks.create(NonDexHelp.NAME, NonDexHelp).init()
    }
}
