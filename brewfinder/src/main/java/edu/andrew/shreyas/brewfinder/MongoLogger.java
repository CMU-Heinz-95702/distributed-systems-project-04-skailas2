package edu.andrew.shreyas.brewfinder;

import com.mongodb.client.*;
import org.bson.Document;

import java.util.Date;

public class MongoLogger {
    private static final String USER = "skailas2";
    private static final String PASSWORD = "x3D4TWzU0oEQtMJG";
    private static final String DATABASE = "brewfinderlogs";
    private static final String COLLECTION = "requests";

    private static final String CONNECTION_STRING =
            "mongodb://skailas2:x3D4TWzU0oEQtMJG@" +
            "ac-wcrrvnc-shard-00-00.21wph6h.mongodb.net:27017," +
            "ac-wcrrvnc-shard-00-01.21wph6h.mongodb.net:27017," +
            "ac-wcrrvnc-shard-00-02.21wph6h.mongodb.net:27017/" +
            "brewfinderlogs?replicaSet=atlas-sjveh9-shard-0&ssl=true&authSource=admin";

    public static void logRequest(String city, String userAgent, String apiUrl, int breweryCount) {
        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase db = mongoClient.getDatabase(DATABASE);
            MongoCollection<Document> logs = db.getCollection(COLLECTION);

            Document log = new Document("timestamp", new Date())
                    .append("city", city)
                    .append("userAgent", userAgent)
                    .append("apiUrl", apiUrl)
                    .append("breweryCount", breweryCount);

            logs.insertOne(log);
            System.out.println("Log successfully inserted for city: " + city);
        } catch (Exception e) {
            System.err.println("Logging failed: " + e.getMessage());
            e.printStackTrace();  // Print stack trace for debugging
        }
    }
}
