package edu.illinois.nondex.gradle.plugin

import edu.illinois.nondex.gradle.plugin.tasks.NonDexHelp;
import org.gradle.api.Plugin;
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin;


class NonDexPlugin implements Plugin<Project> {
    static final String NONDEX_GROUP = 'nondex'

    @Override
    void apply(Project project) {
        NonDexHelp help = project.tasks.create(NonDexHelp.NAME, NonDexHelp)
        help.group = NONDEX_GROUP
        help.description = NonDexHelp.DESC
        project.plugins.withType(JavaPlugin) {
            project.plugins.apply(NonDexJavaPlugin)
        }
    }
}
