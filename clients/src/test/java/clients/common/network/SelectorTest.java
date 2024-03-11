package clients.common.network;

import kafka.common.network.NetworkSend;
import kafka.common.network.Selector;
import lombok.AllArgsConstructor;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author phongpq
 */
public class SelectorTest {
    private static final List<NetworkSend> EMPTY = new ArrayList<NetworkSend>();
    private EchoServer server;
    private Selector selector;
    private final int defaultNode = 0;
    private static final int BUFFER_SIZE = 4 * 1024;
    private static final long timeout = 1000L;

    @Before
    public void setUp() throws Exception {
        this.server = new EchoServer();
        this.server.start();
        selector = new Selector();
    }


    @Test
    public void simpleTest() throws InterruptedException {
        // Thread.sleep(Long.MAX_VALUE);
        while (true) {
            System.out.println("ok");
            Thread.sleep(1000L);
        }
    }

    @Test
    public void connectToServer() throws IOException, InterruptedException {
        blockingConnect(defaultNode);
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void connectToServerAndSendMessage() throws IOException, InterruptedException {
        blockingConnect(defaultNode);
        blockingRequest(defaultNode, "Hello world");
        Thread.sleep(Long.MAX_VALUE);
    }

    private void blockingConnect(int node) throws IOException {
        selector.connect(defaultNode, new InetSocketAddress("localhost", server.port), BUFFER_SIZE, BUFFER_SIZE);
        while (!selector.getConnected().contains(node)) {
            selector.poll(10000L, EMPTY);
        }
    }

    private String blockingRequest(int node, String s) throws IOException {
        selector.poll(timeout, List.of(createNetworkSend(node, s)));
        while (true) {
            selector.poll(timeout, EMPTY);
            return "";
        }
    }

    private NetworkSend createNetworkSend(int node, String s) {
        byte[] inputBytes = s.getBytes(StandardCharsets.UTF_8);


//        ByteBuffer[] byteBuffers = new ByteBuffer[1];
//        byteBuffers[0] = ByteBuffer.allocate(inputBytes.length);
//        byteBuffers[0].put(inputBytes);
        return new NetworkSend(node, ByteBuffer.wrap(s.getBytes()));
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
                    System.out.println("Run echoServer");
                    final Socket socket = serverSocket.accept();
                    System.out.println("After accept connection");
                    sockets.add(socket);
                    Thread thread = new Thread(() -> {
                        try {
                            var input = new DataInputStream(socket.getInputStream());
                            var output = new DataOutputStream(socket.getOutputStream());
                            System.out.println("Server connect to " + socket.isConnected() + socket.isClosed());
                            while (socket.isConnected() && !socket.isClosed()) {
                                try {
                                    System.out.println("Server waiting read 4 bytes data from client");
                                    var size = input.readInt();
                                    System.out.println("Server receive the buffer has delimit size " + size);
                                    byte[] bytes = new byte[size];
                                    input.readFully(bytes);
                                    String result = new String(bytes, StandardCharsets.UTF_8);
                                    System.out.println("Server read data from client " + result);
                                    output.writeInt(size);
                                    output.write(bytes);
                                    output.flush();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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
                e.printStackTrace();
                // ignore
            }
        }
    }
}
