package edu.illinois.nondex.gradle.plugin;

import edu.illinois.nondex.gradle.tasks.NonDexClean;
import edu.illinois.nondex.gradle.tasks.NonDexDebug;
import edu.illinois.nondex.gradle.tasks.NonDexTest;
import org.gradle.api.Project;
import org.gradle.api.Plugin;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

import static edu.illinois.nondex.gradle.constants.NonDexGradlePluginConstants.NONDEX_VERSION;

public class NonDexGradlePlugin implements Plugin<Project> {

    public void apply(Project project) {
        project.getTasks().create(NonDexTest.getNAME(), NonDexTest.class);
        project.getTasks().create(NonDexClean.getNAME(), NonDexClean.class);
        project.getTasks().create(NonDexDebug.getNAME(), NonDexDebug.class);
        downloadNonDexCommonJar(project.getRootProject());
    }

    private void downloadNonDexCommonJar(Project project) {
        Configuration config = project.getConfigurations().create("downloadNonDexCommonJar");
        project.getDependencies().add(config.getName(), "edu.illinois:nondex-common:" + NONDEX_VERSION);
        MavenArtifactRepository mavenCentral = project.getRepositories().mavenCentral();
        if (project.getRepositories().contains(mavenCentral)) {
            config.resolve();
        } else {
            project.getRepositories().add(mavenCentral);
            config.resolve();
            project.getRepositories().remove(mavenCentral);
        }
        project.getConfigurations().remove(config);
    }
}
