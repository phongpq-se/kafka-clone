package kafka.network;

import jdk.jshell.spi.ExecutionControl;
import kafka.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author phongpq
 */


@RequiredArgsConstructor
@Getter
public class SocketServer {

    private final int brokerId;
    private final String host;
    private final int port;
    private final int numProcessorThreads;
    private List<Processor> processors;

    private Acceptor acceptor = null;

    /**
     * Start the socket server
     */
    public void startUp()  {
        processors = new ArrayList<>(numProcessorThreads);

        for (int i = 0; i < numProcessorThreads; i++) {
            processors.add(i, new Processor(i));
            Utils.newThread("kafka-network-thread-%d-%d".formatted(port, i), processors.get(i), false).start();
        }

        this.acceptor = new Acceptor(port, host, processors.toArray(new Processor[0]));
        Utils.newThread("kafka-socket-acceptor", acceptor, false).start();

        try {
            acceptor.awaitStartup();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Started");
    }

    /**
     * Thread that accepts and configures new connections. There is only need for one of these
     */
    @AllArgsConstructor
    static class Acceptor extends AbstractServerThread {
        private final int port;
        private final String host;
        private final Processor[] processors;

        @Override
        public void run() {
            var serverChannel = openServerSocket();
            while (isRunning()) {
                try {
                    var socketChannel = serverChannel.accept();
                    System.out.println("Accepted connection from " + socketChannel.getRemoteAddress());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private ServerSocketChannel openServerSocket() {
            var socketAddress = host == null || host.trim().isEmpty() ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
            try {
                var serverChannel = ServerSocketChannel.open();
                serverChannel.configureBlocking(false);
                serverChannel.socket().setReceiveBufferSize(1024 * 1024);
                serverChannel.socket().bind(socketAddress);
                System.out.println("Awaiting socket connections on %s:%d".formatted(socketAddress.getHostName(), port));
                return serverChannel;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
