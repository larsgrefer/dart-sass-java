package de.larsgrefer.sass.embedded.logging;

import com.sass_lang.embedded_protocol.OutboundMessage.LogEvent;
import com.sass_lang.embedded_protocol.OutboundMessage.LogEventOrBuilder;

/**
 * Callback interface for {@link LogEvent log events} emitted by the sass compilation.
 *
 * @author Lars Grefer
 * @see de.larsgrefer.sass.embedded.SassCompiler#setLoggingHandler(LoggingHandler)
 */
public interface LoggingHandler {

    void handle(LogEventOrBuilder logEvent);
}
