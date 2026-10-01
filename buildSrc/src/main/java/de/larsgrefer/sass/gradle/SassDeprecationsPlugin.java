package de.larsgrefer.sass.gradle;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskProvider;

public abstract class SassDeprecationsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        NamedDomainObjectProvider<Configuration> config = project.getConfigurations().register("deprecationsYml");

        TaskProvider<GenerateDeprecations> generateDeprecations = project.getTasks().register("generateDeprecations", GenerateDeprecations.class, genDep -> {
            genDep.getGeneratedSourcesDir().set(project.getLayout().getBuildDirectory().dir("generated/sources/sass/main/java"));
            genDep.getInputFiles().from(config);
        });

        project.getPlugins().withId("java", javaPlugin -> {

            project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().named("main").configure(main -> {
                main.getJava().srcDir(generateDeprecations.map(GenerateDeprecations::getGeneratedSourcesDir));
            });


        });
    }
}
