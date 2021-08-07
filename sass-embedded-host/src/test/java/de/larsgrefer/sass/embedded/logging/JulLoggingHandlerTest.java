package de.larsgrefer.sass.embedded.logging;

import java.util.logging.Logger;

class JulLoggingHandlerTest extends LoggingHandlerTest {

    @Override
    protected LoggingHandler createLoggingHandler() {
        return new JulLoggingHandler(Logger.getLogger(this.getClass().getName()));
    }
}