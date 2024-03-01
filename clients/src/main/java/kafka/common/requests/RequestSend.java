package kafka.common.requests;

import kafka.common.network.NetworkSend;
import kafka.common.protocol.types.Struct;

import java.nio.ByteBuffer;

/**
 * @author phongpq
 */
public class RequestSend extends NetworkSend {
    private final RequestHeader header;
    private final Struct body;

    public RequestSend(int destination, RequestHeader header, Struct body) {
        super(destination, null);
        this.header = header;
        this.body = body;
    }

    private static ByteBuffer serialize(RequestHeader header, Struct body) {
//        ByteBuffer buffer = ByteBuffer.allocate(header.)
        return null;
    }
}
