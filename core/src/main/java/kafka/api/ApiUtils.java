package kafka.api;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author phongpq
 */
public class ApiUtils {
    private static final String ProtocolEncoding = "UTF-8";

    /**
     * Read size prefixed string where the size is stored as a 2 byte short.
     *
     * @param buffer The buffer to read from
     */
    public static String readShortString(ByteBuffer buffer) {
        int size = buffer.getShort();
        if (size < 0) {
            return null;
        }

        byte[] bytes = new byte[size];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
