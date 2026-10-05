package de.larsgrefer.sass.gradle;

import io.freefair.gradle.plugins.okhttp.OkHttpPlugin;
import io.freefair.gradle.plugins.okhttp.tasks.DownloadFile;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.jvm.tasks.ProcessResources;

@SuppressWarnings("NewApi")
public abstract class SassDeprecationsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.getPlugins().apply(OkHttpPlugin.class);

        TaskProvider<DownloadFile> downloadDeprecationsYaml = project.getTasks().register("downloadDeprecationsYaml", DownloadFile.class, downloadFile -> {
            downloadFile.getUrl().convention(project.getProviders().gradleProperty("embeddedProtocolVersion")
                    .map("https://raw.githubusercontent.com/sass/sass/refs/tags/embedded-protocol-%s/spec/deprecations.yaml"::formatted));
            downloadFile.getOutputFile().set(project.getLayout().getBuildDirectory().file("sass/spec/deprecations.yaml"));
        });

        TaskProvider<GenerateDeprecations> generateDeprecations = project.getTasks().register("generateDeprecations", GenerateDeprecations.class, genDep -> {
            genDep.getGeneratedSourcesDir().set(project.getLayout().getBuildDirectory().dir("generated/sources/sass/main/java"));
            genDep.getDeprecationsYamlFile().set(downloadDeprecationsYaml.flatMap(DownloadFile::getOutputFile));
        });

        project.getPlugins().withId("java", javaPlugin -> {

            project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().named("main").configure(main -> {
                main.getJava().srcDir(generateDeprecations.map(GenerateDeprecations::getGeneratedSourcesDir));
            });

            project.getTasks().named("processResources", ProcessResources.class).configure( pr -> {
                pr.into("sass/spec", f -> f.from(downloadDeprecationsYaml));
            });

        });
    }
}
