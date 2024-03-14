package de.larsgrefer.sass.embedded.connection;

import androidx.annotation.RequiresApi;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author Lars Grefer
 */
@RequiredArgsConstructor
public class ProcessConnection extends StreamConnection {

    private final Process process;

    @RequiresApi(26)
    public ProcessConnection(ProcessBuilder processBuilder) throws IOException {
        this(processBuilder
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start());
    }

    @Override
    public void close() {
        process.destroy();
        try {
            if (!process.waitFor(2, TimeUnit.SECONDS)) {
                process.destroyForcibly();
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        assertAlive();
        return process.getInputStream();
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        assertAlive();
        return process.getOutputStream();
    }

    private void assertAlive() throws IOException {
        try {
            int exitValue = process.exitValue();
            throw new IOException("Process is dead. Exit code was: " + exitValue);
        } catch(IllegalThreadStateException ignored) {
        }
    }
}
