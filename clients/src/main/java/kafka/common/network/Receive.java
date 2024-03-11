package kafka.common.network;

import java.io.IOException;
import java.nio.channels.ScatteringByteChannel;

/**
 * @author phongpq
 */
public interface Receive {

    /**
     * Read bytes into this receive from the given channel
     * @param channel The channel to read from
     * @return The number of bytes read
     * @throws IOException If the reading fails
     */
    long readFrom(ScatteringByteChannel channel) throws IOException;

    /**
     * Are we done receiving data?
     */
    public boolean complete();
}
