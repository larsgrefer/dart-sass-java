package de.larsgrefer.sass.embedded.importer;

import de.larsgrefer.sass.embedded.SassCompilationFailedException;
import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ClasspathImporterIT {

    private SassCompiler sassCompiler;

    @BeforeEach
    public void setUp() throws IOException {
        sassCompiler = SassCompilerFactory.bundled();
        sassCompiler.registerImporter(new ClasspathImporter().autoCanonicalize());
    }

    @AfterEach
    public void tearDown() throws IOException {
        sassCompiler.close();
    }

    @TestFactory
    Stream<DynamicTest> bar() {

        return Stream.of(
                "foo/bar.scss",
                "foo/bar"
        ).map(name -> DynamicTest.dynamicTest("import " + name, () -> {
            String scss = "@import '" + name + "';";

            String css = sassCompiler.compileScssString(scss).getCss();

            assertThat(css).contains("red");
        }));
    }

    @TestFactory
    Stream<DynamicTest> baz() {

        return Stream.of(
                "foo/_baz.scss",
                "foo/_baz",
                "foo/baz.scss",
                "foo/baz"
        ).map(name -> DynamicTest.dynamicTest("import " + name, () -> {
            String scss = "@import '" + name + "';";

            String css = sassCompiler.compileScssString(scss).getCss();

            assertThat(css).contains("green");
        }));
    }

    @Test
    void interClasspathImport() throws SassCompilationFailedException, IOException {
        String css = sassCompiler.compileScssString("@import 'foo/classpathImport';").getCss();

        assertThat(css).contains("green");
    }
    @Test
    void interClasspathImport_fromJar() throws SassCompilationFailedException, IOException {
        String css = sassCompiler.compileScssString("@import 'META-INF/resources/webjars/bootstrap/5.1.0/scss/bootstrap.scss';").getCss();

        assertThat(css).contains("green");
    }
}
