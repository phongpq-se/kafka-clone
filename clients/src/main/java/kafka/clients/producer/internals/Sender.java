package kafka.clients.producer.internals;

import kafka.clients.ClientRequest;
import kafka.clients.KafkaClient;
import kafka.common.TopicPartition;
import kafka.common.requests.ProduceRequest;
import kafka.common.requests.RequestSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author phongpq
 */

public class Sender implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Sender.class);


    /* the clock instance used for getting the time */
    //private final Time time;

    private final KafkaClient client;
    private final RecordAccumulator recordAccumulator;
    private volatile boolean running;

    public Sender(KafkaClient kafkaClient, RecordAccumulator recordAccumulator) {
        this.client = kafkaClient;
        this.recordAccumulator = recordAccumulator;
    }

    /**
     * The main run loop for the sender thread
     */
    @Override
    public void run() {
        while (running) {
            try {
                run(System.currentTimeMillis());
            } catch (Exception e) {
                log.error("Uncaught error in kafka producer I/O thread: ", e);
            }
        }
    }

    /**
     * Run a single iteration of sending
     *
     * @param now The current POSIX time in milliseconds
     */
    public void run(long now) {
        Map<Integer, List<RecordBatch>> batches = recordAccumulator.drain();
        List<ClientRequest> requests = createProduceRequest(batches, now);


    }

    /**
     * Transfer the record batches into a list of produce requests on a per-node basis
     */
    private List<ClientRequest> createProduceRequest(Map<Integer, List<RecordBatch>> collated, long now) {
        return collated.entrySet().stream()
                .map(entry -> producerRequest(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Create a produce request from the given record batches
     */
    private ClientRequest producerRequest(int destination, List<RecordBatch> batches) {
        Map<TopicPartition, ByteBuffer> produceRecordsByPartition = new HashMap<TopicPartition, ByteBuffer>(batches.size());
        Map<TopicPartition, RecordBatch> recordsByPartition = new HashMap<TopicPartition, RecordBatch>(batches.size());
        for (RecordBatch batch : batches) {
            TopicPartition tp = batch.getTopicPartition();
            ByteBuffer recordsBuffer = batch.getRecords().getBuffer();
            recordsBuffer.flip();
            produceRecordsByPartition.put(tp, recordsBuffer);
        }
        ProduceRequest produceRequest = new ProduceRequest(produceRecordsByPartition);
//        RequestSend send = new RequestSend(destination, );
//        return new ClientRequest();
        return null;
    }
}
