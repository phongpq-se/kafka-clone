package kafka.common.protocol.types;

/**
 * @author phongpq
 */
public class Field {
    private static final Object NO_DEFAULT = new Object();

    private int index;
    public final String name;
    private final Type type;
    private final Object defaultValue;
    private final String doc;
    final Schema schema;

    public Field(String name, Type type, Object defaultValue, String doc, Schema schema) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.doc = doc;
        this.schema = schema;
    }
}
