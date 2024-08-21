package de.larsgrefer.sass.embedded.util;

import androidx.annotation.RequiresApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
@RequiredArgsConstructor
@RequiresApi(10000)
public class DirCleaner implements Runnable {

    private final Path path;

    @Override
    public void run() {
        if (!Files.exists(path)) {
            return;
        }

        try {
            Files.walkFileTree(path, new DeletingFileVisitor());
        } catch (IOException e) {
            log.warn("Failed to delete {}", path, e);
        }
    }

    private static class DeletingFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc != null) {
                throw exc;
            }
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
