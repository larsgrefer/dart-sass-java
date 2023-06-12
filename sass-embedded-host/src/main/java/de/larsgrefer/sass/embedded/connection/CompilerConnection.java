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
     * @param inboundMessage The {@link InboundMessage} to send.
     * @throws IOException If the communication with the compiler fails.
     */
    void sendMessage(int compilationId, InboundMessage inboundMessage) throws IOException;

    /**
     * Read a {@link OutboundMessage} from the compiler.
     *
     * @return The next {@link OutboundMessage} sent by the compiler.
     * @throws IOException If the communication with the compiler fails.
     */
    OutboundMessage readResponse() throws IOException;

}
