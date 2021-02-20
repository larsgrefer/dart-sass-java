package de.larsgrefer.sass.embedded;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Lars Grefer
 */
public class SassCompilerFactory {

    private static File bundledDartExec;
    private static File bundledSassSnapshot;

    public static SassCompiler bundled() throws IOException {
        if (bundledDartExec == null || bundledSassSnapshot == null || !bundledDartExec.isFile() || !bundledSassSnapshot.isFile()) {
            extractBundled();
        }

        return new SassCompiler(new ProcessBuilder(bundledDartExec.getAbsolutePath(), bundledSassSnapshot.getAbsolutePath()));
    }

    private synchronized static void extractBundled() throws IOException {
        Path tempDirectory = Files.createTempDirectory("dart-sass");

        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String baseDir;

        String dartExecName;

        if (osName.contains("mac")) {
            baseDir = "macos-x64";
            dartExecName = "dart";
        }
        else if (osName.contains("win")) {
            dartExecName = "dart.exe";
            if (osArch.contains("64")) {
                baseDir = "windows-x64";
            }
            else {
                baseDir = "windows-ia32";
            }
        }
        else {
            dartExecName = "dart";
            if (osArch.contains("64")) {
                baseDir = "linux-x64";
            }
            else {
                baseDir = "linux-ia32";
            }
        }

        String dartExecPath = baseDir + "/sass_embedded/src/" + dartExecName;
        String snapshotPath = baseDir + "/sass_embedded/src/dart-sass-embedded.snapshot";

        bundledDartExec = tempDirectory.resolve(dartExecName).toFile();
        bundledSassSnapshot = tempDirectory.resolve("dart-sass-embedded.snapshot").toFile();

        extracted(dartExecPath, bundledDartExec.toPath());
        bundledDartExec.setExecutable(true, true);
        extracted(snapshotPath, bundledSassSnapshot.toPath());
    }

    private static void extracted(String path, Path targetPath) throws IOException {
        targetPath.toFile().getParentFile().mkdirs();
        try (InputStream resourceAsStream = SassCompilerFactory.class.getClassLoader().getResourceAsStream(path);) {
            Files.copy(resourceAsStream, targetPath);
        } catch (IllegalArgumentException e) {
            throw new IOException("Failed to extract path " + path, e);
        }
    }

}
