package kafka.clients;

import jdk.jfr.Experimental;
import lombok.AllArgsConstructor;

/**
 * @author phongpq
 */

@AllArgsConstructor
public final class ClientRequest {
    private final long createdMs;
    private final boolean expectResponse;

}
