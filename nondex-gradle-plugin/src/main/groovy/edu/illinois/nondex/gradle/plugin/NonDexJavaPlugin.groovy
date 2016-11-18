package edu.illinois.nondex.gradle.plugin

import edu.illinois.nondex.gradle.plugin.tasks.GenOutJar
import edu.illinois.nondex.gradle.plugin.tasks.NonDexPrepare
import edu.illinois.nondex.gradle.plugin.tasks.NonDexTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration


class NonDexJavaPlugin implements Plugin<Project> {
    public static final String NONDEX_JAVA_CONFIGURATION_NAME = 'nondexJava'
    public static final String NONDEX_JAVA_VERSION = '1.1.1'

    @Override
    void apply(Project project) {
        project.configurations.create(NONDEX_JAVA_CONFIGURATION_NAME)
                .setVisible(false)
                .setTransitive(true)
                .setDescription('The nondex library to be used for this project')

        project.afterEvaluate {
            if (project.repositories.size() == 0) {
                project.repositories.addAll(project.buildscript.repositories.collect())
            }
        }

        Configuration config = project.configurations[NONDEX_JAVA_CONFIGURATION_NAME]
        config.defaultDependencies { dependencies ->
            dependencies.add(project.dependencies.create('edu.illinois:nondex-common:' + NONDEX_JAVA_VERSION))
            dependencies.add(project.dependencies.create('edu.illinois:nondex-instrumentation:' + NONDEX_JAVA_VERSION))
        }

        project.tasks.create('nondexPrepare', NonDexPrepare).init()

        project.tasks.create('genOutJar', GenOutJar).init()

        NonDexTest nonDexTest = project.tasks.create(NonDexTest.NAME, NonDexTest)
        nonDexTest.group = NonDexPlugin.NONDEX_GROUP
        nonDexTest.description = NonDexTest.DESC
        nonDexTest.init(config)
    }
}
