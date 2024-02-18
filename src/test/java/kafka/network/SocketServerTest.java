package kafka.network;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

/**
 * @author phongpq
 */
public class SocketServerTest {

    SocketServer socketServer = new SocketServer(1, null, 9092, 1);

    @Before
    public void setUp() {
        socketServer.startUp();
    }

    @Test
    public void simple() throws InterruptedException {
        System.out.println("ok");
        Thread.sleep(1000000);
    }

    @Test
    public void simpleRequest() {
        Socket socket = connect(socketServer);
        System.out.println("Connected to " + socketServer.getPort());

    }

    private Socket connect(SocketServer socketServer) {
        try {
            return new Socket("localhost", socketServer.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
