package de.larsgrefer.sass.embedded.connection;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DownloadingPackageProviderTest {

    DownloadingPackageProvider provider = new DownloadingPackageProvider();

    @Test
    void getDartSassExecutable() throws IOException {

        File dartSassExecutable = provider.getDartSassExecutable();

        assertThat(dartSassExecutable).isNotNull();
        assertThat(dartSassExecutable).isFile();
    }
}