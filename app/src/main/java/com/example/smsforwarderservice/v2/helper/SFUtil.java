package com.example.smsforwarderservice.v2.helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SFUtil {
    private static final String TAG = GlobalConstants.APP_NAME;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Main method to send SMS messages to Salesforce asynchronously.
     */
    public static void sendMessagesToSalesforce(ArrayList<SMSMessageModel> messages) {
        executor.execute(() -> {
            try {
                // Retrieve stored Salesforce token or login if not available
                SalesforceResponseModel sfResponse = getStoredResponse();
                if (sfResponse == null || sfResponse.accessToken == null) {
                    Log.d(TAG, "No valid token found. Initiating login flow...");
                    sfResponse = loginAndRetrieveToken();
                }

                // If token retrieval was successful, send messages
                if (sfResponse != null && sfResponse.accessToken != null) {
                    sendToSalesforce(messages, sfResponse);
                } else {
                    Log.e(TAG, "Failed to retrieve Salesforce token. Messages not sent.");
                    showToast("Failed to authenticate with Salesforce.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in sendMessagesToSalesforce: ", e);
                showToast("Error sending messages to Salesforce: " + e.getMessage());
            }
        });
    }

    /**
     * Retrieve the stored Salesforce token from local storage.
     */
    private static SalesforceResponseModel getStoredResponse() {
        return TokenStorage.getSavedResponse(AppContextProvider.getContext());
    }

    /**
     * Perform login using OAuth password flow and retrieve a new token.
     */
    private static SalesforceResponseModel loginAndRetrieveToken() {
        final SalesforceResponseModel[] response = {null};
        OAuthUtilPasswordFlow.loginWithPasswordFlow(new OAuthUtilPasswordFlow.Callback() {
            @Override
            public void onSuccess(SalesforceResponseModel sfResponse) {
                Log.d(TAG, "Login successful. Token retrieved.");
                response[0] = sfResponse;
                TokenStorage.saveToken(AppContextProvider.getContext(), sfResponse);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Login failed: " + error);
                showToast("Login failed: " + error);
            }
        });

        // Return the token retrieved
        return response[0];
    }

    /**
     * Send SMS messages to Salesforce.
     */
    private static void sendToSalesforce(ArrayList<SMSMessageModel> messages, SalesforceResponseModel sfResponse) {
        try {
            URL url = new URL(sfResponse.instanceUrl + GlobalConstants.RESOURCE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + sfResponse.accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            for (SMSMessageModel message : messages) {
                String payload = buildPayload(message);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(payload.getBytes());
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    Log.d(TAG, "Message sent successfully to Salesforce.");
                } else {
                    Log.e(TAG, "Failed to send message. HTTP Response Code: " + responseCode);
                    handleErrorResponse(connection);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while sending messages to Salesforce: ", e);
        }
    }

    /**
     * Build the JSON payload for a given SMS message.
     */
    private static String buildPayload(SMSMessageModel message) throws Exception {
        return new JSONObject()
                .put("FinPlan__Sender__c", message.sender)
                .put("FinPlan__Content__c", message.content)
                .put("FinPlan__Received_At__c", message.receivedAt)
                .toString();
    }

    /**
     * Handle error responses from Salesforce.
     */
    private static void handleErrorResponse(HttpURLConnection connection) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            Log.e(TAG, "Salesforce API Error Response: " + response.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error reading Salesforce error response: ", e);
        }
    }

    /**
     * Show a toast message on the main thread.
     */
    private static void showToast(String message) {
        handler.post(() -> Toast.makeText(AppContextProvider.getContext(), message, Toast.LENGTH_SHORT).show());
    }
}
