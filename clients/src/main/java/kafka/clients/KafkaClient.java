package kafka.clients;

import java.util.List;

/**
 * @author phongpq
 */
public interface KafkaClient {
    /**
     * Initiate the sending of the given requests and return any completed responses. Requests can only be sent on ready
     * connections.
     * @param requests The requests to send
     * @param timeout The maximum amount of time to wait for responses in ms
     * @param now The current time in ms
     * @throws IllegalStateException If a request is sent to an unready node
     */
    List<ClientResponse> poll(List<ClientRequest> requests, long timeout, long now);
}
