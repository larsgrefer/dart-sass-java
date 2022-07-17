package de.larsgrefer.sass.embedded.spring;

import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class ExampleApp {

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
