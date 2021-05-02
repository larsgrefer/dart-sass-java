package de.larsgrefer.sass.embedded;

import de.larsgrefer.sass.embedded.connection.ConnectionFactory;

import java.io.IOException;

/**
 * @author Lars Grefer
 */
public class SassCompilerFactory {

    public static SassCompiler bundled() throws IOException {
        return new SassCompiler(ConnectionFactory.bundled());
    }
}
