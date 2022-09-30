package de.larsgrefer.sass.embedded.functions;

import de.larsgrefer.sass.embedded.SassCompilationFailedException;
import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sass.embedded_protocol.EmbeddedSass.Value;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HostFunctionFactoryTest {

    private SassCompiler sassCompiler;

    @BeforeEach
    void setUp() throws IOException {
        sassCompiler = SassCompilerFactory.bundled();
    }

    @Test
    void fooFunction() throws IOException, SassCompilationFailedException {
        //language=SCSS
        String scss = ".h2 {\n" +
                "  color: echo('foo');\n" +
                "  size: my-add(1, 2);\n" +
                "}";

        List<HostFunction> hostFunctions = HostFunctionFactory.allSassFunctions(Foo.class);

        hostFunctions.forEach(sassCompiler::registerFunction);

        String s = sassCompiler.compileScssString(scss).getCss();

        assertThat(s).contains("Hello World");
        assertThat(s).contains("3");
    }

    @Test
    void ofCallable() throws Throwable {
        HostFunction hf = HostFunctionFactory.ofLambda("echo", () -> "Hello World");

        Value invoke = hf.invoke(Collections.emptyList());

        assertThat(invoke.getString().getText()).isEqualTo("Hello World");
    }

    @Test
    void ofFunction() throws Throwable {
        HostFunction hf = HostFunctionFactory.ofLambda("foo", String.class, String::length);

        Value invoke = hf.invoke(Collections.singletonList(Value.newBuilder()
                .setString(Value.String.newBuilder()
                        .setText("bar-baz")
                        .build())
                .build()));

        assertThat(invoke.getNumber().getValue()).isEqualTo(7);
    }

    @Test
    void ofLambda() throws Throwable {
        //language=SCSS
        String scss = ".h2 {\n" +
                "  color: echo('foo');\n" +
                "  size: my-add(1, 2);\n" +
                "}";

        sassCompiler.registerFunction(HostFunctionFactory.ofLambda("echo", String.class, Foo::echo));
        sassCompiler.registerFunction(HostFunctionFactory.ofLambda("my-add", Integer.class, Integer.class, Integer::sum));

        String s = sassCompiler.compileScssString(scss).getCss();

        assertThat(s).contains("Hello World");
        assertThat(s).contains("3");
    }

    @SuppressWarnings("SameReturnValue")
    public static class Foo {

        @SassFunction
        public static int my_add(int i, int j) {
            return i + j;
        }

        @SassFunction
        public static String echo(String s) {
            return "Hello World";
        }
    }
}