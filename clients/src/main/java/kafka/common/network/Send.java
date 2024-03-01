package kafka.common.network;

import java.io.IOException;
import java.nio.channels.GatheringByteChannel;

/**
 * @author phongpq
 */
public interface Send {
    /**
     * The numeric id for the destination of this send
     */
    int destination();

    /**
     * Write some as-yet unwritten bytes from this send to the provided channel. It may take multiple calls for the send
     * to be completely written
     *
     * @param channel The channel to write to
     * @return The number of bytes written
     * @throws IOException If the write fails
     */
    long writeTo(GatheringByteChannel channel) throws IOException;
}
