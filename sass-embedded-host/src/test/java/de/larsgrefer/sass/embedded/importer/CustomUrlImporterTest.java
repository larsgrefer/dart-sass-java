package de.larsgrefer.sass.embedded.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUrlImporterTest {

    CustomUrlImporter customUrlImporter;

    @BeforeEach
    void setUp() {
        customUrlImporter = new CustomUrlImporter() {
            @Override
            public URL canonicalizeUrl(String url) throws Exception {
                return null;
            }
        };
    }

    @Test
    void isDirectory_file() throws IOException {
        URL fileDirUrl = getClass().getClassLoader().getResource("foo");

        assertThat(customUrlImporter.isDirectory(fileDirUrl)).isTrue();

        URL fileUrl = getClass().getClassLoader().getResource("foo/bar.scss");

        assertThat(customUrlImporter.isDirectory(fileUrl)).isFalse();
    }

    @Test
    void isDirectory_jar() throws IOException {
        URL jarDirUrl = getClass().getClassLoader().getResource("META-INF");

        assertThat(customUrlImporter.isDirectory(jarDirUrl)).isTrue();

        URL jarFileUrl = getClass().getClassLoader().getResource("META-INF/MANIFEST.MF");

        assertThat(customUrlImporter.isDirectory(jarFileUrl)).isFalse();
    }
}