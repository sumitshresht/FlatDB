---

# 🚀 FlatDB

[![Maven Central](https://img.shields.io/maven-central/v/io.github.sumitshresht/SecureZip.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.sumitshresht/SecureZip)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![Java](https://img.shields.io/badge/Java-8+-blue.svg)
![JUnit](https://img.shields.io/badge/Tested-JUnit5-brightgreen)


A lightweight, fast, and simple **Flat-File NoSQL Database** for Java applications. Designed for simplicity and flexibility, FlatDB stores data in plain JSON files while providing a powerful in-memory querying engine — perfect for small projects, prototyping, embedded apps, and developer tools.

---

## ✨ Features

* 🔥 **NoSQL Document Store** — Store data in flexible JSON-like documents.
* ⚡ **In-Memory Fast Queries** — Query data using powerful conditions.
* 📂 **Flat-File Storage** — Persists data as JSON files, no separate database server needed.
* 🔍 **Rich Query Language** — Supports `equal`, `notEqual`, `greaterThan`, `lessThan`, `contains`, `and`, `or`, `not`.
* 📑 **Nested Field Querying** — Query deeply nested fields easily (`"address.city" == "Delhi"`).
* 🏎️ **Indexing Support** — Create indexes on fields for faster lookup.
* ♻️ **CRUD Operations** — Insert, Update, Delete, Find.
* 💾 **Save and Load Database** — Persistent storage to disk.
* 🔗 **Minimal Dependencies** — Only uses `Gson` for JSON serialization.

---

## 📦 Installation

### Add via Maven:

```xml
<dependency>
  <groupId>io.github.sumitshresht</groupId>
  <artifactId>FlatDB</artifactId>
  <version>1.0.0</version>
</dependency>
```

Or download the release jar from [Releases](https://github.com/sumitshresht/FlatDB/releases).

---

## 🚀 Quick Start Example

```java
import io.github.sumitshresht.flatdb.*;

public class Main {
    public static void main(String[] args) {
        // Create Database
        Database db = new Database("TestDB");

        // Get Collection
        Collection users = db.getCollection("users");

        // Insert Documents
        users.insert(new Document()
                .append("name", "Alice")
                .append("age", 30)
                .append("address", Map.of("city", "Delhi", "zip", 110001)));

        users.insert(new Document()
                .append("name", "Bob")
                .append("age", 25)
                .append("address", Map.of("city", "Mumbai")));

        // Query
        var results = users.find(Query.where("address.city").equal("Delhi"));
        System.out.println("Users in Delhi: " + results);

        // Update
        users.update(Query.where("name").equal("Bob"), new Document().append("age", 26));

        // Delete
        users.delete(Query.where("age").greaterThan(29));

        // Save to File
        Storage.save(db, "db.json");

        // Load from File
        Database db2 = new Database("TestDB");
        Storage.load(db2, "db.json");
    }
}
```

---

## 🔎 Query Examples

| Operation         | Example                                                                    |
| ----------------- | -------------------------------------------------------------------------- |
| Equal             | `Query.where("name").equal("Alice")`                                       |
| Not Equal         | `Query.where("name").notEqual("Alice")`                                    |
| Greater Than      | `Query.where("age").greaterThan(25)`                                       |
| Less Than         | `Query.where("age").lessThan(50)`                                          |
| Contains (String) | `Query.where("name").contains("li")`                                       |
| AND               | `Query.where("age").greaterThan(20).and(Query.where("name").equal("Bob"))` |
| OR                | `Query.where("name").equal("Alice").or(Query.where("name").equal("Bob"))`  |
| Nested Field      | `Query.where("address.city").equal("Delhi")`                               |

---

## 🚀 Indexing Example

```java
users.createIndex("name");
var result = users.find(Query.where("name").equal("Alice"));
```

Indexing drastically improves lookup time for large collections on indexed fields.

---

## 💾 File Storage Example

```java
// Save
Storage.save(db, "mydb.json");

// Load
Storage.load(db, "mydb.json");
```

---

## 🛠 Project Structure

```
FlatDB/
 └── src/
     └── main/
         └── java/
             └── io.github.sumitshresht.flatdb
                 ├── Database.java
                 ├── Collection.java
                 ├── Document.java
                 ├── Query.java
                 └── Storage.java
```

---

## 📜 License

This project is licensed under the **MIT License** — free for personal and commercial use.

---

## 🤝 Contributing

Contributions are welcome! Feel free to fork the repo, open issues, or submit pull requests.

---

## ⭐ Show Your Support

If you like this project, give it a ⭐ on GitHub!

---

## 🔗 Connect

* GitHub: [@sumitshresht](https://github.com/sumitshresht)

---

## 🏆 Why Use FlatDB?

* Perfect for microservices, tools, small apps, and testing scenarios.
* No need for installing databases like MongoDB or SQL.
* A fast, lightweight, dependency-free alternative for local or embedded data storage.

---
