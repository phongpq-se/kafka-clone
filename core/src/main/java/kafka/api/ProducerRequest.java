package kafka.api;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * @author phongpq
 */

@RequiredArgsConstructor
public class ProducerRequest extends RequestOrResponse {

    private final Short versionId;
    private final int correlationId;
    public final String clientId;
    public final Short requiredAcks;
    public final int ackTimeoutMs;

    public static ProducerRequest readFrom(ByteBuffer buffer) {
        var versionId = buffer.getShort();
        var correlationId = buffer.getInt();
        var clientId = ApiUtils.readShortString(buffer);
        var requiredAcks = buffer.getShort();
        var ackTimeoutMs = buffer.getInt();

        return new ProducerRequest(versionId, correlationId, clientId, requiredAcks, ackTimeoutMs);
    }

    public int sizeInBytes() {
        return
                2 + /* versionId */
                        4 + /* correlationId */
                        (2 + clientId.length()) + /* clientId */
                        4 + /* requiredAcks */
                        4; /* ackTimeoutMs */
    }
}
