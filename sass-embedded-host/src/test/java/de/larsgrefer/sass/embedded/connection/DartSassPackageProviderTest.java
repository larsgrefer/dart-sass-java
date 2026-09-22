package de.larsgrefer.sass.embedded.connection;

import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.annotation.Nullable;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DartSassPackageProviderTest {

    DartSassPackageProvider dartSassPackageProvider;

    @BeforeEach
    void setUp() {
        dartSassPackageProvider = new BundledPackageProvider();
    }

    @Test
    void testConcurrentAccess() throws IOException {

        try (SassCompiler compiler = SassCompilerFactory.bundled()) {
            assertThat(compiler.getVersion()).isNotNull();

            dartSassPackageProvider.extractPackage();
            assertThat(compiler.getVersion()).isNotNull();

            dartSassPackageProvider.extractPackage();
            assertThat(compiler.getVersion()).isNotNull();
        }

    }

    @Test
    void extractPackageRestoresMissingExecutable(@TempDir Path tempDir) throws IOException {
        Path archive = tempDir.resolve("dart-sass.zip");
        createDartSassZip(archive);

        Path targetPath = tempDir.resolve("target");
        DartSassPackageProvider provider = new TestPackageProvider(archive.toUri().toURL(), targetPath);

        File executable = provider.extractPackage();
        assertThat(executable).isFile();

        Files.delete(executable.toPath());
        Files.write(targetPath.resolve("dart-sass").resolve("kept-by-temp-cleaner"), new byte[0]);

        File restoredExecutable = provider.extractPackage();
        assertThat(restoredExecutable).isFile();
    }

    private void createDartSassZip(Path archive) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(archive);
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry("dart-sass/"));
            zipOutputStream.closeEntry();
            zipOutputStream.putNextEntry(new ZipEntry("dart-sass/sass"));
            zipOutputStream.write(new byte[]{1});
            zipOutputStream.closeEntry();
        }
    }

    private static class TestPackageProvider extends DartSassPackageProvider {

        private final URL packageUrl;
        private final Path targetPath;

        TestPackageProvider(URL packageUrl, Path targetPath) {
            this.packageUrl = packageUrl;
            this.targetPath = targetPath;
        }

        @Override
        Path getTargetPath() {
            return targetPath;
        }

        @Nullable
        @Override
        protected URL getPackageUrl() {
            return packageUrl;
        }
    }
}
