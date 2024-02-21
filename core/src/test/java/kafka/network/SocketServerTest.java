package kafka.network;

import kafka.api.ProducerRequest;
import kafka.producer.SyncProducerConfig;
import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Array;
import java.util.ArrayList;

/**
 * @author phongpq
 */
public class SocketServerTest {

    SocketServer socketServer = new SocketServer(1,
            null, 9092,
            1,
            10,
            300000, 300000);

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
    public void simpleRequest() throws InterruptedException {
        Socket socket = connect(socketServer);
        System.out.println("Connected to " + socketServer.getPort());

        var correlationId = -1;
        var clientId = SyncProducerConfig.DefaultClientId;
        var ackTimeoutMs = SyncProducerConfig.DefaultAckTimeoutMs;
        var ack = SyncProducerConfig.DefaultRequiredAcks;

        ProducerRequest producerRequest = new ProducerRequest((short) 0, correlationId, clientId, ack, ackTimeoutMs);
        var byteBuffer = ByteBuffer.allocate(producerRequest.sizeInBytes());
        byteBuffer.rewind();
        var serializedBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(serializedBytes);

        sendRequest(socket, (short) 0, serializedBytes);
        processRequest(socketServer.getRequestChannel());
        receiveResponse(socket);

        Thread.sleep(1000000);
    }

    private Socket connect(SocketServer socketServer) {
        try {
            return new Socket("localhost", socketServer.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* A simple request handler that just echos back the response */
    public static void processRequest(RequestChannel channel) {
        RequestChannel.Request request = channel.receiveRequest();
        ByteBuffer byteBuffer = ByteBuffer.allocate(request.getRequestObj().sizeInBytes());
        request.getRequestObj().writeTo(byteBuffer);
        byteBuffer.rewind();
//        BoundedByteBufferSend send = new BoundedByteBufferSend(byteBuffer);
//        channel.sendResponse(new RequestChannel.Response(request.getProcessor(), request, send));
    }

    private void sendRequest(Socket socket, Short id, byte[] request) {
        try {
            var outgoing = new DataOutputStream(socket.getOutputStream());
            outgoing.writeInt(request.length + 2);
            outgoing.writeShort(id);
            outgoing.write(request);
            outgoing.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static byte[] receiveResponse(Socket socket) {
        try {
            DataInputStream incoming = new DataInputStream(socket.getInputStream());
            int len = incoming.readInt();
            byte[] response = new byte[len];
            incoming.readFully(response);
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
