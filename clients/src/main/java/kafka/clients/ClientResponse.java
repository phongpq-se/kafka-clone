package kafka.clients;

import kafka.common.protocol.types.Struct;

/**
 * @author phongpq
 */
public class ClientResponse {
    private final long received;
    private final boolean disconnected;
    private final ClientRequest request;
    private final Struct responseBody;

    /**
     * @param request The original request
     * @param received The unix timestamp when this response was received
     * @param disconnected Whether the client disconnected before fully reading a response
     * @param responseBody The response contents (or null) if we disconnected or no response was expected
     */
    public ClientResponse(ClientRequest request, long received, boolean disconnected, Struct responseBody) {
        super();
        this.received = received;
        this.disconnected = disconnected;
        this.request = request;
        this.responseBody = responseBody;
    }
}
