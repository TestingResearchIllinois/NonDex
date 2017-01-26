package edu.illinois.nondex.gradle.plugin.tasks

import edu.illinois.nondex.common.ConfigurationDefaults
import edu.illinois.nondex.common.Utils
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.testing.Test

class NonDexTest extends Test {
    static final String NAME = "nondexTest"
    static final String DESC = "Test with NonDex"

    void init(Configuration config) {
        project.extensions.create("nondexTest", NonDexTestExtension)
        project.extensions.nondexTest.outPath = project.buildDir.absolutePath + File.pathSeparator + "out.jar"
        dependsOn "generateOutputJar"

        testLogging {
	    exceptionFormat = 'full'
	}
        doFirst {
            def args = "-Xbootclasspath/p:" + project.extensions.nondexTest.outPath + ":" + project.extensions.nondexTest.commonPath
            jvmArgs args, "-D" + ConfigurationDefaults.PROPERTY_EXECUTION_ID + "=" + Utils.getFreshExecutionId()
            println getJvmArgs()
        }
    }
}
