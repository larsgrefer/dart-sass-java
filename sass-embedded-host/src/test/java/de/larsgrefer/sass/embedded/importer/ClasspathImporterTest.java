package de.larsgrefer.sass.embedded.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sass.embedded_protocol.EmbeddedSass;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClasspathImporterTest {

    private ClasspathImporter classpathImporter;

    @BeforeEach
    void setUp() {
        classpathImporter = new ClasspathImporter();
    }

    @Test
    void canonicalizeToImport() throws Exception {
        String canonicalize = classpathImporter.canonicalize("foo/bar.scss", false);

        assertThat(canonicalize).isNotNull();

        URL newUrl = new URL(canonicalize);

        EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess success = classpathImporter.handleImport(newUrl);
        assertThat(success).isNotNull();

        assertThat(success.getContents()).contains("red");
        assertThat(success.getSyntax()).isEqualTo(EmbeddedSass.Syntax.SCSS);
    }

    @Test
    void canonicalizeToImport_jar() throws Exception {
        String canonicalize = classpathImporter.canonicalize("META-INF/resources/webjars/bootstrap/5.1.1/scss/bootstrap.scss", false);

        assertThat(canonicalize).isNotNull();

        URL newUrl = new URL(canonicalize);

        EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess success = classpathImporter.handleImport(newUrl);
        assertThat(success).isNotNull();

        assertThat(success.getContents()).contains("@import");
    }

    @Test
    void canonicalizeAmbigous_dir() throws Exception {

        String canonicalize = classpathImporter.canonicalize("META-INF", false);

        assertThat(canonicalize).isNull();
    }

    @Test
    void canonicalizeAmbigous_file() {
        assertThatThrownBy(() -> {
            classpathImporter.canonicalize("META-INF/MANIFEST.MF", false);
        }).isNotNull();
    }
}