package com.example.educonnect.educonnect.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {

    public static String getWeather(String city) {
        String apiKey = "9b79110bf5cf96424ba3e92033e5dd30";
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                "&appid=" + apiKey + "&units=metric&lang=fr";

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

            JSONObject json = new JSONObject(response.toString());
            String description = json.getJSONArray("weather").getJSONObject(0).getString("description");
            double temperature = json.getJSONObject("main").getDouble("temp");
            int humidity = json.getJSONObject("main").getInt("humidity");

            return "🌤️ " + description + "\n🌡 " + temperature + "°C\n💧 " + humidity + "%";
        } catch (Exception e) {
            return "❌ Erreur météo : " + e.getMessage();
        }
    }
}
