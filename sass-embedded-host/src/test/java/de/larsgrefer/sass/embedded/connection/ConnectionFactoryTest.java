package de.larsgrefer.sass.embedded.connection;

import de.larsgrefer.sass.embedded.bundled.BundledCompilerFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionFactoryTest {

    @Test
    void getExpectedProtocolVersion() {
        String expectedProtocolVersion = ConnectionFactory.getExpectedProtocolVersion();

        assertThat(expectedProtocolVersion).startsWith("2.");
    }

    @Test
    void bundled() throws IOException {
        File bundledExecutable = new BundledCompilerFactory().call();

        String protocolVersion = ConnectionFactory.findProtocolVersion(Arrays.asList(bundledExecutable.getAbsolutePath(), "--embedded"));

        assertThat(protocolVersion).isEqualTo(ConnectionFactory.getExpectedProtocolVersion());

    }
}