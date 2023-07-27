package de.larsgrefer.sass.embedded.connection;

import com.google.protobuf.ByteString;
import com.sass_lang.embedded_protocol.OutboundMessage;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lars Grefer
 */
@Slf4j
@UtilityClass
public class ConnectionFactory {

    public static ProcessConnection bundled() throws IOException {

        Callable<File> bundledExecCallable;

        try {
            Class<?> bundledFactoryClass = Class.forName("de.larsgrefer.sass.embedded.bundled.BundledCompilerFactory");
            bundledExecCallable = (Callable<File>) bundledFactoryClass.getConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Embedded Compilers are not available", e);
        } catch (ReflectiveOperationException e) {
            throw new IOException(e);
        }

        File bundledExecutable;
        try {
            bundledExecutable = bundledExecCallable.call().getAbsoluteFile();
        } catch (IOException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }

        return ofExecutable(bundledExecutable);
    }

    public static ProcessConnection ofExecutable(File executable) throws IOException {
        if (executable == null || !executable.isFile()) {
            throw new IllegalArgumentException("executable is not a file");
        }

        if (!executable.canExecute()) {
            throw new IllegalArgumentException(executable + " can not be executed");
        }

        String expectedProtocolVersion = getExpectedProtocolVersion();
        String protocolVersion = findProtocolVersion(executable);
        if (!expectedProtocolVersion.equalsIgnoreCase(protocolVersion)) {
            log.warn("This Host uses protocolVersion {} but {} provides {}", expectedProtocolVersion, executable, protocolVersion);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(executable.getAbsolutePath(), "--embedded");
        return new ProcessConnection(processBuilder);
    }

    public static String getExpectedProtocolVersion() {
        return OutboundMessage.VersionResponse.class.getPackage().getSpecificationVersion();
    }

    private static final Pattern protocolVersionPattern = Pattern.compile("\"protocolVersion\": \"(.*?)\"");

    @SneakyThrows(InterruptedException.class)
    String findProtocolVersion(File executable) throws IOException {
        Process testProcess = new ProcessBuilder(executable.getAbsolutePath(), "--embedded", "--version")
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .start();

        String stdOut;
        try (InputStream in = testProcess.getInputStream()) {
            stdOut = ByteString.readFrom(in).toStringUtf8();
        }

        int exitCode = testProcess.waitFor();

        if (exitCode != 0) {
            throw new IllegalStateException(executable + " exited with " + exitCode);
        }

        Matcher matcher = protocolVersionPattern.matcher(stdOut);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalStateException("Can't find protocolVersion in " + stdOut);
        }
    }

}
