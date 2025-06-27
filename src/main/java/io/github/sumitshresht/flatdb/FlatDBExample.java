package io.github.sumitshresht.flatdb;

public class FlatDBExample {

    public static void main(String[] args) {
        // 🌟 Create a Database
        Database db = new Database("MyDB");

        // 🌟 Create/Get a Collection
        Collection users = db.getCollection("users");

        // 🌟 Insert Documents
        users.insert(new Document()
                .append("name", "Alice")
                .append("age", 30)
                .append("address", new Document().append("city", "Delhi").append("zip", "110001"))
        );

        users.insert(new Document()
                .append("name", "Bob")
                .append("age", 25)
                .append("address", new Document().append("city", "Mumbai").append("zip", "400001"))
        );

        users.insert(new Document()
                .append("name", "Charlie")
                .append("age", 35)
                .append("address", new Document().append("city", "Delhi").append("zip", "110002"))
        );

        // 🌟 Query Examples
        System.out.println("🔍 Find all users:");
        users.findAll().forEach(System.out::println);

        System.out.println("\n🔍 Find users from Delhi:");
        var delhiUsers = users.find(Query.where("address.city").equal("Delhi"));
        delhiUsers.forEach(System.out::println);

        System.out.println("\n🔍 Find users older than 30:");
        var olderUsers = users.find(Query.where("age").greaterThan(30));
        olderUsers.forEach(System.out::println);

        System.out.println("\n🔍 Find users whose name contains 'li':");
        var nameContains = users.find(Query.where("name").contains("li"));
        nameContains.forEach(System.out::println);

        // 🌟 Indexing Example (improves search speed for 'address.city')
        users.createIndex("address.city");
        System.out.println("\n🔍 Indexed Query (Delhi users):");
        var indexedDelhi = users.find(Query.where("address.city").equal("Delhi"));
        indexedDelhi.forEach(System.out::println);

        // 🌟 Update Example
        System.out.println("\n✍️ Updating Bob's age to 28:");
        users.update(Query.where("name").equal("Bob"), new Document().append("age", 28));
        users.findAll().forEach(System.out::println);

        // 🌟 Delete Example
        System.out.println("\n❌ Deleting users from Mumbai:");
        users.delete(Query.where("address.city").equal("Mumbai"));
        users.findAll().forEach(System.out::println);

        // 🌟 Save to file
        String filePath = "mydb.json";
        Storage.save(db, filePath);
        System.out.println("\n💾 Database saved to " + filePath);

        // 🌟 Load into a new database instance
        Database loadedDb = new Database("MyDB");
        Storage.load(loadedDb, filePath);
        System.out.println("\n📥 Loaded Database contents:");
        loadedDb.getCollection("users").findAll().forEach(System.out::println);
    }
}
