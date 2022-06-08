package de.larsgrefer.sass.embedded.logging;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import sass.embedded_protocol.EmbeddedSass;

/**
 * Log4j2 based {@link LoggingHandler} implementation.
 *
 * @author Lars Grefer
 */
@RequiredArgsConstructor
public class Log4jLoggingHandler implements LoggingHandler {

    private final Logger logger;

    @Override
    public void handle(EmbeddedSass.OutboundMessage.LogEventOrBuilder logEvent) {
        Level level = getLogLevel(logEvent.getType());
        logger.log(level, logEvent.getFormatted());
    }

    protected Level getLogLevel(EmbeddedSass.LogEventType type) {
        switch (type) {
            case WARNING:
                return Level.WARN;
            case DEBUG:
                return Level.DEBUG;
            case DEPRECATION_WARNING:
            case UNRECOGNIZED:
            default:
                return Level.INFO;
        }
    }
}
