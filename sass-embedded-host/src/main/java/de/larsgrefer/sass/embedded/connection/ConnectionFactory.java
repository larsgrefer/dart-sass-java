package de.larsgrefer.sass.embedded.connection;

import androidx.annotation.RequiresApi;
import com.google.protobuf.ByteString;
import de.larsgrefer.sass.embedded.util.PropertyUtils;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lars Grefer
 */
@Slf4j
@UtilityClass
@RequiresApi(1000)
public class ConnectionFactory {

    private static BundledPackageProvider bundledPackageProvider = new BundledPackageProvider();
    private static DownloadingPackageProvider downloadingPackageProvider = new DownloadingPackageProvider();

    public static ProcessConnection bundled() throws IOException {
        return fromPackageProvider(bundledPackageProvider);
    }

    public static ProcessConnection downloaded() throws IOException {
        return fromPackageProvider(downloadingPackageProvider);
    }

    public static ProcessConnection fromPackageProvider(DartSassPackageProvider dartSassPackageProvider) throws IOException {
        File executable;
        try {
            executable = dartSassPackageProvider.getDartSassExecutable();
        } catch (IOException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }

        return ofExecutable(executable);
    }

    /**
     * Create a new {@link ProcessConnection} for the given dart-sass executable.
     *
     * @param executable The sass executable.
     * @return A fresh {@link ProcessConnection}.
     * @throws IOException if the subprocess can not be started.
     */
    public static ProcessConnection ofExecutable(File executable) throws IOException {
        if (executable == null || !executable.isFile()) {
            throw new IllegalArgumentException(executable + " is not a file");
        }

        List<String> cmd = new ArrayList<>();

        if (executable.getName().equals("sass.bat") && System.getProperty("os.name").toLowerCase().contains("win")) {
            File dir = executable.getParentFile();

            File dartExe = new File(dir, "src/dart.exe");
            File sassSnapshot = new File(dir, "src/sass.snapshot");

            if (dartExe.isFile() && dartExe.canExecute() && sassSnapshot.isFile()) {
                cmd.add(dartExe.getAbsolutePath());
                cmd.add(sassSnapshot.getAbsolutePath());
            }
        } else if (executable.length() < 1024) { // small shell-script
            File dir = executable.getParentFile();

            File dart = new File(dir, "src/dart");
            File sassSnapshot = new File(dir, "src/sass.snapshot");

            if (dart.isFile() && dart.canExecute() && sassSnapshot.isFile()) {
                cmd.add(dart.getAbsolutePath());
                cmd.add(sassSnapshot.getAbsolutePath());
            }
        }

        if (cmd.isEmpty()) {
            if (!executable.canExecute()) {
                throw new IllegalArgumentException(executable + " can not be executed");
            }

            cmd.add(executable.getAbsolutePath());
        }

        cmd.add("--embedded");

        return ofExecutable(cmd);
    }

    /**
     * Create a new {@link ProcessConnection} for the given dart-sass command.
     *
     * @param executable The sass commandline (including --embedded).
     * @return A fresh {@link ProcessConnection}.
     * @throws IOException if the subprocess can not be started.
     */
    public static ProcessConnection ofExecutable(List<String> executable) throws IOException {
        String expectedProtocolVersion = getExpectedProtocolVersion();
        String protocolVersion = findProtocolVersion(executable);
        if (!expectedProtocolVersion.equalsIgnoreCase(protocolVersion)) {
            log.warn("This Host uses protocolVersion {} but {} provides {}", expectedProtocolVersion, executable, protocolVersion);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(executable);
        return new ProcessConnection(processBuilder);
    }

    public static String getExpectedProtocolVersion() {
        return PropertyUtils.getEmbeddedProtocolVersion();
    }

    private static final Pattern protocolVersionPattern = Pattern.compile("\"protocolVersion\": \"(.*?)\"");

    @SneakyThrows(InterruptedException.class)
    String findProtocolVersion(List<String> executable) throws IOException {
        List<String> command = new ArrayList<>(executable);
        command.add("--version");

        Process testProcess = new ProcessBuilder(command)
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
