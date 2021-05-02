package de.larsgrefer.sass.embedded.connection;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Lars Grefer
 */
@RequiredArgsConstructor
public class SocketConnection extends StreamConnection {

    private final Socket socket;

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }
}
