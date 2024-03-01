package kafka.common.protocol.types;

/**
 * @author phongpq
 */
public class Struct {
    private final Schema schema;
    private final Object[] values;

    public Struct(Schema schema, Object[] values) {
        this.schema = schema;
        this.values = values;
    }

}
