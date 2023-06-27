package de.larsgrefer.sass.embedded;

import com.sass_lang.embedded_protocol.InboundMessage;
import com.sass_lang.embedded_protocol.OutboundMessage;
import com.sass_lang.embedded_protocol.OutputStyle;
import com.sass_lang.embedded_protocol.Value;
import de.larsgrefer.sass.embedded.functions.HostFunction;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static de.larsgrefer.sass.embedded.BootstrapUtil.getBoostrapVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SassCompilerTest {

    private SassCompiler sassCompiler;

    @BeforeEach
    void init() throws IOException {
        sassCompiler = SassCompilerFactory.bundled();
        sassCompiler.setSourceMapIncludeSources(true);
        sassCompiler.setGenerateSourceMaps(true);
    }

    @AfterEach
    void cleanup() throws Exception {
        sassCompiler.close();
    }

    @Test
    void getVersion() throws IOException {
        OutboundMessage.VersionResponse version = sassCompiler.getVersion();

        assertThat(version).isNotNull();
        System.out.println(version);
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
        sassCompiler.setOutputStyle(OutputStyle.COMPRESSED);
        CompileSuccess compileSuccess = sassCompiler.compileFile(new File("src/test/resources/foo/bar.scss"));
        String css = compileSuccess.getCss();

        assertThat(css).contains("color:red");
    }

    @Test
    void compileCssUrl() throws IOException, SassCompilationFailedException {
        CompileSuccess compileSuccess = sassCompiler.compile(new URL("https://cdn.jsdelivr.net/npm/bootstrap@" + getBoostrapVersion() + "/dist/css/bootstrap.min.css"));

        String css = compileSuccess.getCss();
        assertThat(css).isNotBlank();
    }

    @Test
    void compileScssUrl() throws IOException, SassCompilationFailedException {
        CompileSuccess compileSuccess = sassCompiler.compile(new URL("https://cdn.jsdelivr.net/npm/bootstrap@" + getBoostrapVersion() + "/scss/bootstrap.scss"));

        String css = compileSuccess.getCss();
        assertThat(css).isNotBlank();
    }

    @Test
    void compileClasspathUrl() throws IOException, SassCompilationFailedException {
        URL resource = getClass().getResource("/META-INF/resources/webjars/bootstrap/" + getBoostrapVersion() + "/scss/bootstrap.scss");

        CompileSuccess compileSuccess = sassCompiler.compile(resource);

        String css = compileSuccess.getCss();
        assertThat(css).isNotBlank();
    }

    @Test
    void customFunction() throws Exception {
        @Language("SCSS") String scss = ".foo { .bar { color : foo(#ffffff);}}";

        HostFunction sassFunction = new HostFunction("foo", Collections.singletonList(new HostFunction.Argument("col", null))) {
            @Override
            public @NotNull Value invoke(List<Value> arguments) {
                return Value.newBuilder()
                        .setRgbColor(Value.RgbColor.newBuilder()
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
    void customFunction_error() {
        @Language("SCSS") String scss = ".foo { .bar { color : foo(#ffffff);}}";

        HostFunction sassFunction = new HostFunction("foo", Collections.singletonList(new HostFunction.Argument("col", null))) {
            @Override
            public @NotNull Value invoke(List<Value> arguments) {
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

        InboundMessage.CompileRequest.StringInput string = InboundMessage.CompileRequest.StringInput.newBuilder()
                .setSource(sass)
                .build();

        CompileSuccess compileSuccess = sassCompiler.compileString(string, OutputStyle.EXPANDED);

        System.out.println(compileSuccess.getSourceMap());

        File file = new File("src/test/resources/foo/bar.scss");

        CompileSuccess compileSuccess1 = sassCompiler.compileFile(file);

        System.out.println(compileSuccess1.getSourceMap());
    }

}