package com.example.smsforwarderservice;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class test {
    private static final String TAG = "SalesforceOAuthCallout";
    private static final String TOKEN_ENDPOINT = "https://login.salesforce.com/services/oauth2/token";
    private static final String SALESFORCE_API_ENDPOINT = "https://your-instance.salesforce.com/services/data/vXX.X/sobjects/YourObject";
    private static final String CLIENT_ID = "3MVG9wt4IL4O5wvIBCa0yrhLb82rC8GGk03G2F26xbcntt9nq1JXS75mWYnnuS2rxwlghyQczUFgX4whptQeT";
    private static final String CLIENT_SECRET = "3E0A6C0002E99716BD15C7C35F005FFFB716B8AA2DE28FBD49220EC238B2FFC7";
    private static final String USERNAME = "aritram1@gmail.com.financeplanner";
    private static final String PASSWORD = "financeplanner123W8oC4taee0H2GzxVbAqfVB14";
    private static final String GRANT_TYPE_PASSWORD = "password";

    private String accessToken;

    public void loginToSalesforce() {
        HttpURLConnection connection = null;
        try {
            String urlString = TOKEN_ENDPOINT + "?" +
                    "grant_type=" + GRANT_TYPE_PASSWORD +
                    "&client_id=" + CLIENT_ID +
                    "&client_secret=" + CLIENT_SECRET +
                    "&username=" + USERNAME +
                    "&password=" + PASSWORD;
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Get the HTTP response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse the JSON response to obtain the access token
                    String strResponse = response.toString();
                    System.out.println("Response1" + strResponse);
                    accessToken = new JSONObject(strResponse).getString("access_token");
                }
            } else {
                Log.e(TAG, "Error obtaining access token. HTTP response code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during OAuth callout: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
