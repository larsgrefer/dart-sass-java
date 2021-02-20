package de.larsgrefer.sass.embedded.logging;

import sass.embedded_protocol.EmbeddedSass;

/**
 * Callback interface for {@link sass.embedded_protocol.EmbeddedSass.OutboundMessage.LogEvent log events}
 * emitted by the sass compilation.
 *
 * @author Lars Grefer
 * @see de.larsgrefer.sass.embedded.SassCompiler#setLoggingHandler(LoggingHandler)
 */
public interface LoggingHandler {

    void handle(EmbeddedSass.OutboundMessage.LogEvent logEvent);
}
