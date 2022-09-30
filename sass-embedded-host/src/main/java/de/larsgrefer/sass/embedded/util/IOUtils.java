package de.larsgrefer.sass.embedded.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Lars Grefer
 */
@UtilityClass
public class IOUtils {

    public static void extract(URL archiveUrl, Path destinationDir) throws IOException {
        String file = archiveUrl.getPath();
        if (file.endsWith(".zip")) {
            try (ZipInputStream in = new ZipInputStream(archiveUrl.openStream())) {
                unzip(in, destinationDir);
            }
        }
        else if (file.endsWith(".tar.gz")) {
            try (InputStream in = archiveUrl.openStream()) {
                untar(in, destinationDir.toFile());
            }
        }
        else {
            throw new IllegalArgumentException("Unknown archive extension: " + archiveUrl);
        }
    }

    public void unzip(ZipInputStream zipInputStream, Path targetPath) throws IOException {
        ZipEntry entry = zipInputStream.getNextEntry();

        while (entry != null) {
            Path entryPath = targetPath.resolve(entry.getName());
            File entryFile = entryPath.toFile();

            if (entry.isDirectory()) {
                ensureDirectory(entryFile);
            }
            else {
                ensureDirectory(entryFile.getParentFile());

                Files.copy(zipInputStream, entryPath);
            }
            zipInputStream.closeEntry();

            entry = zipInputStream.getNextEntry();
        }
    }

    private static void ensureDirectory(File entryFile) throws IOException {
        if (!entryFile.isDirectory() && !entryFile.mkdirs()) {
            throw new IOException("Failed to create dir " + entryFile);
        }
    }

    @SneakyThrows(InterruptedException.class)
    public void untar(InputStream inputStream, File targetDir) throws IOException {
        ensureDirectory(targetDir);

        Process tar = new ProcessBuilder("tar", "xz")
                .directory(targetDir)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .start();

        try (OutputStream os = tar.getOutputStream()) {
            inputStream.transferTo(os);
        }

        if (!tar.waitFor(5, TimeUnit.SECONDS)) {
            tar.destroy();
        }

        if (!tar.waitFor(5, TimeUnit.SECONDS)) {
            tar.destroyForcibly();
        }

        int exitValue = tar.exitValue();
        if (exitValue != 0) {
            throw new IOException("tar process exited with " + exitValue);
        }
    }

}
