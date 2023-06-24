package de.larsgrefer.sass.embedded.connection;

import com.google.protobuf.TextFormat;
import com.sass_lang.embedded_protocol.InboundMessage;
import com.sass_lang.embedded_protocol.OutboundMessage;
import lombok.extern.slf4j.Slf4j;

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
    public synchronized void sendMessage(Packet<InboundMessage> packet) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("{} --> {}", packet.getCompilationId(), TextFormat.printer().shortDebugString(packet.getMessage()));
        }

        if (packet.getMessage().hasVersionRequest()) {
            packet.setCompilationId(0);
        }

        packet.writeDelimitedTo(getOutputStream());
    }

    @Override
    public synchronized Packet<OutboundMessage> readResponse() throws IOException {
        InputStream inputStream = getInputStream();

        Packet<OutboundMessage> packet = Packet.parseDelimitedFrom(inputStream, OutboundMessage.parser());

        if (log.isTraceEnabled()) {
            log.trace("{} <-- {}", packet.getCompilationId(), TextFormat.printer().shortDebugString(packet.getMessage()));
        }
        return packet;
    }

}
