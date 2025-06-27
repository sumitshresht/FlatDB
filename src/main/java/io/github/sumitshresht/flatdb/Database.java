package io.github.sumitshresht.flatdb;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private final String name;
    private final Map<String, Collection> collections;

    public Database(String name) {
        this.name = name;
        this.collections = new HashMap<>();
    }

    public Collection getCollection(String name) {
        return collections.computeIfAbsent(name, Collection::new);
    }

    public String getName() {
        return name;
    }

    public Map<String, Collection> getCollections() {
        return collections;
    }
}
