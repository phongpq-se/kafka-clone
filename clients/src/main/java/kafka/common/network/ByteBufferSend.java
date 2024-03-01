package kafka.common.network;

import lombok.RequiredArgsConstructor;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

/**
 * @author phongpq
 */

@RequiredArgsConstructor
public class ByteBufferSend implements Send {
    private final int destination;
    protected final ByteBuffer[] buffers;
    private int remaining;
    private int size;

    @Override
    public int destination() {
        return destination;
    }

    @Override
    public long writeTo(GatheringByteChannel channel) throws IOException {
        long written = channel.write(buffers);
        if (written < 0) {
            throw new EOFException("This shouldn't happen.");
        }
        remaining -= written;
        return written;
    }

}
