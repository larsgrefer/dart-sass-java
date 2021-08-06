package de.larsgrefer.sass.embedded.util;

import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass.Syntax;

import java.io.File;
import java.net.URL;

@UtilityClass
public class SyntaxUtil {

    public static Syntax guessSyntax(URL url) {
        return guessSyntax(url.getPath());
    }

    public static Syntax guessSyntax(File file) {
        return guessSyntax(file.getName());
    }

    private static Syntax guessSyntax(String path) {
        String lowerPath = path.toLowerCase();

        if (lowerPath.endsWith(".css")) {
            return Syntax.CSS;
        }

        if (lowerPath.endsWith(".scss")) {
            return Syntax.SCSS;
        }

        if (lowerPath.endsWith(".sass")) {
            return Syntax.INDENTED;
        }

        return Syntax.UNRECOGNIZED;
    }
}
