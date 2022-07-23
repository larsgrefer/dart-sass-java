package de.larsgrefer.sass.embedded.spring;

import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class ExampleApp {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApp.class, args);
    }

    @Bean
    public SassCompiler sassCompiler() throws IOException {
        return SassCompilerFactory.bundled();
    }

    @Bean
    public SassResourceTransformer sassResourceTransformer(SassCompiler sassCompiler) {
        return new SassResourceTransformer(sassCompiler);
    }

    @Bean
    public SassResourceResolver sassResourceResolver(SassCompiler sassCompiler) {
        return new SassResourceResolver(sassCompiler);
    }

}
