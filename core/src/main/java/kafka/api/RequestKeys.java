package kafka.api;

/**
 * @author phongpq
 */

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RequestKeys {

    public static final short ProduceKey = 0;
    public static final short FetchKey = 1;
    public static final short OffsetsKey = 2;
    public static final short MetadataKey = 3;
    public static final short LeaderAndIsrKey = 4;
    public static final short StopReplicaKey = 5;
    public static final short UpdateMetadataKey = 6;
    public static final short ControlledShutdownKey = 7;
    public static final short OffsetCommitKey = 8;
    public static final short OffsetFetchKey = 9;
    public static final short ConsumerMetadataKey = 10;
    public static final short JoinGroupKey = 11;
    public static final short HeartbeatKey = 12;

    public static final Map<Short, Function<ByteBuffer, RequestOrResponse>> keyToDeserializerMap = new HashMap<>();

    static {
        //keyToDeserializerMap.put(ProduceKey, ProducerRequest.readFrom());
//        keyToDeserializerMap.put(FetchKey, FetchRequest::readFrom);
//        keyToDeserializerMap.put(OffsetsKey, OffsetRequest::readFrom);
//        keyToDeserializerMap.put(MetadataKey, TopicMetadataRequest::readFrom);
//        keyToDeserializerMap.put(LeaderAndIsrKey, LeaderAndIsrRequest::readFrom);
//        keyToDeserializerMap.put(StopReplicaKey, StopReplicaRequest::readFrom);
//        keyToDeserializerMap.put(UpdateMetadataKey, UpdateMetadataRequest::readFrom);
//        keyToDeserializerMap.put(ControlledShutdownKey, ControlledShutdownRequest::readFrom);
//        keyToDeserializerMap.put(OffsetCommitKey, OffsetCommitRequest::readFrom);
//        keyToDeserializerMap.put(OffsetFetchKey, OffsetFetchRequest::readFrom);
//        keyToDeserializerMap.put(ConsumerMetadataKey, ConsumerMetadataRequest::readFrom);
//        keyToDeserializerMap.put(JoinGroupKey, JoinGroupRequestAndHeader::readFrom);
//        keyToDeserializerMap.put(HeartbeatKey, HeartbeatRequestAndHeader::readFrom);
    }

    public static String nameForKey(short key) {
        Function<ByteBuffer, RequestOrResponse> deserializer = deserializerForKey(key);
        return deserializer.getClass().getSimpleName();
    }

    public static Function<ByteBuffer, RequestOrResponse> deserializerForKey(short key) {
        Function<ByteBuffer, RequestOrResponse> deserializer = keyToDeserializerMap.get(key);
        if (deserializer != null) {
            return deserializer;
        } else {
            // throw new KafkaException(String.format("Wrong request type %d", key));
            throw new RuntimeException(String.format("Wrong request type %d", key));
        }
    }
}

