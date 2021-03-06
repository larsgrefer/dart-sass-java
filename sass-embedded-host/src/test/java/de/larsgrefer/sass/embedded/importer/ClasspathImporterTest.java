package de.larsgrefer.sass.embedded.importer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sass.embedded_protocol.EmbeddedSass;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class ClasspathImporterTest {

    private ClasspathImporter classpathImporter;

    @BeforeEach
    void setUp() {
        classpathImporter = new ClasspathImporter();
    }

    @Test
    void canonicalizeToImport() throws Exception {
        String canonicalize = classpathImporter.canonicalize("foo/bar.scss");

        assertThat(canonicalize).isNotNull();

        URL newUrl = new URL(canonicalize);

        EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess success = classpathImporter.handleImport(newUrl);
        assertThat(success).isNotNull();

        assertThat(success.getContents()).contains("red");
        assertThat(success.getSyntax()).isEqualTo(EmbeddedSass.InboundMessage.Syntax.SCSS);
    }

    @Test
    void canonicalizeToImport_jar() throws Exception {
        String canonicalize = classpathImporter.canonicalize("google/protobuf/type.proto");

        assertThat(canonicalize).isNotNull();

        URL newUrl = new URL(canonicalize);

        EmbeddedSass.InboundMessage.ImportResponse.ImportSuccess success = classpathImporter.handleImport(newUrl);
        assertThat(success).isNotNull();

        assertThat(success.getContents()).contains("CONSEQUENTIAL");
    }
}