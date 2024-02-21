package kafka.network;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author phongpq
 */
@RequiredArgsConstructor
@Getter
@Setter
public class Processor extends AbstractServerThread {

    private final static Logger logger = LogManager.getLogger(Processor.class);

    private final int id;

    private Long currentTimeNanos = System.nanoTime();
    private ConcurrentLinkedQueue<SocketChannel> newConnections = new ConcurrentLinkedQueue<>();
    private final LinkedHashMap<SelectionKey, Long> lruConnections = new LinkedHashMap<>();

    @Override
    public void run() {
        startupComplete();
        logger.info("Processor " + id + " is running");
        while (isRunning()) {
            try {
                configureNewConnections();
                // setup any new connections that have been queued up
                logger.trace("Processor id " + id);
                var ready = selector.select(300);
                if (ready > 0) {
                    Thread.sleep(1000);
                    logger.info("Processor " + id + " is running");
                    var keys = selector.selectedKeys();
                    var iter = keys.iterator();
                    while (iter.hasNext() && isRunning()) {
                        SelectionKey key = null;
                        try {
                            key = iter.next();
                            iter.remove();
                            if (key.isReadable()) {
                                logger.info("Readable");
                                read(key);
                            } else if (key.isWritable()) {
                                logger.info("Writable");
                            } else if (!key.isValid()) {
                                logger.info("Closed");
                                close(key);
                            } else {
                                throw new EOFException("Unrecognized key state for processor thread.");
                            }
                        } catch (EOFException ex) {
                            logger.info("Closing socket connection to %s due to invalid request: %s".formatted(key, ex.getMessage()));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error in processor " + id, e);
                throw new RuntimeException(e);
            }
            // do something
        }
        logger.debug("Closing selector");
    }

    /*
     * Process reads from ready sockets
     */
    void read(SelectionKey key) {
        logger.info("Reading from " + key);
        lruConnections.put(key, currentTimeNanos);
    }

    void write(SelectionKey key) {
        logger.info("Writing to " + key);

    }

    private void configureNewConnections() {
        if (!newConnections.isEmpty()) {
            logger.info("num connections " + newConnections.size());
        }
        while (!newConnections.isEmpty()) {
            var channel = newConnections.poll();
            try {
                logger.debug("Processor " + id + " listening to new connection from " + channel.socket().getRemoteSocketAddress());
                channel.register(selector, SelectionKey.OP_READ);
            } catch (IOException e) {
                logger.error("Error in processor " + id, e);
                throw new RuntimeException(e);
            }
        }
    }

    public void accept(SocketChannel socketChannel) {
        newConnections.add(socketChannel);
        wakeup();
    }

    @Override
    public void close(SelectionKey key) {
        super.close(key);
    }
}
