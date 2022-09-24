package de.larsgrefer.sass.embedded.util;

import org.junit.jupiter.api.Test;
import sass.embedded_protocol.EmbeddedSass.Syntax;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class SyntaxUtilTest {

    @Test
    void guessSyntax_file() {
        assertThat(SyntaxUtil.guessSyntax(new File("test.sass"))).isEqualTo(Syntax.INDENTED);
        assertThat(SyntaxUtil.guessSyntax(new File("test.scss"))).isEqualTo(Syntax.SCSS);
        assertThat(SyntaxUtil.guessSyntax(new File("test.css"))).isEqualTo(Syntax.CSS);

        assertThat(SyntaxUtil.guessSyntax(new File("test.txt"))).isEqualTo(Syntax.UNRECOGNIZED);
    }

    @Test
    void guessSyntax_url() throws MalformedURLException {
        assertThat(SyntaxUtil.guessSyntax(new URL("http://example.com/test.scss"))).isEqualTo(Syntax.SCSS);
        assertThat(SyntaxUtil.guessSyntax(new URL("http://example.com/dummy/test.sass?foo=bar"))).isEqualTo(Syntax.INDENTED);
        assertThat(SyntaxUtil.guessSyntax(new URL("http://example.com/dummy/test.txt?foo=bar"))).isEqualTo(Syntax.UNRECOGNIZED);
    }
}