package kafka.network;

import kafka.api.RequestKeys;
import kafka.api.RequestOrResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author phongpq
 */

public class RequestChannel {
    private final int numProcessors;
    private final int queueSize;

    private ArrayBlockingQueue<Request> responseQueue;
    private ArrayBlockingQueue<Request> requestQueue;

    public RequestChannel(int numProcessors, int queueSize) {
        this.numProcessors = numProcessors;
        this.queueSize = queueSize;
        responseQueue = new ArrayBlockingQueue<Request>(numProcessors);
        requestQueue = new ArrayBlockingQueue<Request>(queueSize);
    }

    public void sendRequest(Request request) {
        try {
            requestQueue.put(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Request receiveRequest() {
        try {
            return requestQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    public static class Request {
        public final int processor;
        private final Object requestKey = null;
        private ByteBuffer buffer;

        public final RequestChannel.Request request;

        public RequestOrResponse requestObj = RequestKeys.deserializerForKey(buffer.getShort()).apply(buffer);

        public Request(int processor, RequestChannel.Request request) {
            this.processor = processor;
            this.request = request;
        }
    }

    public static class Response {

    }
}
