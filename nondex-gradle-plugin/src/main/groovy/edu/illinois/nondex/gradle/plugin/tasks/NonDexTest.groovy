package edu.illinois.nondex.gradle.plugin.tasks

import edu.illinois.nondex.common.ConfigurationDefaults
import edu.illinois.nondex.common.Utils
import edu.illinois.nondex.instr.Main
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.testing.Test

class NonDexTest extends Test {
    static final String NAME = "nondexTest"

    void init() {
        setDescription("Test with NonDex")
        setGroup("NonDex")

        testLogging {
            exceptionFormat 'full'
        }

        doFirst {
            String commonPath = project.configurations.nondexJava.resolve().find {it.name.startsWith("nondex-common")}.absolutePath
            String outPath = project.buildDir.absolutePath + File.separator + "out.jar"

            Main.main(outPath)

            def args = "-Xbootclasspath/p:" + outPath + File.pathSeparator + commonPath
            jvmArgs args, "-D" + ConfigurationDefaults.PROPERTY_EXECUTION_ID + "=" + Utils.getFreshExecutionId()
            println getJvmArgs()
        }
    }
}
