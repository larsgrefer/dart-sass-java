package de.larsgrefer.sass.embedded.logging;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class JulLoggingHandlerTest extends LoggingHandlerTest {

    @Override
    protected LoggingHandler createLoggingHandler() {
        return new JulLoggingHandler(Logger.getLogger(this.getClass().getName()));
    }
}