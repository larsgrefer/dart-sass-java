package de.larsgrefer.sass.embedded.connection;

import com.google.protobuf.TextFormat;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public abstract class StreamConnection implements CompilerConnection {

    protected abstract InputStream getInputStream() throws IOException;

    protected abstract OutputStream getOutputStream() throws IOException;

    @Override
    public synchronized void sendMessage(InboundMessage inboundMessage) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("--> {}", TextFormat.printer().shortDebugString(inboundMessage));
        }
        OutputStream outputStream = getOutputStream();
        inboundMessage.writeDelimitedTo(outputStream);
        outputStream.flush();
    }

    @Override
    public synchronized OutboundMessage readResponse() throws IOException {
        OutboundMessage outboundMessage = OutboundMessage.parseDelimitedFrom(getInputStream());
        if (log.isTraceEnabled()) {
            log.trace("<-- {}", TextFormat.printer().shortDebugString(outboundMessage));
        }
        return outboundMessage;
    }
}
