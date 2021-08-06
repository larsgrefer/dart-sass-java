package de.larsgrefer.sass.embedded.logging;

import lombok.RequiredArgsConstructor;
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.LogEventOrBuilder;

import java.util.logging.Level;
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
        Level logLevel = getLogLevel(logEvent.getType());
        logger.log(logLevel, logEvent.getFormatted());
    }

    protected Level getLogLevel(EmbeddedSass.LogEventType type) {
        switch (type) {
            case WARNING:
            case DEPRECATION_WARNING:
                return Level.WARNING;
            case DEBUG:
                return Level.FINE;
            case UNRECOGNIZED:
            default:
                return Level.INFO;
        }
    }
}
