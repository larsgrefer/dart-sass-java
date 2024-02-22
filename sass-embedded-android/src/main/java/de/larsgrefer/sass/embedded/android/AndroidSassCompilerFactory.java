package de.larsgrefer.sass.embedded.android;

import android.content.Context;
import androidx.annotation.NonNull;
import de.larsgrefer.sass.embedded.SassCompiler;

import java.io.IOException;

/**
 * @author Lars Grefer
 * @see de.larsgrefer.sass.embedded.SassCompilerFactory
 */
public class AndroidSassCompilerFactory {

    @NonNull
    public static SassCompiler bundled(@NonNull Context context) throws IOException {
        SassCompiler sassCompiler = new SassCompiler(AndroidConnectionFactory.bundled(context));
        sassCompiler.setLoggingHandler(new AndroidLoggingHandler());
        return sassCompiler;
    }
}
