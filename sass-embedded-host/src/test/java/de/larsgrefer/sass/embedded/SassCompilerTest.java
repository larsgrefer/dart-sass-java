package de.larsgrefer.sass.embedded;

import de.larsgrefer.sass.embedded.functions.HostFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sass.embedded_protocol.EmbeddedSass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

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
    @Disabled
    void getVersion() throws IOException {
        EmbeddedSass.OutboundMessage.VersionResponse version = sassCompiler.getVersion();

        assertThat(version).isNotNull();
    }

    @Test
    void compileString() throws Exception {
        String sass = ".foo { .bar { color : #ffffff; @warn 'haha';}}";

        String s = null;
        for (int i = 0; i < 100; i++) {
            s = sassCompiler.compileString(sass);
        }
        System.out.println(s);
    }

    @Test
    void compileStringtoFile() throws Exception {
        String sass = ".foo { .bar { color : #ffffff;}}";

        String s = null;
        for (int i = 0; i < 100; i++) {
            Path target = Files.createTempFile("target", ".css");
            sassCompiler.compileString(sass, target.toFile());
            s = new String(Files.readAllBytes(target));
        }
        System.out.println(s);
    }

    @Test
    void customFunction() throws Exception {
        String sass = ".foo { .bar { color : foo(#ffffff);}}";

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

        String css = sassCompiler.compileString(sass);
        System.out.println(css);

    }

    @Test
    void customFunction_error() throws Exception {
        String sass = ".foo { .bar { color : foo(#ffffff);}}";

        HostFunction sassFunction = new HostFunction("foo", Collections.singletonList(new HostFunction.Argument("col", null))) {
            @Override
            public EmbeddedSass.Value invoke(List<EmbeddedSass.Value> arguments) throws Throwable {
                throw new RuntimeException("bazinga");
            }
        };

        sassCompiler.registerFunction(sassFunction);
        SassCompilationFailedException e = assertThrows(SassCompilationFailedException.class, () -> {
            String css = sassCompiler.compileString(sass);
        });

        assertThat(e.getMessage()).contains("bazinga");
    }

}