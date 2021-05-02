package de.larsgrefer.sass.embedded.connection;

import de.larsgrefer.sass.embedded.SassCompilationFailedException;
import de.larsgrefer.sass.embedded.SassCompiler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@EnabledOnOs(value = {OS.LINUX, OS.MAC})
class SocketConnectionIT {

    private int port = 0;

    private Process socat;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            port = serverSocket.getLocalPort();
        }

        socat = new ProcessBuilder()
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .command(
                        "socat",
                        "tcp-listen:" + port + ",reuseaddr",
                        "exec:" + ConnectionFactory.getBundledDartExec().getAbsolutePath()
                ).start();

        Thread.sleep(10);
    }

    @AfterEach
    void tearDown() {
        socat.destroy();
    }

    @Test
    public void compile() throws IOException, SassCompilationFailedException {
        assertThat(socat.isAlive()).isTrue();

        SocketConnection socketConnection = new SocketConnection(new Socket("localhost", port));

        try (SassCompiler compiler = new SassCompiler(socketConnection)) {
            String s = compiler.compileString("body {size: 1+1}");

            assertThat(s).contains("2");
        }
    }
}