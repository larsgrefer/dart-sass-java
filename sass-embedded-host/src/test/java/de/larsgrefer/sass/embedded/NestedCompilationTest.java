package de.larsgrefer.sass.embedded;

import de.larsgrefer.sass.embedded.functions.HostFunction;
import de.larsgrefer.sass.embedded.functions.HostFunctionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class NestedCompilationTest {

    SassCompiler sassCompiler;

    @BeforeEach
    void setUp() throws IOException {
        sassCompiler = SassCompilerFactory.bundled();
    }

    @AfterEach
    void tearDown() throws IOException {
        sassCompiler.close();
    }

    @Test
    void nestedCompilation() throws SassCompilationFailedException, IOException {

        HostFunction dummyFunction = HostFunctionFactory.ofLambda("dummy", () -> {
            CompileSuccess compileSuccess = sassCompiler.compileScssString("body {color: red; size: 1+2}");

            System.out.println(compileSuccess.getCss());

            return compileSuccess.getCss();
        });

        sassCompiler.registerFunction(dummyFunction);

        CompileSuccess compileSuccess = sassCompiler.compileScssString("body { color: dummy()}");

        System.out.println(compileSuccess.getCss());
    }
}
