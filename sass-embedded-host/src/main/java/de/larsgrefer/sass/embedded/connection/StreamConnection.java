package de.larsgrefer.sass.embedded.connection;

import sass.embedded_protocol.EmbeddedSass.InboundMessage;
import sass.embedded_protocol.EmbeddedSass.OutboundMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link CompilerConnection} implementation based on an {@link InputStream} {@link OutputStream} pair.
 *
 * @author Lars Grefer
 */
public abstract class StreamConnection implements CompilerConnection {

    protected abstract InputStream getInputStream() throws IOException;

    protected abstract OutputStream getOutputStream() throws IOException;

    @Override
    public void sendMessage(InboundMessage inboundMessage) throws IOException {
        OutputStream outputStream = getOutputStream();
        inboundMessage.writeDelimitedTo(outputStream);
        outputStream.flush();
    }

    @Override
    public OutboundMessage readResponse() throws IOException {
        return OutboundMessage.parseDelimitedFrom(getInputStream());
    }
}
