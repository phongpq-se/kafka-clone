package kafka.clients.producer;

import java.io.Closeable;
import java.util.concurrent.Future;

/**
 * @author phongpq
 */
public interface Producer<K, V> extends Closeable {
    /**
     * Send the given record asynchronously and return a future which will eventually contain the response information.
     *
     * @param record The record to send
     * @return A future which will eventually contain the response information
     */
    public Future<RecordMetadata> send(ProducerRecord<K,V> record);
}
