package de.larsgrefer.sass.embedded.android;

import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class SassCompilerFactoryTest {

    @Test
    public void testNormalCompilerFactory() throws IOException {
        try {
            SassCompilerFactory.bundled();
            Assert.fail();
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessageContaining("Android");
        }
    }
}
