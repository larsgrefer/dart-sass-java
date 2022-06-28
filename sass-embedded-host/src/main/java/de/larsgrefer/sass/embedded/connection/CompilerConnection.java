package de.larsgrefer.sass.embedded.connection;

import sass.embedded_protocol.EmbeddedSass.InboundMessage;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Lars Grefer
 */
public interface CompilerConnection extends Closeable {

    void sendMessage(InboundMessage inboundMessage) throws IOException;

    OutboundMessage readResponse() throws IOException;

}
