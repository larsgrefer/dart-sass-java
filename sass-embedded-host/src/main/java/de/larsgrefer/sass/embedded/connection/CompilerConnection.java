package de.larsgrefer.sass.embedded.connection;

import sass.embedded_protocol.EmbeddedSass;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Lars Grefer
 */
public interface CompilerConnection extends Closeable {

    void sendMessage(EmbeddedSass.InboundMessage inboundMessage) throws IOException;

    EmbeddedSass.OutboundMessage readResponse() throws IOException;

}
