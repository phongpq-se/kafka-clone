package kafka.common.network;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.*;
import java.util.ArrayList;

/**
 * @author phongpq
 */

@Getter
public class Selector implements Selectable {
    private static final Logger log = LoggerFactory.getLogger(Selector.class);
    private final List<Integer> connected;
    private final java.nio.channels.Selector selector;
    private final Map<Integer, SelectionKey> keys;
    private final List<NetworkSend> completedSends;
    private final List<NetworkReceive> completedReceives;

    public Selector() {
        try {
            this.selector = java.nio.channels.Selector.open();
            this.connected = new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.completedSends = new ArrayList<>();
        this.completedReceives = new ArrayList<>();
        this.keys = new HashMap<>();
    }

    @Override
    public void connect(int id, InetSocketAddress address, int sendBufferSize, int receiveBufferSize) throws IOException {
        if (this.keys.containsKey(id)) {
            throw new IllegalStateException("There is already a connection for id " + id);
        }
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        socket.setSendBufferSize(sendBufferSize);
        socket.setReceiveBufferSize(receiveBufferSize);
        socket.setTcpNoDelay(true);
        try {
            channel.connect(address);
            log.info("Connect to address " + address.getAddress());
        } catch (UnresolvedAddressException e) {
            channel.close();
            throw new IOException("Can't resolve address: " + address, e);
        } catch (IOException e) {
            channel.close();
            throw e;
        }
        SelectionKey key = channel.register(this.selector, SelectionKey.OP_CONNECT);
        key.attach(new Transmissions(id));
        this.keys.put(id, key);
    }

    /**
     * Send the data to server after connected
     *
     * @param timeout The amount of time to block if there is nothing to do
     * @param sends   The new sends to initiate
     * @throws IOException
     */
    @Override
    public void poll(long timeout, List<NetworkSend> sends) throws IOException {
        /* register for write interest on any new sends */
        for (NetworkSend send : sends) {
            SelectionKey key = keyForId(send.destination());
            Transmissions transmissions = transmissions(key);
            if (transmissions.hasSend()) {
                throw new IllegalStateException("Attempt to begin a send operation with prior send operation still in progress.");
            }
            transmissions.send = send;
            try {
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            } catch (CancelledKeyException e) {
                close(key);
            }
        }

        int readyKeys = select(timeout);
        if (readyKeys > 0) {
            Set<SelectionKey> keys = this.selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                Transmissions transmissions = transmissions(key);
                SocketChannel channel = channel(key);
                if (key.isConnectable()) {
                    log.info("Connectable " + transmissions.id);
                    channel.finishConnect();
                    key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
                    this.connected.add(transmissions.id);
                }
                /* read from any connections that have readable data */
                if (key.isReadable()) {
                    if (!transmissions.hasReceive()) {
                        transmissions.receive = new NetworkReceive(transmissions.id);
                    }
                    transmissions.receive.readFrom(channel);
                    if (transmissions.receive.complete()) {
                        log.info("Client completed read");
                        this.completedReceives.add(transmissions.receive);
                        transmissions.receive.payload().rewind();
                        transmissions.clearReceive();
                    }
                    log.info("Readable " + transmissions.id);
                }
                /* write to any sockets that have space in their buffer and for which we have data */
                if (key.isWritable()) {
                    log.info("Writable " + transmissions.id);
                    transmissions.send.writeTo(channel);
                    if (transmissions.send.getRemaining() <= 0) {
                        this.completedSends.add(transmissions.send);
                        log.info("Client completed send in transmission " + transmissions.id);
                        transmissions.clearSend();
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    }
                }

            }
        }
    }

    /**
     * Check for data, waiting up to the given timeout.
     *
     * @param ms Length of time to wait, in milliseconds. If negative, wait indefinitely.
     * @return The number of keys ready
     * @throws IOException
     */
    private int select(long ms) throws IOException {
        if (ms == 0L) {
            return this.selector.selectNow();
        } else if (ms < 0L) {
            return this.selector.select();
        } else {
            return this.selector.select(ms);
        }
    }

    /**
     * Get the transmissions for the given connection
     */
    private Transmissions transmissions(SelectionKey key) {
        return (Transmissions) key.attachment();
    }

    /**
     * Get the socket channel associated with this selection key
     */
    private SocketChannel channel(SelectionKey key) {
        return (SocketChannel) key.channel();
    }

    /**
     * Get the selection key associated with this numeric id
     */
    private SelectionKey keyForId(int id) {
        SelectionKey key = this.keys.get(id);
        if (key == null) {
            throw new IllegalStateException("Attempt to write to socket for which there is no open connection.");
        }
        return key;
    }

    /**
     * Begin closing this connection
     */
    private void close(SelectionKey key) {
        SocketChannel channel = channel(key);
        Transmissions trans = transmissions(key);
        if (trans != null) {

        }
        key.cancel();
        try {
            channel.socket().close();
            channel.close();
        } catch (IOException e) {
            log.error("Exception closing connection to node {}:", trans.id, e);
        }
    }

    /**
     * The id and in-progress send and receive associated with a connection
     */
    private static class Transmissions {
        public int id;
        public NetworkSend send;
        public NetworkReceive receive;

        public Transmissions(int id) {
            this.id = id;
        }

        public boolean hasSend() {
            return this.send != null;
        }

        public boolean hasReceive() {
            return this.receive != null;
        }

        public void clearSend() {
            this.send = null;
        }

        public void clearReceive() {
            this.receive = null;
        }
    }
}
