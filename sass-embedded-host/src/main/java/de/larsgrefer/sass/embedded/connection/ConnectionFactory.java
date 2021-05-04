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
        Path tempDirectory = Files.createTempDirectory("dart-sass");

        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String classifier;
        String archiveExtension = "tar.gz";

        if (osName.contains("mac")) {
            classifier = "macos-x64";
        }
        else if (osName.contains("win")) {
            archiveExtension = "zip";
            if (osArch.contains("64")) {
                classifier = "windows-x64";
            }
            else {
                classifier = "windows-ia32";
            }
        }
        else {
            if (osArch.contains("64")) {
                classifier = "linux-x64";
            }
            else {
                classifier = "linux-ia32";
            }
        }

        String resourcePath = String.format("/de/larsgrefer/sass/embedded/sass_embedded-%s.%s", classifier, archiveExtension);

        URL dist = SassCompilerFactory.class.getResource(resourcePath);

        if (dist == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }

        IOUtils.extract(dist, tempDirectory);

        Path execPath = tempDirectory.resolve("sass_embedded/dart-sass-embedded");
        Path execPathBat = tempDirectory.resolve("sass_embedded/dart-sass-embedded.bat");
        Path execPathExe = tempDirectory.resolve("sass_embedded/dart-sass-embedded.exe");
        if (execPath.toFile().isFile()) {
            bundledDartExec = execPath.toFile();
        }
        else if (execPathBat.toFile().isFile()) {
            bundledDartExec = execPathBat.toFile();
        }
        else if (execPathExe.toFile().isFile()) {
            bundledDartExec = execPathExe.toFile();
        }
        else {
            throw new IllegalStateException("No executable found");
        }

        bundledDartExec.setWritable(false);
        bundledDartExec.setExecutable(true, true);
    }
}
