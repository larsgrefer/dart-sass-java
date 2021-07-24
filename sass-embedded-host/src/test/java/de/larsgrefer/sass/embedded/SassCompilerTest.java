package de.larsgrefer.sass.embedded;

import de.larsgrefer.sass.embedded.functions.HostFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sass.embedded_protocol.EmbeddedSass;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SassCompilerTest {

    private SassCompiler sassCompiler;

    @BeforeEach
    void init() throws IOException {
        sassCompiler = SassCompilerFactory.bundled();
    }

    @AfterEach
    void cleanup() throws Exception {
        sassCompiler.close();
    }

    @Test
    void getVersion() throws IOException {
        EmbeddedSass.OutboundMessage.VersionResponse version = sassCompiler.getVersion();

        assertThat(version).isNotNull();
    }

    @Test
    void compileString() throws Exception {
        String sass = ".foo { .bar { color : #ffffff; @warn 'haha';}}";

        String s = null;
        for (int i = 0; i < 100; i++) {
            s = sassCompiler.compileScssString(sass).getCss();
        }
        System.out.println(s);
    }

    @Test
    void compileFileToString() throws SassCompilationFailedException, IOException {
        sassCompiler.setOutputStyle(EmbeddedSass.InboundMessage.CompileRequest.OutputStyle.COMPRESSED);
        String css = sassCompiler.compileFile(new File("src/test/resources/foo/bar.scss")).getCss();

        assertThat(css).contains("color:red");
    }

    @Test
    void customFunction() throws Exception {
        String scss = ".foo { .bar { color : foo(#ffffff);}}";

        HostFunction sassFunction = new HostFunction("foo", Collections.singletonList(new HostFunction.Argument("col", null))) {
            @Override
            public EmbeddedSass.Value invoke(List<EmbeddedSass.Value> arguments) throws Throwable {
                return EmbeddedSass.Value.newBuilder()
                        .setRgbColor(EmbeddedSass.Value.RgbColor.newBuilder()
                                .setRed(255)
                                .setBlue(25)
                                .setAlpha(1d)
                                .build())
                        .build();
            }
        };

        sassCompiler.registerFunction(sassFunction);

        String css = sassCompiler.compileScssString(scss).getCss();
        System.out.println(css);

    }

    @Test
    void customFunction_error() throws Exception {
        String scss = ".foo { .bar { color : foo(#ffffff);}}";

        HostFunction sassFunction = new HostFunction("foo", Collections.singletonList(new HostFunction.Argument("col", null))) {
            @Override
            public EmbeddedSass.Value invoke(List<EmbeddedSass.Value> arguments) throws Throwable {
                throw new RuntimeException("bazinga");
            }
        };

        sassCompiler.registerFunction(sassFunction);
        SassCompilationFailedException e = assertThrows(SassCompilationFailedException.class, () -> {
            String css = sassCompiler.compileScssString(scss).getCss();
        });

        assertThat(e.getMessage()).contains("bazinga");
    }

    @Test
    void sourceMapPaths() throws SassCompilationFailedException, IOException {
        String sass = ".foo { .bar { color : foo(#ffffff);}}";

        EmbeddedSass.InboundMessage.CompileRequest.StringInput string = EmbeddedSass.InboundMessage.CompileRequest.StringInput.newBuilder()
                .setSource(sass)
                .build();

        EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess compileSuccess = sassCompiler.compileString(string, EmbeddedSass.InboundMessage.CompileRequest.OutputStyle.EXPANDED);

        System.out.println(compileSuccess.getSourceMap());

        File file = new File("src/test/resources/foo/bar.scss");

        EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess compileSuccess1 = sassCompiler.compileFile(file);

        System.out.println(compileSuccess1.getSourceMap());
    }

}