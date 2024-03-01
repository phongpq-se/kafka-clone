package kafka.server;

import lombok.AllArgsConstructor;

/**
 * @author phongpq
 */
public class KafkaServerStartable {
    private final KafkaConfig kafkaConfig;
    private final KafkaServer kafkaServer;

    public KafkaServerStartable(KafkaConfig kafkaConfig, KafkaServer kafkaServer) {
        this.kafkaConfig = kafkaConfig;
        this.kafkaServer = new KafkaServer();
    }

    public void startup() {
        try {
            kafkaServer.startUp();
        } catch (Throwable e) {
            System.exit(1);
        }
    }
}
