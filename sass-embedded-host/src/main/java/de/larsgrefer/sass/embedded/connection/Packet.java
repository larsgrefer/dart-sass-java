package de.larsgrefer.sass.embedded.connection;

import com.google.protobuf.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents a "packet" as specified by the embedded sass protocol.
 *
 * @param <T> Type of the protobuf message
 * @author Lars Grefer
 */
@Data
@AllArgsConstructor
public class Packet<T extends Message> {

    private int compilationId;

    private T message;

    /**
     * @see Message#getSerializedSize()
     */
    public int getSerializedSize() {
        return CodedOutputStream.computeUInt32SizeNoTag(compilationId) + message.getSerializedSize();
    }


    /**
     * @see Message#writeDelimitedTo(OutputStream)
     */
    public void writeDelimitedTo(OutputStream outputStream) throws IOException {

        int fullSize = getSerializedSize();

        int bufferSize = CodedOutputStream.computeUInt32SizeNoTag(fullSize) + fullSize;
        if (bufferSize > CodedOutputStream.DEFAULT_BUFFER_SIZE) {
            bufferSize = CodedOutputStream.DEFAULT_BUFFER_SIZE;
        }


        final CodedOutputStream codedOutput = CodedOutputStream.newInstance(outputStream, bufferSize);
        codedOutput.writeUInt32NoTag(fullSize);
        codedOutput.writeUInt32NoTag(compilationId);
        message.writeTo(codedOutput);
        codedOutput.flush();
        outputStream.flush();
    }

    /**
     * @see com.sass_lang.embedded_protocol.OutboundMessage#parseDelimitedFrom(InputStream)
     */
    public static <T extends Message> Packet<T> parseDelimitedFrom(InputStream inputStream, Parser<T> parser) throws IOException {

        int firstByte = inputStream.read();
        int fullSize = CodedInputStream.readRawVarint32(firstByte, inputStream);

        inputStream = new LimitedInputStream(inputStream, fullSize);

        firstByte = inputStream.read();
        int compilationId = CodedInputStream.readRawVarint32(firstByte, inputStream);

        try {
            T message = parser.parseFrom(inputStream);
            return new Packet<>(compilationId, message);
        } catch (InvalidProtocolBufferException e) {
            throw e.unwrapIOException();
        }

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
