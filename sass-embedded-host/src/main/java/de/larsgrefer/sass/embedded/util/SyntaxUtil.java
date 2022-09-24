package de.larsgrefer.sass.embedded.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import sass.embedded_protocol.EmbeddedSass.Syntax;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

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

    @Nonnull
    public static Syntax guessSyntax(URLConnection urlConnection) {

        String contentType = urlConnection.getContentType();

        if (contentType != null) {
            contentType = contentType.toLowerCase(Locale.ROOT);

            if (contentType.startsWith("text/css")) {
                return Syntax.CSS;
            }

            if (contentType.startsWith("text/x-scss")) {
                return Syntax.SCSS;
            }

            if (contentType.startsWith("text/x-sass")) {
                return Syntax.INDENTED;
            }
        }

        return guessSyntax(urlConnection.getURL());
    }
}
