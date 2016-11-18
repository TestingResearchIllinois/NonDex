package edu.illinois.nondex.gradle.plugin.tasks

import edu.illinois.nondex.gradle.plugin.NonDexJavaPlugin
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.testing.Test

class NonDexTest extends Test {
    static final String NAME = "nondexTest"
    static final String DESC = "Test with NonDex"

    void init(Configuration config) {
        project.extensions.create("nondexTest", NonDexTestExtension)
        project.extensions.nondexTest.outPath = project.buildDir.absolutePath + "/out.jar"
        dependsOn "genOutJar"

        doFirst {
            def args = "-Xbootclasspath/p:" + project.extensions.nondexTest.outPath + ":" + project.extensions.nondexTest.commonPath
            println args
            jvmArgs args
        }
    }
}
