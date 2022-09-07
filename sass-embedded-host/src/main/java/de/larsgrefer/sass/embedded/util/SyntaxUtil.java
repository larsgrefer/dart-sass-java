package de.larsgrefer.sass.embedded.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass.Syntax;

import java.io.File;
import java.net.URL;

/**
 * @author Lars Grefer
 */
@UtilityClass
public class SyntaxUtil {

    public static Syntax guessSyntax(@NonNull URL url) {
        return guessSyntax(url.getPath());
    }

    public static Syntax guessSyntax(@NonNull File file) {
        return guessSyntax(file.getName());
    }

    public static Syntax guessSyntax(@NonNull String path) {
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
