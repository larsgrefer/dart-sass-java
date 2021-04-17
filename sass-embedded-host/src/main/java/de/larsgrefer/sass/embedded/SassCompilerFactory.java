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
        if (bundledDartExec == null || !bundledDartExec.isFile()) {
            extractBundled();
        }

        ProcessBuilder processBuilder;
        if (bundledSassSnapshot == null) {
            processBuilder = new ProcessBuilder(bundledDartExec.getAbsolutePath());
        }
        else {
            processBuilder = new ProcessBuilder(bundledDartExec.getAbsolutePath(), bundledSassSnapshot.getAbsolutePath());
        }

        return new SassCompiler(processBuilder);
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
            if (osArch.contains("64")) {
                dartExecName = "dart-sass-embedded";
                baseDir = "linux-x64";
            }
            else {
                dartExecName = "dart";
                baseDir = "linux-ia32";
            }
        }

        bundledDartExec = tempDirectory.resolve(dartExecName).toFile();

        if ("linux-x64".equals(baseDir)) {
            String execPath = baseDir + "/sass_embedded/dart-sass-embedded";

            extracted(execPath, bundledDartExec.toPath());
        }
        else {
            String dartExecPath = baseDir + "/sass_embedded/src/" + dartExecName;
            String snapshotPath = baseDir + "/sass_embedded/src/dart-sass-embedded.snapshot";

            bundledSassSnapshot = tempDirectory.resolve("dart-sass-embedded.snapshot").toFile();
            extracted(dartExecPath, bundledDartExec.toPath());
            extracted(snapshotPath, bundledSassSnapshot.toPath());
        }

        bundledDartExec.setExecutable(true, true);
    }

    private static void extracted(String path, Path targetPath) throws IOException {
        targetPath.toFile().getParentFile().mkdirs();
        try (InputStream resourceAsStream = SassCompilerFactory.class.getClassLoader().getResourceAsStream(path);) {
            if (resourceAsStream != null) {
                Files.copy(resourceAsStream, targetPath);
            }
            else {
                throw new IllegalArgumentException(String.format("Resource '%s' was not found", path));
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Failed to extract path " + path, e);
        }
    }

}
