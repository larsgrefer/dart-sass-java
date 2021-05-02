package de.larsgrefer.sass.embedded.connection;

import de.larsgrefer.sass.embedded.SassCompilationFailedException;
import de.larsgrefer.sass.embedded.SassCompiler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@EnabledOnOs(value = {OS.LINUX, OS.MAC})
class SocketConnectionIT {

    private Process socat;

    @BeforeEach
    void setUp() throws IOException {
        socat = new ProcessBuilder()
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .command(
                        "socat",
                        "tcp-listen:50506,reuseaddr",
                        "exec:" + ConnectionFactory.getBundledDartExec().getAbsolutePath()
                ).start();
    }

    @AfterEach
    void tearDown() {
        socat.destroy();
    }

    @Test
    public void compile() throws IOException, SassCompilationFailedException {
        assertThat(socat.isAlive()).isTrue();

        SocketConnection socketConnection = new SocketConnection(new Socket("localhost", 50506));

        try (SassCompiler compiler = new SassCompiler(socketConnection)) {
            String s = compiler.compileString("body {size: 1+1}");

            assertThat(s).contains("2");
        }
    }
}