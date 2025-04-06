package com.example.brewfinder;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String BACKEND_URL = "https://didactic-waddle-4jvv7pg79qj9f5w7r.github.dev/brewery?city=pittsburgh";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.resultText);

        new Thread(() -> {
            try {
                URL url = new URL(BACKEND_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();

                JSONArray breweries = new JSONArray(result.toString());
                StringBuilder output = new StringBuilder();

                for (int i = 0; i < breweries.length(); i++) {
                    JSONObject b = breweries.getJSONObject(i);
                    String name = b.optString("name");
                    String type = b.optString("brewery_type");
                    String urlStr = b.optString("website_url");

                    output.append("Name: ").append(name).append("\n")
                          .append("Type: ").append(type).append("\n")
                          .append("Website: ").append(urlStr).append("\n\n");
                }

                runOnUiThread(() -> textView.setText(output.toString()));

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        }).start();
    }
}
