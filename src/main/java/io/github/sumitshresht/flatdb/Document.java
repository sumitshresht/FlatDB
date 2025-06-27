package io.github.sumitshresht.flatdb;

import java.util.HashMap;

public class Document extends HashMap<String, Object> {
    public Document append(String key, Object value) {
        this.put(key, value);
        return this;
    }
    public int getInt(String key) {
        Object value = this.get(key);
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    public double getDouble(String key) {
        Object value = this.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

}
