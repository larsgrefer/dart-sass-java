package de.larsgrefer.sass.embedded;

import com.sass_lang.embedded_protocol.OutboundMessage;
import de.larsgrefer.sass.embedded.util.PropertyUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SassCompilerFactoryTest {

    @Test
    void bundled() throws IOException {
        testCompiler(SassCompilerFactory.bundled());
    }

    @Test
    void downloaded() throws IOException {
        testCompiler(SassCompilerFactory.downloaded());
    }

    private static void testCompiler(SassCompiler bundled) throws IOException {
        OutboundMessage.VersionResponse version = bundled.getVersion();

        assertThat(version.getImplementationVersion()).isEqualTo(PropertyUtils.getDartSassVersion());
    }
}