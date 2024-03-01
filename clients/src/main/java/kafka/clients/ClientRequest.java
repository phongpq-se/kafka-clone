package kafka.clients;

import jdk.jfr.Experimental;
import kafka.common.requests.RequestSend;
import lombok.AllArgsConstructor;

/**
 * @author phongpq
 */

@AllArgsConstructor
public final class ClientRequest {
    private final long createdMs;
    private final boolean expectResponse;
    private final RequestSend request;
    private final Object attachment;
}
