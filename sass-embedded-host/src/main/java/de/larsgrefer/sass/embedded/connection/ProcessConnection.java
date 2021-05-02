package de.larsgrefer.sass.embedded.connection;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author Lars Grefer
 */
public class ProcessConnection extends StreamConnection {

    private final Process process;

    public ProcessConnection(ProcessBuilder processBuilder) throws IOException {
        this(processBuilder
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start());
    }

    public ProcessConnection(Process process) {
        this.process = process;
    }

    @SneakyThrows
    @Override
    public void close() {
        process.destroy();
        if (!process.waitFor(2, TimeUnit.SECONDS)) {
            process.destroyForcibly();
        }
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        if (!process.isAlive()) {
            throw new IOException("Process is dead. Exit code was: " + process.exitValue());
        }
        return process.getInputStream();
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        if (!process.isAlive()) {
            throw new IOException("Process is dead. Exit code was: " + process.exitValue());
        }
        return process.getOutputStream();
    }
}
