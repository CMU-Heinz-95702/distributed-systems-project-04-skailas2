<%@ page import="com.mongodb.client.*, org.bson.Document" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Brewfinder Dashboard</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        h1 { color: #2c3e50; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
<h1>Brewfinder Operations Dashboard</h1>

<h2>Analytics</h2>
<%-- MongoDB connection --%>
<%
    String uri = "mongodb://skailas2:x3D4TWzU0oEQtMJG@" +
            "ac-wcrrvnc-shard-00-00.21wph6h.mongodb.net:27017," +
            "ac-wcrrvnc-shard-00-01.21wph6h.mongodb.net:27017," +
            "ac-wcrrvnc-shard-00-02.21wph6h.mongodb.net:27017/" +
            "brewfinderlogs?replicaSet=atlas-sjveh9-shard-0&ssl=true&authSource=admin";

    MongoClient client = MongoClients.create(uri);
    MongoDatabase db = client.getDatabase("brewfinderlogs");
    MongoCollection<Document> coll = db.getCollection("requests");

    // Top 5 cities
    Document group = new Document("$group", new Document("_id", "$city").append("count", new Document("$sum", 1)));
    Document sort = new Document("$sort", new Document("count", -1));
    Document limit = new Document("$limit", 5);
    AggregateIterable<Document> topCities = coll.aggregate(Arrays.asList(group, sort, limit));
%>
<table>
    <tr><th>Top 5 Cities</th><th>Search Count</th></tr>
    <% for (Document doc : topCities) { %>
        <tr>
            <td><%= doc.getString("_id") %></td>
            <td><%= doc.get("count") %></td>
        </tr>
    <% } %>
</table>

<h2>Request Logs</h2>
<table>
    <tr>
        <th>Timestamp</th>
        <th>City</th>
        <th>User Agent</th>
        <th>Brewery Count</th>
    </tr>
<%
    FindIterable<Document> logs = coll.find().sort(new Document("timestamp", -1)).limit(50);
    for (Document log : logs) {
%>
    <tr>
        <td><%= log.getDate("timestamp") %></td>
        <td><%= log.getString("city") %></td>
        <td><%= log.getString("userAgent") %></td>
        <td><%= log.get("breweryCount") %></td>
    </tr>
<% } %>
</table>
</body>
</html>
