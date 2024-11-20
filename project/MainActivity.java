package com.example.weatherdressapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText etPlace, etGender, etTemperature, etWeatherCondition;
    Button btnRecommend;
    TextView tvRecommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPlace = findViewById(R.id.et_place);
        etGender = findViewById(R.id.et_gender);
        etTemperature = findViewById(R.id.et_temperature);  // Add this EditText for temperature
        etWeatherCondition = findViewById(R.id.et_weather_condition);  // Add this EditText for weather condition
        btnRecommend = findViewById(R.id.btn_recommend);
        tvRecommendation = findViewById(R.id.tv_recommendation);

        btnRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = etPlace.getText().toString();
                String gender = etGender.getText().toString();
                String temperature = etTemperature.getText().toString();  // Get temperature
                String weatherCondition = etWeatherCondition.getText().toString();  // Get weather condition

                // Validate user input
                if (place.isEmpty() || gender.isEmpty() || temperature.isEmpty() || weatherCondition.isEmpty()) {
                    tvRecommendation.setText("Please fill in all fields.");
                    return;
                }

                // Call the Flask API with API key
                new Thread(() -> {
                    try {
                        String apiUrl = "http://10.0.2.2:5001/recommend";// For Android emulator
                        URL url = new URL(apiUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Authorization", "Bearer 3b72f47ae42bf73811a6322872032e08");  // Your API key here
                        conn.setDoOutput(true);

                        // Send place, gender, temperature, and weather condition as input
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("place", place);
                        jsonParam.put("gender", gender);
                        jsonParam.put("temperature", temperature);  // Include temperature
                        jsonParam.put("condition", weatherCondition);  // Include weather condition

                        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                        out.write(jsonParam.toString());
                        out.flush();

                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder responseBuilder = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            responseBuilder.append(inputLine);
                        }
                        String response = responseBuilder.toString();

                        JSONObject jsonResponse = new JSONObject(response);
                        String recommendation = jsonResponse.getString("recommendation");

                        runOnUiThread(() -> tvRecommendation.setText("Recommendation: " + recommendation));

                        in.close();
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> tvRecommendation.setText("Error: " + e.getMessage()));
                    }
                }).start();
            }
        });
    }
}
