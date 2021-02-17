package de.larsgrefer.sass.embedded;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class SassCompilerFactory {


    public static SassCompiler forLocalInstallation() throws IOException {
        return forLocalInstallation("sass");
    }

    public static SassCompiler forLocalInstallation(String path) throws IOException {
        return new SassCompiler(path);
    }

    public static SassCompiler bundled() throws IOException {

        File sassExec = getSassExecutable();

        return new SassCompiler(sassExec.getAbsolutePath());

    }

    private static File getSassExecutable() throws IOException {
        Path tempDirectory = Files.createTempDirectory("dart-sass");

        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String baseDir;
        List<String> paths;

        if (osName.contains("mac")) {
            baseDir = "macos-x64";
            paths = Arrays.asList("dart-sass-embedded", "src/dart", "src/dart-sass-embedded.snapshot");
        }
        else if (osName.contains("win")) {
            paths = Arrays.asList("dart-sass-embedded.bat", "src/dart.exe", "src/dart-sass-embedded.snapshot");
            if (osArch.contains("64")) {
                baseDir = "windows-x64";
            }
            else {
                baseDir = "windows-ia32";
            }
        }
        else {
            paths = Arrays.asList("dart-sass-embedded", "src/dart", "src/dart-sass-embedded.snapshot");
            if (osArch.contains("64")) {
                baseDir = "linux-x64";
            }
            else {
                baseDir = "linux-ia32";
            }
        }

        File sassExec = null;

        for (String path : paths) {
            Path targetPath = tempDirectory.resolve(path);
            if (sassExec == null) {
                sassExec = targetPath.toFile();
            }
            targetPath.toFile().getParentFile().mkdirs();
            try (
                    InputStream resourceAsStream = SassCompilerFactory.class.getClassLoader().getResourceAsStream(baseDir + "/sass_embedded/" + path);
                    BufferedSource source = Okio.buffer(Okio.source(resourceAsStream));
                    BufferedSink sink = Okio.buffer(Okio.sink(targetPath))) {

                source.readAll(sink);
            } catch (IllegalArgumentException e) {
                throw new IOException("Failed to extract path " + path, e);
            }

            targetPath.toFile().setExecutable(true, true);
        }
        return sassExec;
    }

}
