package de.larsgrefer.sass.embedded.connection;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
class BundledPackageProviderTest {

    BundledPackageProvider bundledPackageProvider = new BundledPackageProvider();

    @Test
    void getDartSassExecutable() throws InterruptedException, IOException {
        Process dartSass = new ProcessBuilder(bundledPackageProvider.getDartSassExecutable().getAbsolutePath(), "--embedded", "--version")
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start();

        dartSass.waitFor();

    }
}