package edu.andrew.shreyas.brewfinder;

import com.mongodb.client.*;
import org.bson.Document;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Accumulators.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class DashboardServlet extends HttpServlet {

    private static final String CONNECTION_URI =
        "mongodb://skailas2:x3D4TWzU0oEQtMJG@" +
        "ac-wcrrvnc-shard-00-00.21wph6h.mongodb.net:27017," +
        "ac-wcrrvnc-shard-00-01.21wph6h.mongodb.net:27017," +
        "ac-wcrrvnc-shard-00-02.21wph6h.mongodb.net:27017/" +
        "brewfinderlogs?replicaSet=atlas-sjveh9-shard-0&ssl=true&authSource=admin";

    private static final String DB_NAME = "brewfinderlogs";
    private static final String COLLECTION_NAME = "requests";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        MongoClient client = MongoClients.create(CONNECTION_URI);
        MongoCollection<Document> logs = client.getDatabase(DB_NAME).getCollection(COLLECTION_NAME);

        out.println("<html><head><title>Dashboard</title></head><body>");
        out.println("<h1>Brewfinder Operations Dashboard</h1>");

        // ✅ Analytics Section
        out.println("<h2>Analytics</h2>");
        out.println("<ul>");

        // 1. Total request count
        long totalRequests = logs.countDocuments();
        out.println("<li><strong>Total Requests:</strong> " + totalRequests + "</li>");

        // 2. Top 5 most requested cities
        out.println("<li><strong>Top 5 Cities:</strong><ul>");
        logs.aggregate(Arrays.asList(
                group("$city", sum("count", 1)),
                sort(descending("count")),
                limit(5)
        )).forEach(doc -> {
            out.println("<li>" + doc.getString("_id") + " — " + doc.get("count") + " requests</li>");
        });
        out.println("</ul></li>");

        // 3. Most common Android device
        out.println("<li><strong>Top Devices:</strong><ul>");
        logs.aggregate(Arrays.asList(
                group("$userAgent", sum("count", 1)),
                sort(descending("count")),
                limit(3)
        )).forEach(doc -> {
            out.println("<li>" + doc.getString("_id") + " — " + doc.get("count") + " uses</li>");
        });
        out.println("</ul></li>");

        out.println("</ul>");

        // ✅ Logs Table Section
        out.println("<h2>Logged Requests</h2>");
        out.println("<table border='1'><tr><th>Timestamp</th><th>City</th><th>Device</th><th>Result Count</th></tr>");
        
        // Fetch the logged requests and populate the table
        MongoCursor<Document> cursor = logs.find().sort(new Document("timestamp", -1)).iterator();
        while (cursor.hasNext()) {
            Document log = cursor.next();
            out.println("<tr>");
            out.println("<td>" + (log.getString("timestamp") != null ? log.getString("timestamp") : "N/A") + "</td>");
            out.println("<td>" + (log.getString("city") != null ? log.getString("city") : "N/A") + "</td>");
            out.println("<td>" + (log.getString("userAgent") != null ? log.getString("userAgent") : "N/A") + "</td>");
            out.println("<td>" + (log.getInteger("breweryCount", 0)) + "</td>");
            out.println("</tr>");
        }
        out.println("</table>");

        out.println("</body></html>");
        client.close();
    }
}
