package kafka.clients.producer;

import kafka.clients.NetworkClient;
import kafka.common.network.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * @author phongpq
 */
public class KafkaProducer<K, V> implements Producer<K, V> {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);


    public KafkaProducer() {
       // Selector selector = new Selector();
       // NetworkClient client = new NetworkClient(selector);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> record) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
