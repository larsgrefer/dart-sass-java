package de.larsgrefer.sass.embedded.android;

import android.util.Log;
import com.sass_lang.embedded_protocol.LogEventType;
import com.sass_lang.embedded_protocol.OutboundMessage;
import de.larsgrefer.sass.embedded.logging.LoggingHandler;

/**
 * {@link LoggingHandler} implementation which delegates to {@link android.util.Log}.
 *
 * @author Lars Grefer
 * @see LoggingHandler
 * @see de.larsgrefer.sass.embedded.SassCompiler#setLoggingHandler(LoggingHandler)
 */
public class AndroidLoggingHandler implements LoggingHandler {
    @Override
    public void handle(OutboundMessage.LogEventOrBuilder logEvent) {
        LogEventType type = logEvent.getType();

        switch (type) {
            case WARNING:
                Log.w("dart-sass", logEvent.getFormatted());
                break;
            case DEPRECATION_WARNING:
                Log.i("dart-sass", logEvent.getFormatted());
                break;
            case DEBUG:
                Log.d("dart-sass", logEvent.getFormatted());
                break;
            case UNRECOGNIZED:
                break;
        }
    }
}
