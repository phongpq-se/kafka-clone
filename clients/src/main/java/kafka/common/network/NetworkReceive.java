package kafka.common.network;

import java.nio.ByteBuffer;

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
        this.size = ByteBuffer.allocate(4);
        this.source = source;
    }
}
