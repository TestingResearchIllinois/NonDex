package edu.illinois.nondex.gradle.plugin.tasks

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.testing.Test

class NonDexTest extends Test {
    static final String NAME = "nondexTest"

    void init() {
        setDescription("NonDexTest Description")
        setGroup("NonDex")

        testLogging {
	       exceptionFormat 'full'
        }

        doFirst {
            String commonPath = project.configurations.nondexJava.resolve().find {it.name.startsWith("nondex-common")}.absolutePath
            String outPath = project.buildDir.absolutePath + File.separator + "out.jar"

            edu.illinois.nondex.instr.Main.main(outPath)

            def args = "-Xbootclasspath/p:" + outPath + File.pathSeparator + commonPath
            jvmArgs args, "-D" + edu.illinois.nondex.common.ConfigurationDefaults.PROPERTY_EXECUTION_ID + "=" + edu.illinois.nondex.common.Utils.getFreshExecutionId()
            println getJvmArgs()
        }
    }
}
