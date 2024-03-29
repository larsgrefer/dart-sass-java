package de.larsgrefer.sass.embedded.logging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class Slf4jLoggingHandlerTest extends LoggingHandlerTest {

    @Override
    protected LoggingHandler createLoggingHandler() {
        return new Slf4jLoggingHandler(log);
    }
}