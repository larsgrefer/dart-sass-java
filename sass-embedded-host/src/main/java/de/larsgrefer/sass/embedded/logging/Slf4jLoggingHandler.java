package de.larsgrefer.sass.embedded.logging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import sass.embedded_protocol.EmbeddedSass;

/**
 * Slf4j based {@link LoggingHandler} implementation.
 *
 * @author Lars Grefer
 */
@RequiredArgsConstructor
public class Slf4jLoggingHandler implements LoggingHandler {

    private final Logger logger;

    @Override
    public void handle(EmbeddedSass.OutboundMessage.LogEvent logEvent) {
        EmbeddedSass.OutboundMessage.LogEvent.Type type = logEvent.getType();

        switch (type) {
            case WARNING:
                logger.warn(logEvent.getMessage());
                break;
            case DEPRECATION_WARNING:
                logger.info(logEvent.getMessage());
                break;
            case DEBUG:
                logger.debug(logEvent.getMessage());
                break;
            case UNRECOGNIZED:
                break;
        }
    }
}
