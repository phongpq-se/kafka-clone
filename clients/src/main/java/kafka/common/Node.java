package kafka.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author phongpq
 */

@AllArgsConstructor
@Getter
public class Node {
    private final int id;
    private final String host;
    private final int port;
}
