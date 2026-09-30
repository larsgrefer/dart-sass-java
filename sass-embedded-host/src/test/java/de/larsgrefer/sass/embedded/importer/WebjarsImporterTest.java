package de.larsgrefer.sass.embedded.importer;

import de.larsgrefer.sass.embedded.BootstrapUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void canonicalizeUrl_1() throws IOException {
        URL url = webjarsImporter.canonicalizeUrl(BootstrapUtil.getBoostrapVersion() + "/scss/bootstrap.scss");

        assertThat(url).isNotNull();
    }

    @Test
    void canonicalizeUrl_2() throws IOException {
        URL url = webjarsImporter.canonicalizeUrl("bootstrap/"+BootstrapUtil.getBoostrapVersion() + "/scss/bootstrap.scss");

        assertThat(url).isNotNull();
    }

    @Test
    void canonicalizeUrl_3() throws IOException {
        URL url = webjarsImporter.canonicalizeUrl("bootstrap/scss/bootstrap.scss");

        assertThat(url).isNotNull();
    }
}