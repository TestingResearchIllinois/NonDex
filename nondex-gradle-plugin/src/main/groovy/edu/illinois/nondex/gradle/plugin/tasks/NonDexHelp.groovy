package edu.illinois.nondex.gradle.plugin.tasks

import org.codehaus.groovy.reflection.ReflectionUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class NonDexHelp extends DefaultTask {
    static final String NAME = "nondexHelp";

    void init() {
    	setDescription("Display NonDex Help")
        setGroup("NonDex")

    	doFirst {
    		println "\nNonDex Gradle Plugin"
        	println ReflectionUtils.getCallingClass(0).getResourceAsStream("/nondexHelp.txt").text
    	}
    }
}
