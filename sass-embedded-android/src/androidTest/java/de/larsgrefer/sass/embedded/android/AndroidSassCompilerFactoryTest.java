package de.larsgrefer.sass.embedded.android;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import com.sass_lang.embedded_protocol.OutboundMessage;
import de.larsgrefer.sass.embedded.SassCompiler;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class AndroidSassCompilerFactoryTest {

    @Test
    public void testVersionResponse() throws IOException {

        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        try (SassCompiler sassCompiler = AndroidSassCompilerFactory.bundled(context)) {
            OutboundMessage.VersionResponse version = sassCompiler.getVersion();

            assertThat(version).isNotNull();
            assertThat(version.getCompilerVersion()).isEqualTo(BuildConfig.DART_SASS_VERSION);
        }

    }
}