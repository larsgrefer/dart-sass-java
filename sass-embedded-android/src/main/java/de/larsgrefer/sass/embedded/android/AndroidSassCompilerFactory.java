package de.larsgrefer.sass.embedded.android;

import android.content.Context;
import android.content.res.AssetManager;
import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.connection.ConnectionFactory;

import java.io.IOException;

/**
 * @author Lars Grefer
 * @see de.larsgrefer.sass.embedded.SassCompilerFactory
 */
public class AndroidSassCompilerFactory {

    public static SassCompiler bundled(Context context) throws IOException {
        SassCompiler sassCompiler = new SassCompiler(AndroidConnectionFactory.bundled(context));
        sassCompiler.setLoggingHandler(new AndroidLoggingHandler());
        return sassCompiler;
    }
}
