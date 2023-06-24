package de.larsgrefer.sass.embedded.connection;

import com.sass_lang.embedded_protocol.InboundMessage;
import com.sass_lang.embedded_protocol.OutboundMessage;

import java.io.Closeable;
import java.io.IOException;

/**
 * Abstract representation of a connection to an embedded sass compiler.
 *
 * @author Lars Grefer
 * @see ProcessConnection
 * @see ConnectionFactory#bundled()
 */
public interface CompilerConnection extends Closeable {

    /**
     * Send the given {@link InboundMessage} to the compiler.
     *
     * @param compilationId  The compilationId to send.
     * @param inboundMessage The {@link InboundMessage} to send.
     * @throws IOException If the communication with the compiler fails.
     */
    default void sendMessage(int compilationId, InboundMessage inboundMessage) throws IOException {
        if (inboundMessage.hasVersionRequest()) {
            compilationId = 0;
        }

        sendMessage(new Packet<>(compilationId, inboundMessage));
    }

    /**
     * Send the given {@link Packet<InboundMessage>} to the compiler.
     *
     * @param inboundMessagePacket The {@link InboundMessage} to send.
     * @throws IOException If the communication with the compiler fails.
     */
    void sendMessage(Packet<InboundMessage> inboundMessagePacket) throws IOException;

    /**
     * Read a {@link OutboundMessage} from the compiler.
     *
     * @return The next {@link OutboundMessage} sent by the compiler.
     * @throws IOException If the communication with the compiler fails.
     */
    Packet<OutboundMessage> readResponse() throws IOException;

}
