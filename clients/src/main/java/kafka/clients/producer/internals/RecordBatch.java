package kafka.clients.producer.internals;

import kafka.common.TopicPartition;
import kafka.common.record.MemoryRecords;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author phongpq
 */
@Getter
public final class RecordBatch {
    private static final Logger log = LoggerFactory.getLogger(RecordBatch.class);

    private final MemoryRecords records;
    private final TopicPartition topicPartition;

    public RecordBatch(TopicPartition topicPartition, MemoryRecords records) {
        this.topicPartition = topicPartition;
        this.records = records;
    }
}
