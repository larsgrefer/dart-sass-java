package de.larsgrefer.sass.embedded.android;

import android.app.Instrumentation;
import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import com.sass_lang.embedded_protocol.OutboundMessage;
import de.larsgrefer.sass.embedded.SassCompiler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;


public class AndroidSassCompilerFactoryTest {

    @Test
    public void testVersionResponse() throws IOException {

        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        try (SassCompiler sassCompiler = AndroidSassCompilerFactory.bundled(context)) {
            OutboundMessage.VersionResponse version = sassCompiler.getVersion();

            assertNotNull(version);
            assertEquals(0, version.getId());
        }

    }
}