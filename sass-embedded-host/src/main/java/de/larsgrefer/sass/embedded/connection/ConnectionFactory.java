package de.larsgrefer.sass.embedded.connection;

import de.larsgrefer.sass.embedded.SassCompilerFactory;
import de.larsgrefer.sass.embedded.util.IOUtils;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class ConnectionFactory {

    private static File bundledDartExec;

    public static ProcessConnection bundled() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(getBundledDartExec().getAbsolutePath());

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

        IOUtils.extract(dist, tempDirectory);

        File execDir = tempDirectory.resolve("sass_embedded").toFile();

        File[] execFile = execDir.listFiles(pathname -> pathname.isFile() && pathname.getName().startsWith("dart-sass-embedded"));

        if (execFile == null || execFile.length != 1) {
            throw new IllegalStateException("No (unique) executable file found in " + execDir);
        }
        else {
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
            classifier = "macos-x64";
        }
        else if (osName.contains("win")) {
            archiveExtension = "zip";
            classifier = osArch.contains("64") ? "windows-x64" : "windows-ia32";
        }
        else {
            classifier = osArch.contains("64") ? "linux-x64" : "linux-ia32";
        }

        return String.format("/de/larsgrefer/sass/embedded/sass_embedded-%s.%s", classifier, archiveExtension);
    }
}
