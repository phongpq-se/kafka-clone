package kafka.common.record;

import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * @author phongpq
 */

@Getter
public class MemoryRecords implements Records {
    private ByteBuffer buffer;

    public MemoryRecords(ByteBuffer buffer) {
        this.buffer = buffer;
    }
}
