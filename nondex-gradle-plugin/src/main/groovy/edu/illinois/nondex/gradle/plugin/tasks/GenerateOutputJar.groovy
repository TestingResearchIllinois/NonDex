package edu.illinois.nondex.gradle.plugin.tasks

import org.gradle.api.tasks.Exec

class GenerateOutputJar extends Exec {
    void init() {
        dependsOn "nondexPrepare"
        doFirst {
			edu.illinois.nondex.instr.Main.main([project.extensions.nondexTest.instrumentationPath, project.extensions.nondexTest.outPath])
        }
    }
}
