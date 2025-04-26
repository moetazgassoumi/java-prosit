package com.example.educonnect.educonnect.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QuoteService {
    public static String[] getRandomQuote() {
        String apiUrl = "https://zenquotes.io/api/random";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            JSONObject quoteObj = jsonArray.getJSONObject(0);
            String quote = quoteObj.getString("q");
            String author = quoteObj.getString("a");

            return new String[]{quote, author};

        } catch (Exception e) {
            return new String[]{"Erreur de citation", ""};
        }
    }
}
