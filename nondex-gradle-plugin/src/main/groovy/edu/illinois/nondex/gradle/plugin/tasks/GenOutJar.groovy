package edu.illinois.nondex.gradle.plugin.tasks

import org.gradle.api.tasks.Exec

class GenOutJar extends Exec {
    void init() {
        dependsOn "nondexPrepare"
        doFirst {
            commandLine 'java', '-jar', project.extensions.nondexTest.instrumentationPath, project.extensions.nondexTest.outPath
        }
    }
}
