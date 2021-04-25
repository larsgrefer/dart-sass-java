package de.larsgrefer.sass.embedded;

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

@UtilityClass
public class IOUtils {

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
    public void untar(InputStream inputStream, Path targetDir) throws IOException {
        ProcessBuilder tarBuilder = new ProcessBuilder("tar", "xz", "-C", targetDir.toFile().getAbsolutePath());

        tarBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        tarBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        tarBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);

        Process tar = tarBuilder.start();

        try (OutputStream os = tar.getOutputStream()) {
            copy(inputStream, os);
        }

        tar.waitFor(5, TimeUnit.SECONDS);

    }

    private static final int bufferSize = 4096;

    public static void copy(InputStream inputStream, OutputStream os) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = inputStream.read(buffer, 0, bufferSize)) >= 0) {
            os.write(buffer, 0, read);
        }
    }

    public static void extract(URL archiveUrl, Path destinationDir) throws IOException {
        String file = archiveUrl.getFile();
        if (file.endsWith(".zip")) {
            try (ZipInputStream in = new ZipInputStream(archiveUrl.openStream())) {
                unzip(in, destinationDir);
            }
        }
        else if (file.endsWith(".tar.gz")) {
            try (InputStream in = archiveUrl.openStream()) {
                untar(in, destinationDir);
            }
        }
        else {
            throw new IllegalArgumentException("Unknown archive extension: " + archiveUrl);
        }
    }
}
