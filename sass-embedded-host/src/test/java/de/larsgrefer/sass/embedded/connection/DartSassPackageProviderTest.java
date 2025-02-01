package de.larsgrefer.sass.embedded.connection;

import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
}