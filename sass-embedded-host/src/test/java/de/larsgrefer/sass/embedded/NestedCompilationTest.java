package de.larsgrefer.sass.embedded;

import com.sass_lang.embedded_protocol.OutboundMessage;
import de.larsgrefer.sass.embedded.functions.HostFunction;
import de.larsgrefer.sass.embedded.functions.HostFunctionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sass_lang.embedded_protocol.OutboundMessage.CompileResponse.CompileSuccess;

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
            OutboundMessage.CompileResponse compileSuccess = sassCompiler.compileScssString("body {color: red; size: 1+2}");

            System.out.println(compileSuccess.getSuccess().getCss());

            return compileSuccess.getSuccess().getCss();
        });

        sassCompiler.registerFunction(dummyFunction);

        OutboundMessage.CompileResponse compileSuccess = sassCompiler.compileScssString("body { color: dummy()}");

        System.out.println(compileSuccess.getSuccess().getCss());
    }
}
