package de.kurzware;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.printf("Hello and welcome!");

        String jsonFilePath = "src/main/resources/aggregation.json";
        List<BsonDocument> pipeline = Collections.emptyList();
        try (FileReader fileReader = new FileReader(jsonFilePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            System.out.println("Datei erfolgreich gelesen.");
            String collect = bufferedReader.lines().collect(Collectors.joining());
            collect = collect.replaceAll("/[*].*?[*]/", "");
            System.out.println("collect = " + collect);
            BsonArray bsonArray = BsonArray.parse(collect);
            System.out.println("bsonArray = " + bsonArray);
            pipeline = bsonArray.stream()
                    .map(BsonValue::asDocument)
                    .toList();
            System.out.println("pipeline = " + pipeline);
        } catch (Exception e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
            return;
        }

        String connectionString = "mongodb://localhost:27017";
        String databaseName = "local";
        String collectionName = "startup_log";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            // Aggregation ausführen
            collection.aggregate(pipeline).toCollection();
            //.forEach(doc -> System.out.println(doc.toJson()));
        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }
}