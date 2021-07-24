package de.larsgrefer.sass.embedded.logging;

import sass.embedded_protocol.EmbeddedSass.OutboundMessage.LogEvent;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.LogEventOrBuilder;

/**
 * Callback interface for {@link LogEvent log events} emitted by the sass compilation.
 *
 * @author Lars Grefer
 * @see de.larsgrefer.sass.embedded.SassCompiler#setLoggingHandler(LoggingHandler)
 */
public interface LoggingHandler {

    void handle(LogEventOrBuilder logEvent);
}
