package de.larsgrefer.sass.embedded;

import de.larsgrefer.sass.embedded.connection.ConnectionFactory;
import lombok.experimental.UtilityClass;

import java.io.IOException;

/**
 * @author Lars Grefer
 */
@UtilityClass
public class SassCompilerFactory {

    public static SassCompiler bundled() throws IOException {
        return new SassCompiler(ConnectionFactory.bundled());
    }
}
