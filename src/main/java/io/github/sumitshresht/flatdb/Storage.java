package io.github.sumitshresht.flatdb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Storage {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void save(Database db, String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            Map<String, Object> data = new HashMap<>();
            for (Map.Entry<String, Collection> entry : db.getCollections().entrySet()) {
                data.put(entry.getKey(), entry.getValue().findAll());
            }
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save database: " + e.getMessage(), e);
        }
    }

    public static void load(Database db, String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, java.util.List<Document>>>() {}.getType();
            Map<String, java.util.List<Document>> data = gson.fromJson(reader, type);

            if (data != null) {
                for (Map.Entry<String, java.util.List<Document>> entry : data.entrySet()) {
                    Collection collection = db.getCollection(entry.getKey());
                    for (Document doc : entry.getValue()) {
                        collection.insert(doc);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No previous database found, starting fresh.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database: " + e.getMessage(), e);
        }
    }
}
