package edu.illinois.nondex.gradle.plugin

import edu.illinois.nondex.common.ConfigurationDefaults
import edu.illinois.nondex.gradle.plugin.tasks.NonDexHelp
import edu.illinois.nondex.gradle.plugin.tasks.NonDexTest
import org.gradle.api.Plugin
import org.gradle.api.Project

class NonDexPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.configurations.create('nondexJava')
                .setVisible(true)
                .setTransitive(true)
                .setDescription('The nondex library to be used for this project')
                .defaultDependencies { dependencies ->
            dependencies.add(project.dependencies.create('edu.illinois:nondex-common:' + ConfigurationDefaults.VERSION))
            dependencies.add(project.dependencies.create('edu.illinois:nondex-instrumentation:' + ConfigurationDefaults.VERSION))
        }

        project.afterEvaluate {
            if (project.repositories.size() == 0) {
                project.repositories.addAll(project.buildscript.repositories.collect())
            }
        }

        project.tasks.create(NonDexTest.NAME, NonDexTest).init()
        project.tasks.create(NonDexHelp.NAME, NonDexHelp).init()
    }
}
