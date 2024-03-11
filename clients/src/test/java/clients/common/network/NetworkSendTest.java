package clients.common.network;

import kafka.common.network.NetworkSend;
import org.junit.Assert;
import org.junit.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;


/**
 * @author phongpq
 */
public class NetworkSendTest {

    @Test
    public void createSimpleNetworkSend() {
        ByteBuffer[] byteBuffers = new ByteBuffer[1];
        byteBuffers[0] = ByteBuffer.allocate(10);

        byteBuffers[0].put((byte) 1);
        byteBuffers[0].putDouble(0.1);
        NetworkSend networkSend = new NetworkSend(0, byteBuffers);

        byteBuffers[0].flip();
        //read the data from the buffer
        var buffersInNetworkSend = networkSend.getBuffers();
        var info = buffersInNetworkSend[0].getInt();
        System.out.println(info);
        Assert.assertEquals(info, 1);

        ByteBuffer buffer = buffersInNetworkSend[1];
        byte data = buffer.get();
        System.out.println(data);
        Assert.assertEquals(info, 1);

        double data01 = buffer.getDouble();
        System.out.println(data01);
        Assert.assertEquals(data01, 0.1, 0);
    }
}
