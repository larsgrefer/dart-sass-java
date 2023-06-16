package de.larsgrefer.sass.embedded.logging;

import de.larsgrefer.sass.embedded.CompileSuccess;
import de.larsgrefer.sass.embedded.SassCompilationFailedException;
import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

abstract class LoggingHandlerTest {

    //language=SCSS
    public static String warnScss = "$known-prefixes: webkit, moz, ms, o;\n" +
            "\n" +
            "@mixin prefix($property, $value, $prefixes) {\n" +
            "  @each $prefix in $prefixes {\n" +
            "    @if not index($known-prefixes, $prefix) {\n" +
            "      @warn \"Unknown prefix #{$prefix}.\";\n" +
            "    }\n" +
            "\n" +
            "    -#{$prefix}-#{$property}: $value;\n" +
            "  }\n" +
            "  #{$property}: $value;\n" +
            "}\n" +
            "\n" +
            ".tilt {\n" +
            "  // Oops, we typo'd \"webkit\" as \"wekbit\"!\n" +
            "  @include prefix(transform, rotate(15deg), wekbit ms);\n" +
            "}\n";

    //language=SCSS
    public static String debugScss = "@mixin inset-divider-offset($offset, $padding) {\n" +
            "  $divider-offset: (2 * $padding) + $offset;\n" +
            "  @debug \"divider offset: #{$divider-offset}\";\n" +
            "\n" +
            "  margin-left: $divider-offset;\n" +
            "  width: calc(100% - #{$divider-offset});\n" +
            "}\n" +
            "\n" +
            ".tilt {\n" +
            "  @include inset-divider-offset(2px, 3px);\n" +
            "}\n";

    private SassCompiler sassCompiler;

    @BeforeEach
    void setUp() throws IOException {
        sassCompiler = SassCompilerFactory.bundled();
        sassCompiler.setLoggingHandler(createLoggingHandler());
    }

    protected abstract LoggingHandler createLoggingHandler();

    @AfterEach
    void tearDown() throws IOException {
        sassCompiler.close();
    }

    @Test
    void testWarn() throws SassCompilationFailedException, IOException {
        CompileSuccess compileSuccess = sassCompiler.compileScssString(warnScss);
    }

    @Test
    void testDebug() throws SassCompilationFailedException, IOException {
        CompileSuccess compileSuccess = sassCompiler.compileScssString(debugScss);
    }
}