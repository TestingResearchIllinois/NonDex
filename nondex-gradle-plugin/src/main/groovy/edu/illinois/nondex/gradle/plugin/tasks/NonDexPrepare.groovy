package edu.illinois.nondex.gradle.plugin.tasks

import org.gradle.api.DefaultTask


class NonDexTestExtension {
    String commonPath
    String instrumentationPath
    String outPath
}

class NonDexPrepare extends DefaultTask {
    void init() {

        doLast {
            project.configurations.nondexJava.resolve().each {
                if (it.name.startsWith("nondex-common")) {
                    project.extensions.nondexTest.commonPath = it.absolutePath
                } else if (it.name.startsWith("nondex-instrumentation")) {
                    project.extensions.nondexTest.instrumentationPath = it.absolutePath
                }
            }
        }
    }
}
