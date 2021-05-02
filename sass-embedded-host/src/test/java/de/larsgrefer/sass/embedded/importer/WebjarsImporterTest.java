package de.larsgrefer.sass.embedded.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WebjarsImporterTest {

    WebjarsImporter webjarsImporter;

    @BeforeEach
    void setUp() {
        webjarsImporter = new WebjarsImporter();
    }

    @Test
    void canonicalizeUrl() throws IOException {
        URL url = webjarsImporter.canonicalizeUrl("scss/bootstrap.scss");

        assertThat(url).isNotNull();
    }
}