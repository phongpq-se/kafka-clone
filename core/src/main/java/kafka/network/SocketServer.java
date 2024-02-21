package kafka.network;

import kafka.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author phongpq
 */


@RequiredArgsConstructor
@Getter
public class SocketServer {

    private static Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final int brokerId;
    private final String host;
    private final int port;
    private final int numProcessorThreads;
    private final int maxQueuedRequests;
    private final int sendBufferSize;
    private final int recvBufferSize;

    private List<Processor> processors;

    private Acceptor acceptor = null;
    private RequestChannel requestChannel = null;

    /**
     * Start the socket server
     */
    public void startUp() {
        requestChannel = new RequestChannel(numProcessorThreads, maxQueuedRequests);
        processors = new ArrayList<>(numProcessorThreads);

        for (int i = 0; i < numProcessorThreads; i++) {
            processors.add(i, new Processor(i));
            Utils.newThread("kafka-network-thread-%d-%d".formatted(port, i), processors.get(i), false).start();
        }

        this.acceptor = new Acceptor(port, host, processors.toArray(new Processor[0]), sendBufferSize, recvBufferSize);
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
        private final int sendBufferSize;
        private final int recvBufferSize;

        @Override
        public void run() {
            var serverChannel = openServerSocket();
            try {
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                startupComplete();
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }

            var currentProcessor = 0;
            while (isRunning()) {
                try {
                    var ready = selector.select(500);
                    if (ready > 0) {
                        var keys = selector.selectedKeys();
                        var it = keys.iterator();
                        while (it.hasNext() && isRunning()) {
                            var key = it.next();
                            it.remove();
                            if (key.isAcceptable()) {
                                accept(key, processors[currentProcessor]);
                            } else {
                                throw new IllegalArgumentException("Unrecognized key state for acceptor thread of socket server: " + key);
                            }
                            currentProcessor = (currentProcessor + 1) % processors.length;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        protected void accept(SelectionKey key, Processor processor) {
            var serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = null;
            try {
                socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.socket().setTcpNoDelay(true);
                socketChannel.socket().setSendBufferSize(sendBufferSize);

                processor.accept(socketChannel);
                logger.debug("Accepted connection from %s on %s. sendBufferSize [actual|requested]: [%d|%d] recvBufferSize [actual|requested]: [%d|%d]"
                        .formatted(socketChannel.socket().getInetAddress(), socketChannel.socket().getLocalSocketAddress(),
                                socketChannel.socket().getSendBufferSize(), sendBufferSize,
                                socketChannel.socket().getReceiveBufferSize(), recvBufferSize
                        )
                );
            } catch (Exception e) {
                logger.info("Rejected connection from %s, address already has the configured maximum of %d connections.");
                close(socketChannel);
            }
        }

        private ServerSocketChannel openServerSocket() {
            var socketAddress = host == null || host.trim().isEmpty() ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
            try {
                var serverChannel = ServerSocketChannel.open();
                serverChannel.configureBlocking(false);
                serverChannel.socket().setReceiveBufferSize(recvBufferSize);
                serverChannel.socket().bind(socketAddress);
                System.out.println("Awaiting socket connections on %s:%d".formatted(socketAddress.getHostName(), port));
                return serverChannel;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
