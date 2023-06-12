package de.larsgrefer.sass.embedded.connection;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionFactoryTest {

    @Test
    void getBundledDartExec() throws IOException, InterruptedException {

        Process dartSass = new ProcessBuilder(ConnectionFactory.getBundledDartExec().getAbsolutePath(), "--embedded", "--version")
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start();

        dartSass.waitFor();
    }
}