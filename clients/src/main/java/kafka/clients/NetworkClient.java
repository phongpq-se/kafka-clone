package kafka.clients;


import kafka.common.Node;
import kafka.common.network.NetworkSend;
import kafka.common.network.Selectable;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author phongpq
 */

public class NetworkClient implements KafkaClient {

    /* the selector used to perform network i/o */
    private final Selectable selector;

    public NetworkClient(Selectable selector) {
        this.selector = selector;
    }

    private static final Logger log = LoggerFactory.getLogger(NetworkClient.class);

    @Override
    public List<ClientResponse> poll(List<ClientRequest> requests, long timeout, long now) {
        List<NetworkSend> sends = new ArrayList<>();

        for (ClientRequest request : requests) {
            int nodeId = request.getRequest().destination();
            sends.add(request.getRequest());
        }

        // do the I/O
        try {
            this.selector.poll(timeout, sends);
        } catch (IOException e) {
            log.error("Unexpected error during I/O in producer network thread", e);
        }

        List<ClientResponse> responses = new ArrayList<ClientResponse>();
//        this.selector.completedSends().forEach(send -> {
//            responses.add(new ClientResponse())
//        });
        return responses;
    }

    /**
     * Initiate a connection to the given node
     */
    private void initiateConnect(Node node, long now) {
        try {
            log.debug("Initiating connection to node {} at {}:{}.", node.getId(), node.getHost(), node.getPort());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
