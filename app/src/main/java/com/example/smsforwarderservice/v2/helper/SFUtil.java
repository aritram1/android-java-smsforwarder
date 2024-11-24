package com.example.smsforwarderservice.v2.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.example.smsforwarderservice.v2.AppContextProvider;
import com.example.smsforwarderservice.v2.model.SMSMessageModel;
import com.example.smsforwarderservice.v2.model.SalesforceResponseModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SFUtil extends AsyncTask<ArrayList<SMSMessageModel>, Void, Void> {
    static final String TAG = GlobalConstants.APP_NAME;

    private SalesforceResponseModel getStoredToken() {
        // Retrieve the stored token from storage (if available)
        // This avoids logging in every time
        return TokenStorage.getToken(AppContextProvider.getContext());
    }

    @Override
    protected Void doInBackground(ArrayList<SMSMessageModel>... params) {
        SalesforceResponseModel sfResponse = getStoredToken();

        // If token is null, launch OAuth flow or perform token exchange
        if (sfResponse == null || sfResponse.accessToken == null) {
            Log.d(TAG, "No valid token found, initiating login flow...");
            // Here, you can prompt for login if needed
            return null;
        }

        try {
            sendToSalesforce(params[0], sfResponse);
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while saving data to Salesforce", e);
        }
        return null;
    }

    private static void sendToSalesforce(ArrayList<SMSMessageModel> messages, SalesforceResponseModel sfResponse) {
        try {
            URL url = new URL(sfResponse.instanceUrl + GlobalConstants.RESOURCE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + sfResponse.accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            for (SMSMessageModel message : messages) {
                String payload = new JSONObject()
                        .put("FinPlan__Sender__c", message.sender)
                        .put("FinPlan__Content__c", message.content)
                        .put("FinPlan__Received_At__c", message.receivedAt)
                        .toString();

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(payload.getBytes());
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    Log.d(TAG, "Message sent successfully to Salesforce");
                } else {
                    Log.e(TAG, "Failed to send message. HTTP Response Code: " + responseCode);
                    handleErrorResponse(connection);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while sending messages to Salesforce: " + e.getMessage());
        }
    }

    private static void handleErrorResponse(HttpURLConnection connection) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            Log.e(TAG, "Salesforce API Error Response: " + response.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error reading Salesforce error response: " + e.getMessage());
        }
    }
}
