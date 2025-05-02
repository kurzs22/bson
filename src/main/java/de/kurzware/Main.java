package de.kurzware;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonArray;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        List<Document> pipeline = Collections.emptyList();
        try (FileReader fileReader = new FileReader("src/main/resources/aggregation.json");
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            // Hier können Sie den Inhalt der Datei lesen
            System.out.println("Datei erfolgreich gelesen.");
            String collect = bufferedReader.lines().collect(Collectors.joining());
            collect = collect.replaceAll("/[*].*?[*]/", "");
            System.out.println("collect = " + collect);
            BsonArray bsonArray = BsonArray.parse(collect);
            System.out.println("bsonArray = " + bsonArray);
            List<Document> stages = bsonArray.stream()
                    .map(doc -> Document.parse(doc.toString()))
                    .toList();
            System.out.println("pipeline = " + stages);
        } catch (Exception e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
            return;
        }

        String connectionString = "mongodb://localhost:27017";
        String databaseName = "local";
        String collectionName = "startup_log";
        String jsonFilePath = "src/main/resources/aggregation.json";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            // Aggregation ausführen
            collection.aggregate(pipeline).forEach(doc -> System.out.println(doc.toJson()));
        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }
}