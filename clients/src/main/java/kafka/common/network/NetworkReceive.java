package kafka.common.network;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;

/**
 * @author phongpq
 */

/**
 * A size delimited Receive that consists of a 4 byte network-ordered size N followed by N bytes of content
 */
public class NetworkReceive implements Receive {
    private final int source;
    private final ByteBuffer size;
    private ByteBuffer buffer;

    public NetworkReceive(int source) {
        this.source = source;
        this.size = ByteBuffer.allocate(4);
        this.buffer = null;
    }

    @Override
    public long readFrom(ScatteringByteChannel channel) throws IOException {
        System.out.println("Client Read data from server " + size.remaining());
        int read = 0;
        if (size.hasRemaining()) {
            int bytesRead = channel.read(size);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            System.out.println("Client read bytes is " + bytesRead);
            read += bytesRead;
            if (!size.hasRemaining()) {
                size.rewind();
                int requestSize = size.getInt();
                System.out.println("Request size " + requestSize);
                if (requestSize < 0) {
                    throw new IllegalStateException("Invalid request (size = " + requestSize + ")");
                }
                this.buffer = ByteBuffer.allocate(requestSize);
            }
        }

        if (buffer != null) {
            int bytesRead = channel.read(buffer);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            read += bytesRead;
        }
        return read;
    }

    @Override
    public boolean complete() {
        return !size.hasRemaining() && !buffer.hasRemaining();
    }

    public ByteBuffer payload() {
        return this.buffer;
    }

}
