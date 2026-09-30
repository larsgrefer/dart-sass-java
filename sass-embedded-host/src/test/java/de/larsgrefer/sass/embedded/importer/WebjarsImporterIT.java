package de.larsgrefer.sass.embedded.importer;

import de.larsgrefer.sass.embedded.BootstrapUtil;
import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WebjarsImporterIT {

    @TestFactory
    Stream<DynamicTest> boostrap() throws IOException {

        return Stream.of(
                "scss/bootstrap.scss",
                "scss/bootstrap",
                "bootstrap/scss/bootstrap.scss",
                "bootstrap/scss/bootstrap",
                String.format("bootstrap/%s/scss/bootstrap.scss", BootstrapUtil.getBoostrapVersion()),
                String.format("bootstrap/%s/scss/bootstrap", BootstrapUtil.getBoostrapVersion())
        ).map(name -> DynamicTest.dynamicTest("import " + name, () -> {
            String scss = "@import '" + name + "';";

            String css;

            try (SassCompiler sassCompiler = SassCompilerFactory.bundled()) {
                sassCompiler.registerImporter(new WebjarsImporter().autoCanonicalize());
                css = sassCompiler.compileScssString(scss).getCss();
            }

            assertThat(css).contains("red");
        }));
    }
}