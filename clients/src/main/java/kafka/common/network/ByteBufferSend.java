package kafka.common.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

/**
 * @author phongpq
 */

@Getter
public class ByteBufferSend implements Send {
    private final int destination;
    protected final ByteBuffer[] buffers;
    private int remaining;
    private int size;

    public ByteBufferSend(int destination, ByteBuffer... buffers) {
        this.destination = destination;
        this.buffers = buffers;
        for (ByteBuffer buffer : buffers) {
            remaining += buffer.remaining();
        }
        this.size = remaining;
    }


    @Override
    public int destination() {
        return destination;
    }

    @Override
    public long writeTo(GatheringByteChannel channel) throws IOException {
        long written = channel.write(buffers);
        System.out.println("Send the Byte Buffers to server has length " + buffers.length + " then written " + written);
        if (written < 0) {
            throw new EOFException("This shouldn't happen.");
        }
        remaining -= written;
        return written;
    }

}
