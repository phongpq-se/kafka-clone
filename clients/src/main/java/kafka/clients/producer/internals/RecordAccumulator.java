package kafka.clients.producer.internals;

import kafka.common.TopicPartition;
import kafka.common.utils.CopyOnWriteMap;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author phongpq
 */
public class RecordAccumulator {
    private final ConcurrentMap<TopicPartition, Deque<RecordBatch>> batches;

    public RecordAccumulator() {
        this.batches = new CopyOnWriteMap<>();
    }

    public Map<Integer, List<RecordBatch>> drain() {

        return null;
    }

    /**
     * Add a record to the accumulator, return the append result
     * <p>
     * The append result will contain the future metadata, and flag for whether the appended batch is full or a new batch is created
     * <p>
     *
     * @param tp          The topic/partition to which this record is being sent
     * @param key         The key for the record
     * @param value       The value for the record
     * @param compression The compression codec for the record
     * @param callback    The user-supplied callback to execute when the request is complete
     */
    public RecordAppendResult append(byte[] key, byte[] value) {
        return null;
    }

    public final static class RecordAppendResult {

    }
}
