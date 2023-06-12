package de.larsgrefer.sass.embedded.importer;

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
    private SassCompiler sassCompiler;

    @BeforeEach
    public void setUp() throws IOException {
        sassCompiler = SassCompilerFactory.bundled();
        sassCompiler.registerImporter(new WebjarsImporter().autoCanonicalize());
    }

    @AfterEach
    public void tearDown() throws IOException {
        sassCompiler.close();
    }

    @TestFactory
    Stream<DynamicTest> boostrap() {

        return Stream.of(
                "scss/bootstrap.scss",
                "scss/bootstrap"
        ).map(name -> DynamicTest.dynamicTest("import " + name, () -> {
            String scss = "@import '" + name + "';";

            String css = sassCompiler.compileScssString(scss).getSuccess().getCss();

            assertThat(css).contains("red");
        }));
    }
}