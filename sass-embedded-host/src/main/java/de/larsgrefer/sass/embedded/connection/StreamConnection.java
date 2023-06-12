package de.larsgrefer.sass.embedded.connection;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.TextFormat;
import com.sass_lang.embedded_protocol.InboundMessage;
import com.sass_lang.embedded_protocol.OutboundMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.FilterInputStream;
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
    public synchronized void sendMessage(int compilationId, InboundMessage inboundMessage) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("{} --> {}", compilationId, TextFormat.printer().shortDebugString(inboundMessage));
        }

        if (inboundMessage.hasVersionRequest()) {
            compilationId = 0;
        }

        int compilationIdSize = CodedOutputStream.computeUInt32SizeNoTag(compilationId);
        int messageSize = inboundMessage.getSerializedSize();

        int fullSize = compilationIdSize + messageSize;


        int bufferSize = CodedOutputStream.computeUInt32SizeNoTag(fullSize) + fullSize;
        if (bufferSize > CodedOutputStream.DEFAULT_BUFFER_SIZE) {
            bufferSize = CodedOutputStream.DEFAULT_BUFFER_SIZE;
        }

        OutputStream outputStream = getOutputStream();

        final CodedOutputStream codedOutput = CodedOutputStream.newInstance(outputStream, bufferSize);
        codedOutput.writeUInt32NoTag(fullSize);
        codedOutput.writeUInt32NoTag(compilationId);
        inboundMessage.writeTo(codedOutput);
        codedOutput.flush();
        outputStream.flush();
    }

    @Override
    public synchronized OutboundMessage readResponse() throws IOException {
        InputStream inputStream = getInputStream();

        int firstByte = inputStream.read();
        int fullSize = CodedInputStream.readRawVarint32(firstByte, inputStream);

        inputStream = new LimitedInputStream(inputStream, fullSize);

        firstByte = inputStream.read();
        int compilationId = CodedInputStream.readRawVarint32(firstByte, inputStream);

        OutboundMessage outboundMessage = OutboundMessage.parseFrom(inputStream);
        if (log.isTraceEnabled()) {
            log.trace("{} <-- {}", compilationId, TextFormat.printer().shortDebugString(outboundMessage));
        }
        return outboundMessage;
    }

    /**
     * Copied from {@link AbstractMessageLite.Builder}
     */
    static final class LimitedInputStream extends FilterInputStream {
        private int limit;

        LimitedInputStream(InputStream in, int limit) {
            super(in);
            this.limit = limit;
        }

        @Override
        public int available() throws IOException {
            return Math.min(super.available(), limit);
        }

        @Override
        public int read() throws IOException {
            if (limit <= 0) {
                return -1;
            }
            final int result = super.read();
            if (result >= 0) {
                --limit;
            }
            return result;
        }

        @Override
        public int read(final byte[] b, final int off, int len) throws IOException {
            if (limit <= 0) {
                return -1;
            }
            len = Math.min(len, limit);
            final int result = super.read(b, off, len);
            if (result >= 0) {
                limit -= result;
            }
            return result;
        }

        @Override
        public long skip(final long n) throws IOException {
            // because we take the minimum of an int and a long, result is guaranteed to be
            // less than or equal to Integer.MAX_INT so this cast is safe
            int result = (int) super.skip(Math.min(n, limit));
            if (result >= 0) {
                // if the superclass adheres to the contract for skip, this condition is always true
                limit -= result;
            }
            return result;
        }
    }
}
