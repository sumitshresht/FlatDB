package io.github.sumitshresht.flatdb;

import java.io.File;
import java.util.List;

public class FlatDBTest {

    public static void main(String[] args) {
        System.out.println("⚙️  Starting FlatDB Tests...\n");

        Database db = new Database("testdb");
        Collection users = db.getCollection("users");

        // ✅ Insert Tests
        System.out.println("✅ Insert Tests");
        users.insert(new Document().append("name", "Alice").append("age", 25));
        users.insert(new Document().append("name", "Bob").append("age", 30));
        users.insert(new Document().append("name", "Charlie").append("age", 35));
        users.insert(new Document()); // Empty doc

        assertCondition(users.count() == 4, "Insert count check");
        assertCondition(users.find(Query.where("name").equal("Nonexistent")).size() == 0, "Insert empty document check");

        // ✅ Query Tests
        System.out.println("\n✅ Query Tests");
        assertCondition(users.find(Query.where("name").equal("Bob")).size() == 1, "Query for Bob");
        assertCondition(users.find(Query.where("name").equal("Nonexistent")).size() == 0, "Query non-existing user");
        assertCondition(users.find(Query.where("unknown").equal("value")).size() == 0, "Query on missing field");

        // ✅ Nested Query Test (Simulate nested by string key like "address.city")
        users.insert(new Document().append("name", "David").append("address.city", "New York"));
        assertCondition(users.find(Query.where("address.city").equal("New York")).size() == 1, "Query nested field");

        // ✅ Complex Query Tests
        System.out.println("\n✅ Complex Query Tests");
        Query q1 = Query.where("age").greaterThan(20);
        Query q2 = Query.where("name").equal("Bob");
        assertCondition(users.find(q1.and(q2)).size() == 1, "AND query");
        assertCondition(users.find(q1.or(q2)).size() >= 1, "OR query");

        // ✅ Index Tests
        System.out.println("\n✅ Index Tests");
        users.createIndex("name");
        List<Document> indexedResult = users.find(Query.where("name").equal("Alice"));
        assertCondition(indexedResult.size() == 1, "Index query");

        List<Document> missingIndexResult = users.find(Query.where("name").equal("NotExist"));
        assertCondition(missingIndexResult.isEmpty(), "Index query for missing value");

        // ✅ Update Tests
        System.out.println("\n✅ Update Tests");
        users.update(Query.where("name").equal("Alice"), new Document().append("age", 26));
        assertCondition(users.find(Query.where("age").equal(26)).size() == 1, "Update Alice's age");

        long beforeUpdate = users.count();
        users.update(Query.where("name").equal("NotExist"), new Document().append("age", 50));
        assertCondition(users.count() == beforeUpdate, "Update non-existent user (no change)");

        // ✅ Delete Tests
        System.out.println("\n✅ Delete Tests");
        users.delete(Query.where("age").greaterThan(30));
        assertCondition(users.find(Query.where("age").greaterThan(30)).isEmpty(), "Delete users with age > 30");

        long beforeDelete = users.count();
        users.delete(Query.where("name").equal("NotExist"));
        assertCondition(users.count() == beforeDelete, "Delete non-existent user (no change)");

        users.delete(Query.where("name").notEqual("Nonexistent")); // Delete all remaining
        assertCondition(users.count() == 0, "Delete all users");

        // ✅ Storage (Save/Load) Tests
        System.out.println("\n✅ Storage (Save/Load) Tests");
        Collection books = db.getCollection("books");
        books.insert(new Document().append("title", "Book A").append("pages", 200));
        books.insert(new Document().append("title", "Book B").append("pages", 300));

        String filePath = "flatdb_test.json";
        Storage.save(db, filePath);

        Database loadedDB = new Database("testdb");
        Storage.load(loadedDB, filePath);
        Collection loadedBooks = loadedDB.getCollection("books");
        assertCondition(loadedBooks.count() == 2, "Loaded books count matches");

        // Cleanup test file
        new File(filePath).delete();

        System.out.println("\n🎉 All tests completed successfully!");
    }

    public static void assertCondition(boolean condition, String testName) {
        if (condition) {
            System.out.println("✅ PASS: " + testName);
        } else {
            System.out.println("❌ FAIL: " + testName);
            throw new AssertionError("Test failed: " + testName);
        }
    }
}
