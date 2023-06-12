package de.larsgrefer.sass.embedded.connection;

import com.google.protobuf.ByteString;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import de.larsgrefer.sass.embedded.util.IOUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Lars Grefer
 */
@Slf4j
@UtilityClass
public class ConnectionFactory {

    /**
     * Path of the extracted compiler executable.
     */
    private static File bundledDartExec;

    public static ProcessConnection bundled() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(getBundledDartExec().getAbsolutePath(), "--embedded");

        return new ProcessConnection(processBuilder);
    }

    static File getBundledDartExec() throws IOException {
        if (bundledDartExec == null) {
            extractBundled();
        }
        return bundledDartExec;
    }

    synchronized static void extractBundled() throws IOException {
        String resourcePath = getBundledCompilerDistPath();

        URL dist = SassCompilerFactory.class.getResource(resourcePath);

        if (dist == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }

        Path tempDirectory = Files.createTempDirectory("dart-sass");

        try {
            IOUtils.extract(dist, tempDirectory);
        } catch (IOException e) {
            throw new IOException(String.format("Failed to extract %s into %s", dist, tempDirectory), e);
        }

        File execDir = tempDirectory.resolve("dart-sass").toFile();

        File[] execFile = execDir.listFiles(pathname -> pathname.isFile() && pathname.getName().startsWith("sass"));

        if (execFile == null || execFile.length != 1) {
            throw new IllegalStateException("No (unique) executable file found in " + execDir);
        } else {
            bundledDartExec = execFile[0];
        }

        bundledDartExec.setWritable(false);
        bundledDartExec.setExecutable(true, true);
    }

    private static String getBundledCompilerDistPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String classifier;
        String archiveExtension = "tar.gz";

        if (osName.contains("mac")) {
            if (osArch.equals("aarch64") || osArch.contains("arm") || isRunningOnRosetta2()) {
                classifier = "macos-arm64";
            } else {
                classifier = "macos-x64";
            }
        } else if (osName.contains("win")) {
            archiveExtension = "zip";
            classifier = osArch.contains("64") ? "windows-x64" : "windows-ia32";
        } else {
            if (osArch.equals("aarch64") || osArch.equals("arm64")) {
                classifier = "linux-arm64";
            } else if (osArch.contains("arm")) {
                classifier = "linux-arm";
            } else if (osArch.contains("64")) {
                classifier = "linux-x64";
            } else {
                classifier = "linux-ia32";
            }
        }

        return String.format("/de/larsgrefer/sass/embedded/dart-sass-%s.%s", classifier, archiveExtension);
    }

    /**
     * Check if we are running on an Intel x86_64 JDK emulated by Rosetta2 on an ARM mac.
     */
    private static boolean isRunningOnRosetta2() {
        try {
            Process sysctl = Runtime.getRuntime().exec("sysctl -in sysctl.proc_translated");
            ByteString stdOut;
            try (InputStream in = sysctl.getInputStream()) {
                stdOut = ByteString.readFrom(in);
            }
            if (sysctl.exitValue() == 0 && stdOut.toStringUtf8().equals("1\n")) {
                return true;
            }
        } catch (Exception e) {
            log.info("Unable to check for rosetta", e);
        }
        return false;
    }
}
