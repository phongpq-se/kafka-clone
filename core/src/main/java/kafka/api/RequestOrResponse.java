package kafka.api;

/**
 * @author phongpq
 */

import java.nio.ByteBuffer;
import java.util.Optional;

import kafka.network.RequestChannel;

public abstract class RequestOrResponse {


    public int sizeInBytes() {
        // Implement the size calculation logic
        return 0;
    }

    public void writeTo(ByteBuffer buffer) {
        // Implement the write logic to ByteBuffer
    }

    public void handleError(Throwable e, RequestChannel requestChannel, RequestChannel.Request request) {
        // Implement the error handling logic
    }

    /* The purpose of this API is to return a string description of the Request mainly for the purpose of request logging.
     * This API has no meaning for a Response object.
     * @param details If this is false, omit the parts of the request description that are proportional to the number of
     *                topics or partitions. This is mainly to control the amount of request logging. */
    public String describe(boolean details) {
        // Implement the description logic
        return "";
    }
}