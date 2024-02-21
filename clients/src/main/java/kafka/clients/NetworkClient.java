package kafka.clients;


import kafka.common.Node;
import kafka.common.network.Selectable;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author phongpq
 */

@AllArgsConstructor
public class NetworkClient implements KafkaClient {

    /* the selector used to perform network i/o */
    private final Selectable selector;

    private static final Logger logger = LoggerFactory.getLogger(NetworkClient.class);

    @Override
    public List<ClientResponse> poll(List<ClientRequest> requests, long timeout, long now) {
        return null;
    }

    /**
     * Initiate a connection to the given node
     */
    private void initiateConnect(Node node, long now) {
        try {
            logger.debug("Initiating connection to node {} at {}:{}.", node.getId(), node.getHost(), node.getPort());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
