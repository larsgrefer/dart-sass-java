package de.larsgrefer.sass.embedded.logging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import sass.embedded_protocol.EmbeddedSass;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage.LogEventOrBuilder;

/**
 * Slf4j based {@link LoggingHandler} implementation.
 *
 * @author Lars Grefer
 */
@RequiredArgsConstructor
public class Slf4jLoggingHandler implements LoggingHandler {

    private final Logger logger;

    @Override
    public void handle(LogEventOrBuilder logEvent) {
        EmbeddedSass.LogEventType type = logEvent.getType();

        switch (type) {
            case WARNING:
                logger.warn(logEvent.getFormatted());
                break;
            case DEPRECATION_WARNING:
                logger.info(logEvent.getFormatted());
                break;
            case DEBUG:
                logger.debug(logEvent.getFormatted());
                break;
            case UNRECOGNIZED:
                break;
        }
    }
}
