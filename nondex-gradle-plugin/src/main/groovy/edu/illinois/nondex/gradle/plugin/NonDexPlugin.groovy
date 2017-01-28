package edu.illinois.nondex.gradle.plugin

import edu.illinois.nondex.gradle.plugin.tasks.NonDexHelp
import edu.illinois.nondex.gradle.plugin.tasks.NonDexTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class NonDexPlugin implements Plugin<Project> {
    public static final String NONDEX_JAVA_CONFIGURATION_NAME = 'nondexJava'
    public static final String NONDEX_JAVA_VERSION = edu.illinois.nondex.common.ConfigurationDefaults.VERSION;

    @Override
    void apply(Project project) {
        project.configurations.create(NONDEX_JAVA_CONFIGURATION_NAME)
                .setVisible(false)
                .setTransitive(true)
                .setDescription('The nondex library to be used for this project')
                .defaultDependencies { dependencies ->
            dependencies.add(project.dependencies.create('edu.illinois:nondex-common:' + NONDEX_JAVA_VERSION))
            dependencies.add(project.dependencies.create('edu.illinois:nondex-instrumentation:' + NONDEX_JAVA_VERSION))
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
