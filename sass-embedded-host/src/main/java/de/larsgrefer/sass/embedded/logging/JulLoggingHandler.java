package de.larsgrefer.sass.embedded.logging;

import lombok.RequiredArgsConstructor;
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.LogEventOrBuilder;

import java.util.logging.Logger;

/**
 * Java Util Logging (JUL) based {@link LoggingHandler} implementation.
 *
 * @author Lars Grefer
 */
@RequiredArgsConstructor
public class JulLoggingHandler implements LoggingHandler {

    private final Logger logger;

    @Override
    public void handle(LogEventOrBuilder logEvent) {
        EmbeddedSass.OutboundMessage.LogEvent.Type type = logEvent.getType();

        switch (type) {
            case WARNING:
                logger.warning(logEvent.getMessage());
                break;
            case DEPRECATION_WARNING:
                logger.info(logEvent.getMessage());
                break;
            case DEBUG:
                logger.fine(logEvent.getMessage());
                break;
            case UNRECOGNIZED:
                break;
        }

    }
}
