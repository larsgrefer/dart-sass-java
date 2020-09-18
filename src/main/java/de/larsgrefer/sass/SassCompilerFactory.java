package de.larsgrefer.sass;

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

        Path tempDirectory = Files.createTempDirectory("dart-sass");

        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String baseDir;
        List<String> paths;

        if (osName.contains("mac")) {
            baseDir = "macos-x64";
            paths = Arrays.asList("sass", "src/dart", "src/sass.snapshot");
        }
        else if (osName.contains("win")) {
            paths = Arrays.asList("sass.bat", "src/dart.exe", "src/sass.snapshot");
            if (osArch.contains("64")) {
                baseDir = "windows-x64";
            }
            else {
                baseDir = "windows-ia32";
            }
        }
        else {
            paths = Arrays.asList("sass", "src/dart", "src/sass.snapshot");
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
                    InputStream resourceAsStream = SassCompilerFactory.class.getClassLoader().getResourceAsStream(baseDir + "/dart-sass/" + path);
                    BufferedSource source = Okio.buffer(Okio.source(resourceAsStream));
                    BufferedSink sink = Okio.buffer(Okio.sink(targetPath))) {

                source.readAll(sink);
            }

            targetPath.toFile().setExecutable(true, true);
        }

        return new SassCompiler(sassExec.getAbsolutePath());

    }

}
