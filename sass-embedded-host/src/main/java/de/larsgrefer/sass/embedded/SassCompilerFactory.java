package de.larsgrefer.sass.embedded;

import androidx.annotation.RequiresApi;
import de.larsgrefer.sass.embedded.connection.ConnectionFactory;
import de.larsgrefer.sass.embedded.util.PlatformUtils;
import lombok.experimental.UtilityClass;

import java.io.IOException;

/**
 * @author Lars Grefer
 */
@RequiresApi(1000)
@UtilityClass
public class SassCompilerFactory {

    /**
     * Create a new {@link SassCompiler} based on the dart-sass-embedded binary bundled in this jar.
     * <p>
     * Note that this is an expensive operation as it spawns a new process.
     * So check if you can re-use the {@link SassCompiler} instance and make sure to
     * {@link SassCompiler#close() close} it when you're done.
     *
     * @return A freshly created {@link SassCompiler} based on a new subprocess.
     * @throws IOException
     */
    public static SassCompiler bundled() throws IOException {
        try {
            return new SassCompiler(ConnectionFactory.bundled());
        } catch (RuntimeException e) {
            if (PlatformUtils.isAndroid()) {
                throw new IllegalStateException("Use AndroidSassCompilerFactory on Android", e);
            } else {
                throw e;
            }
        }
    }

    public static SassCompiler downloaded() throws IOException {
        return new SassCompiler(ConnectionFactory.downloaded());
    }
}
