package de.larsgrefer.sass;

import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class SassCompiler {

    private String version;

    private final String sassExecutable;

    private Duration timeout = Duration.ofSeconds(10);

    public SassCompiler() throws IOException {
        this("sass");
    }

    public SassCompiler(String sassExecutable) throws IOException {
        this.sassExecutable = sassExecutable;

        determineVersion();
    }

    private void determineVersion() throws IOException {
        Process process = new ProcessBuilder(sassExecutable, "--version")
                .start();


        try {
            if (process.waitFor() == 0) {
                try (BufferedSource buffer = Okio.buffer(Okio.source(process.getInputStream()))) {
                    version = buffer.readUtf8();
                }
            }
            else {
                throw new RuntimeException(Okio.buffer(Okio.source(process.getErrorStream())).readUtf8());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String compileString(String sass) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder("sass", "--stdin");

        Process sassc = processBuilder.start();

        BufferedSink stdin = Okio.buffer(Okio.sink(sassc.getOutputStream()));

        stdin.writeUtf8(sass);
        stdin.close();

        BufferedSource stdout = Okio.buffer(Okio.source(sassc.getInputStream()));
        BufferedSource stderr = Okio.buffer(Okio.source(sassc.getErrorStream()));

        Buffer stdoutBuffer = new Buffer();
        Buffer stderrBuffer = new Buffer();

        Instant end = Instant.now().plus(timeout);
        do {
            stdout.readAll(stdoutBuffer);
            stderr.readAll(stderrBuffer);
        } while (!sassc.waitFor(10, TimeUnit.MILLISECONDS) && sassc.isAlive() && Instant.now().isBefore(end));

        stdout.close();
        stderr.close();

        if (sassc.exitValue() == 0) {
            return stdoutBuffer.readUtf8();
        }
        else {
            throw new RuntimeException(stderrBuffer.readUtf8());
        }

    }

    public void compileString(String sass, File outputFile) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder("sass", "--stdin", outputFile.getPath());

        Process sassc = processBuilder.start();

        BufferedSink stdin = Okio.buffer(Okio.sink(sassc.getOutputStream()));

        stdin.writeUtf8(sass);
        stdin.close();

        BufferedSource stderr = Okio.buffer(Okio.source(sassc.getErrorStream()));

        Buffer stderrBuffer = new Buffer();

        Instant end = Instant.now().plus(timeout);
        do {
            stderr.readAll(stderrBuffer);
        } while (!sassc.waitFor(10, TimeUnit.MILLISECONDS) && sassc.isAlive() && Instant.now().isBefore(end));

        stderr.close();

        if (sassc.exitValue() != 0) {
            throw new RuntimeException(stderrBuffer.readUtf8());
        }

    }

    public String compileFile(File inputFile) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("sass", inputFile.getPath());

        Process sassc = processBuilder.start();

        BufferedSource stdout = Okio.buffer(Okio.source(sassc.getInputStream()));
        BufferedSource stderr = Okio.buffer(Okio.source(sassc.getErrorStream()));

        Buffer stdoutBuffer = new Buffer();
        Buffer stderrBuffer = new Buffer();

        Instant end = Instant.now().plus(timeout);
        do {
            stdout.readAll(stdoutBuffer);
            stderr.readAll(stderrBuffer);
        } while (!sassc.waitFor(10, TimeUnit.MILLISECONDS) && sassc.isAlive() && Instant.now().isBefore(end));

        stdout.close();
        stderr.close();

        if (sassc.exitValue() == 0) {
            return stdoutBuffer.readUtf8();
        }
        else {
            throw new RuntimeException(stderrBuffer.readUtf8());
        }

    }

    public void compileFile(File inputFile, File outputFile) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("sass", inputFile.getPath(), outputFile.getPath());

        Process sassc = processBuilder.start();

        BufferedSource stderr = Okio.buffer(Okio.source(sassc.getErrorStream()));

        Buffer stderrBuffer = new Buffer();

        Instant end = Instant.now().plus(timeout);
        do {
            stderr.readAll(stderrBuffer);
        } while (!sassc.waitFor(10, TimeUnit.MILLISECONDS) && sassc.isAlive() && Instant.now().isBefore(end));

        stderr.close();

        if (sassc.exitValue() != 0) {
            throw new RuntimeException(stderrBuffer.readUtf8());
        }

    }

    public String getVersion() {
        return version;
    }
}
