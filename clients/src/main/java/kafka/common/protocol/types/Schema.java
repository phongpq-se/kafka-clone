package kafka.common.protocol.types;

/**
 * @author phongpq
 */

import java.util.Map;

/**
 * The schema for a compound record definition
 */
public class Schema extends Type {
    private final Field[] fields;
    private final Map<String, Field> fieldsByName;

    public Schema(Field[] fields, Map<String, Field> fieldsByName) {
        this.fields = fields;
        this.fieldsByName = fieldsByName;
    }
}
