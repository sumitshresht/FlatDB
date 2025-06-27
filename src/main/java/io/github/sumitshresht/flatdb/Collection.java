package io.github.sumitshresht.flatdb;

import java.util.*;
import java.util.stream.Collectors;

public class Collection {

    private final String name;
    private final List<Document> documents;
    private final Map<String, Map<Object, List<Document>>> indexes;

    public Collection(String name) {
        this.name = name;
        this.documents = new ArrayList<>();
        this.indexes = new HashMap<>();
    }

    // ➕ Insert
    public void insert(Document doc) {
        documents.add(doc);
        updateIndexesOnInsert(doc);
    }

    // 🔍 Find (uses index if available)
    public List<Document> find(Query query) {
        if (indexes.containsKey(query.getKey()) && query.isSimpleEquality()) {
            Object value = query.getExpectedValue();
            if (value != null) {
                Map<Object, List<Document>> index = indexes.get(query.getKey());
                List<Document> indexedDocs = index.getOrDefault(value, Collections.emptyList());
                return indexedDocs.stream().filter(query::matches).collect(Collectors.toList());
            }
        }
        return documents.stream().filter(query::matches).collect(Collectors.toList());
    }

    // 🔍 Find All
    public List<Document> findAll() {
        return new ArrayList<>(documents);
    }

    // ❌ Delete
    public void delete(Query query) {
        List<Document> toRemove = find(query);
        documents.removeAll(toRemove);
        rebuildIndexes();
    }

    // ✍️ Update
    public void update(Query query, Document newData) {
        for (Document doc : find(query)) {
            doc.putAll(newData);
        }
        rebuildIndexes();
    }

    // 🔢 Count
    public long count() {
        return documents.size();
    }

    // 🔥 Create Index
    public void createIndex(String field) {
        Map<Object, List<Document>> index = new HashMap<>();
        for (Document doc : documents) {
            Object value = resolveValue(doc, field);
            index.computeIfAbsent(value, k -> new ArrayList<>()).add(doc);
        }
        indexes.put(field, index);
    }

    // ♻️ Update Index on Insert
    private void updateIndexesOnInsert(Document doc) {
        for (String field : indexes.keySet()) {
            Object value = resolveValue(doc, field);
            indexes.get(field).computeIfAbsent(value, k -> new ArrayList<>()).add(doc);
        }
    }

    // 🔄 Rebuild Index (after delete or update)
    private void rebuildIndexes() {
        Map<String, Map<Object, List<Document>>> newIndexes = new HashMap<>();
        for (String field : indexes.keySet()) {
            Map<Object, List<Document>> index = new HashMap<>();
            for (Document doc : documents) {
                Object value = resolveValue(doc, field);
                index.computeIfAbsent(value, k -> new ArrayList<>()).add(doc);
            }
            newIndexes.put(field, index);
        }
        indexes.clear();
        indexes.putAll(newIndexes);
    }

    // 🔍 Nested key resolver (like "address.city")
    private Object resolveValue(Document doc, String key) {
        String[] parts = key.split("\\.");
        Object current = doc;
        for (String part : parts) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = ((Map<?, ?>) current).get(part);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    public String getName() {
        return name;
    }
}
