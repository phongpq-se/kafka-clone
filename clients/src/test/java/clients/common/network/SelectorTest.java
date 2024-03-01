package clients.common.network;

import kafka.common.network.NetworkSend;
import kafka.common.network.Selector;
import lombok.AllArgsConstructor;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author phongpq
 */
public class SelectorTest {
    private static final List<NetworkSend> EMPTY = new ArrayList<NetworkSend>();
    private EchoServer server;
    private Selector selector;
    private final int defaultNode = 0;
    private static final int BUFFER_SIZE = 4 * 1024;

    @Before
    public void setUp() throws Exception {
        this.server = new EchoServer();
        this.server.start();
        selector = new Selector();
    }


    @Test
    public void connectToServer() throws IOException, InterruptedException {
        selector.connect(defaultNode, new InetSocketAddress("localhost", server.port), BUFFER_SIZE, BUFFER_SIZE);
        selector.poll(1000L, EMPTY);
        Thread.sleep(50000);
    }

    /**
     * A simple server that takes size delimited byte arrays and just echos them back to the sender.
     */

    static class EchoServer extends Thread {
        public final int port;
        private final ServerSocket serverSocket;
        private final List<Thread> threads;
        private final List<Socket> sockets;

        public EchoServer() throws Exception {
            this.port = 8080;
            this.serverSocket = new ServerSocket(port);
            this.threads = Collections.synchronizedList(new ArrayList<Thread>());
            this.sockets = Collections.synchronizedList(new ArrayList<Socket>());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    final Socket socket = serverSocket.accept();
                    sockets.add(socket);
                    Thread thread = new Thread(() -> {
                        try {
                            var input = new DataInputStream(socket.getInputStream());
                            System.out.println("info " + input.available());
                        } catch (IOException ex) {
                            //ignore
                        } finally {
                            try {
                                socket.close();
                            } catch (IOException e) {

                            }
                        }
                    });
                    thread.start();
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
