package de.larsgrefer.sass.embedded.importer;

import com.sass_lang.embedded_protocol.InboundMessage;
import com.sass_lang.embedded_protocol.Syntax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        InboundMessage.ImportResponse.ImportSuccess success = classpathImporter.handleImport(newUrl);
        assertThat(success).isNotNull();

        assertThat(success.getContents()).contains("red");
        assertThat(success.getSyntax()).isEqualTo(Syntax.SCSS);
    }

    @Test
    void canonicalizeToImport_jar() throws Exception {
        String canonicalize = classpathImporter.canonicalize("META-INF/resources/webjars/bootstrap/5.2.0/scss/bootstrap.scss", false);

        assertThat(canonicalize).isNotNull();

        URL newUrl = new URL(canonicalize);

        InboundMessage.ImportResponse.ImportSuccess success = classpathImporter.handleImport(newUrl);
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
        assertThatThrownBy(() -> classpathImporter.canonicalize("META-INF/MANIFEST.MF", false)).isNotNull();
    }
}