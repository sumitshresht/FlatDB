package io.github.sumitshresht.flatdb;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class Query {

    final String key;
    final Predicate<Object> condition;
    private Object expectedValue;
    private boolean simpleEquality = false;

    private Query(String key, Predicate<Object> condition) {
        this.key = key;
        this.condition = condition;
    }

    public static Query where(String key) {
        return new Query(key, obj -> true);
    }

    public Query equal(Object value) {
        this.expectedValue = value;
        this.simpleEquality = true;
        return new Query(key, obj -> Objects.equals(value, obj)).withExpected(value);
    }

    public Query notEqual(Object value) {
        return new Query(key, obj -> !Objects.equals(value, obj));
    }

    public Query greaterThan(Number value) {
        return new Query(key, obj -> (obj instanceof Number) &&
                ((Number) obj).doubleValue() > value.doubleValue());
    }

    public Query lessThan(Number value) {
        return new Query(key, obj -> (obj instanceof Number) &&
                ((Number) obj).doubleValue() < value.doubleValue());
    }

    public Query contains(String value) {
        return new Query(key, obj -> (obj instanceof String) &&
                ((String) obj).contains(value));
    }

    public Query and(Query other) {
        return new CombinedQuery(this, other, true);
    }

    public Query or(Query other) {
        return new CombinedQuery(this, other, false);
    }

    public Query not() {
        return new Query(key, obj -> !condition.test(obj));
    }

    public boolean matches(Document doc) {
        Object val = resolveValue(doc, key);
        return condition.test(val);
    }

    // ✅ Nested key resolver (supports key like "address.city")
    private Object resolveValue(Map<?, ?> doc, String key) {
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

    // ✔️ Index optimization support
    private Query withExpected(Object value) {
        this.expectedValue = value;
        this.simpleEquality = true;
        return this;
    }

    public String getKey() {
        return key;
    }

    public Object getExpectedValue() {
        return expectedValue;
    }

    public boolean isSimpleEquality() {
        return simpleEquality;
    }

    // 🔗 Combined query inner class
    private static class CombinedQuery extends Query {
        private final Query q1;
        private final Query q2;
        private final boolean isAnd;

        public CombinedQuery(Query q1, Query q2, boolean isAnd) {
            super(null, null);
            this.q1 = q1;
            this.q2 = q2;
            this.isAnd = isAnd;
        }

        @Override
        public boolean matches(Document doc) {
            return isAnd ? (q1.matches(doc) && q2.matches(doc)) :
                    (q1.matches(doc) || q2.matches(doc));
        }
    }
}
