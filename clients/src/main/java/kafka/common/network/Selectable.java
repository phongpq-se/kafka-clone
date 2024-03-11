package kafka.common.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author phongpq
 */
public interface Selectable {
    /**
     * Begin establishing a socket connection to the given address identified by the given address
     *
     * @param id                The id for this connection
     * @param address           The address to connect to
     * @param sendBufferSize    The send buffer for the socket
     * @param receiveBufferSize The receive buffer for the socket
     * @throws IOException If we cannot begin connecting
     */
    void connect(int id, InetSocketAddress address, int sendBufferSize, int receiveBufferSize) throws IOException;

    /**
     * Initiate any sends provided, and make progress on any other I/O operations in-flight (connections,
     * disconnections, existing sends, and receives)
     *
     * @param timeout The amount of time to block if there is nothing to do
     * @param sends   The new sends to initiate
     * @throws IOException
     */
    void poll(long timeout, List<NetworkSend> sends) throws IOException;

    /**
     * The list of sends that completed on the last {@link #poll(long, List) poll()} call.
     */
    List<NetworkSend> completedSends();

    /**
     * The list of receives that completed on the last {@link #poll(long, List) poll()} call.
     */
    List<NetworkReceive> completedReceives();
}
