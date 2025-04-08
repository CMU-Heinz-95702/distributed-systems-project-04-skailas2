package edu.andrew.shreyas.brewfinder;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class BreweryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String city = request.getParameter("city");

            if (city == null || city.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Missing city parameter\"}");
                return;
            }

            String encodedCity = URLEncoder.encode(city, "UTF-8");
            String apiUrl = "https://api.openbrewerydb.org/v1/breweries?by_city=" + encodedCity;

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");

            int apiResponseCode = conn.getResponseCode();

            if (apiResponseCode != 200) {
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                response.getWriter().write("{\"error\": \"Open Brewery API error\"}");
                return;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder apiResponse = new StringBuilder();
            while (scanner.hasNext()) {
                apiResponse.append(scanner.nextLine());
            }
            scanner.close();

            JSONArray breweries = new JSONArray(apiResponse.toString());
            JSONArray filtered = new JSONArray();

            for (int i = 0; i < breweries.length(); i++) {
                JSONObject b = breweries.getJSONObject(i);
                JSONObject item = new JSONObject();
                item.put("name", b.optString("name"));
                item.put("brewery_type", b.optString("brewery_type"));
                item.put("website_url", b.optString("website_url"));
                filtered.put(item);
            }

            // ✅ Send JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(filtered.toString());

            // ✅ Log request to MongoDB
            //MongoLogger.logRequest(city, request.getHeader("User-Agent"), apiUrl, filtered.length());
            try {
    MongoLogger.logRequest(city, request.getHeader("User-Agent"), apiUrl, filtered.length());
} catch (Exception e) {
    e.printStackTrace();  // This will go to Codespaces terminal
}


        } catch (Exception e) {
            e.printStackTrace(); // logs to Codespaces terminal
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error: " + e.getMessage() + "\"}");
        }
    }
}
