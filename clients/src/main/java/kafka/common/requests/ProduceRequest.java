package kafka.common.requests;

import kafka.common.TopicPartition;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author phongpq
 */
public class ProduceRequest {
    private final Map<TopicPartition, ByteBuffer> partitionRecords;

    public ProduceRequest(Map<TopicPartition, ByteBuffer> partitionRecords) {
        this.partitionRecords = partitionRecords;
    }
}
