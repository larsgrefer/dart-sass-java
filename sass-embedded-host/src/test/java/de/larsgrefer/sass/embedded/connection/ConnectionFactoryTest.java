package de.larsgrefer.sass.embedded.connection;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionFactoryTest {

    @Test
    void getExpectedProtocolVersion() {
        String expectedProtocolVersion = ConnectionFactory.getExpectedProtocolVersion();

        assertThat(expectedProtocolVersion).startsWith("2.");
    }

    @Test
    void bundled() throws IOException {
        File bundledExecutable = new BundledPackageProvider().getDartSassExecutable();

        String protocolVersion = ConnectionFactory.findProtocolVersion(Arrays.asList(bundledExecutable.getAbsolutePath(), "--embedded"));

        assertThat(protocolVersion).isEqualTo(ConnectionFactory.getExpectedProtocolVersion());

    }
}