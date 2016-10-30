package edu.illinois.nondex.gradle.plugin.tasks

import org.codehaus.groovy.reflection.ReflectionUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class NonDexHelp extends DefaultTask {
    static final String NAME = "nondexHelp";
    static final String DESC = "Help for NonDex";

    @TaskAction
    def help() {
        println "\nNonDex Gradle Plugin"
        println ReflectionUtils.getCallingClass(0).getResourceAsStream("/nondexHelp.txt").text
    }
}
