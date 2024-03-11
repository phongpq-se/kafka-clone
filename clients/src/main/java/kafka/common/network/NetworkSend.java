package kafka.common.network;

import javax.sound.midi.Soundbank;
import java.nio.ByteBuffer;

/**
 * @author phongpq
 */

/**
 * A size delimited Send that consists of a 4 byte network-ordered size N followed by N bytes of content
 */
public class NetworkSend extends ByteBufferSend {
    public NetworkSend(int destination, ByteBuffer... buffers) {
        super(destination, sizeDelimit(buffers));
    }

    private static ByteBuffer[] sizeDelimit(ByteBuffer[] buffers) {
        System.out.println("Client received buffer length " + buffers.length);
        int size = 0;
        for (int i = 0; i < buffers.length; i++) {
            size += buffers[i].remaining();
        }
        System.out.println("Client receive size delimit " + size);
        ByteBuffer[] delimited = new ByteBuffer[buffers.length + 1];
        delimited[0] = ByteBuffer.allocate(4);
        delimited[0].putInt(size);
        delimited[0].rewind();
        System.arraycopy(buffers, 0, delimited, 1, buffers.length);
        return delimited;
    }

}
