package kafka.network;

import kafka.utils.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author phongpq
 */
public class ProcessorTest {

    private final Processor processor =
            new Processor(1);

    @Before
    public void startUp() throws IOException {
        // Open a ServerSocketChannel and bind it to a port
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);

        //connect to server
        Socket socket = new Socket("localhost", 8080);
        System.out.println("Connected to " + socket.getRemoteSocketAddress());

        // Accept a connection made to this channel's socket
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        processor.getNewConnections().add(socketChannel);

        Utils.newThread("processor test", processor, false).start();
    }

    @Test
    public void simpleTest_readable() throws InterruptedException {
//        Thread.sleep(10000);
//        Assert.assertFalse(processor.getLruConnections().isEmpty());
        System.out.println("ok");
        Thread.sleep(20000);
    }
}
