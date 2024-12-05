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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                SalesforceResponseModel sfResponse =
                        ExpensoTokenStorageService.getSavedResponse(AppContextProvider.getContext());
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
     * Perform login using OAuth password flow and retrieve a new token.
     */

    public static SalesforceResponseModel loginAndRetrieveToken() {
        final SalesforceResponseModel[] loginResponse = new SalesforceResponseModel[1];
        OAuthUtilClientCredentialsFlow.loginWithClientCredentialsFlow(new OAuthUtilClientCredentialsFlow.Callback() {
            @Override
            public void onSuccess(SalesforceResponseModel sfResponse) {
                Log.d(TAG, "Login successful. Token retrieved.");
                Log.d(TAG, "Login Successful : Salesforce Response Details:");
                Log.d(TAG, "Login Successful : Access Token: " + sfResponse.accessToken);
                Log.d(TAG, "Login Successful : Instance URL: " + sfResponse.instanceUrl);
                Log.d(TAG, "Login Successful : ID: " + sfResponse.id);
                Log.d(TAG, "Login Successful : Token Type: " + sfResponse.tokenType);
                Log.d(TAG, "Login Successful : Issued At: " + sfResponse.issuedAt);
                Log.d(TAG, "Login Successful : Signature: " + sfResponse.signature);
                ExpensoTokenStorageService.saveToken(AppContextProvider.getContext(), sfResponse);
                loginResponse[0] = sfResponse;
                showToast("Login successful: " + sfResponse.accessToken);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Login failed: " + error);
                showToast("Login failed: " + error);
            }
        });

        // Return the token retrieved
        return loginResponse[0];
    }

    /**
     * Send SMS messages to Salesforce.
     */
    private static void sendToSalesforce(ArrayList<SMSMessageModel> messages, SalesforceResponseModel sfResponse) {
        try {
            URL url = new URL(sfResponse.instanceUrl + GlobalConstants.RESOURCE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(GlobalConstants.POST);
            connection.setRequestProperty("Authorization", "Bearer " + sfResponse.accessToken);
            connection.setRequestProperty("Content-Type", GlobalConstants.CONTENT_TYPE_APPLICATION_JSON);
            connection.setDoOutput(true);

            for (SMSMessageModel message : messages) {
                boolean isTransactionMessage = isTransactionalMessage(message.content);
                if(isTransactionMessage == true) {
                    String payload = buildPayload(message);
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(payload.getBytes());
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        Log.d(TAG, "Message sent successfully to Salesforce." +
                                responseCode + " : " +
                                connection.getResponseMessage());
                    } else {
                        Log.e(TAG, "Failed to send message. HTTP Response Code: " + responseCode);
                        handleErrorResponse(connection);
                    }
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
        Log.d(TAG, "Inside buildPayload method !!");
        String formattedContent = message.content.replaceAll("\\r?\\n", " ");
        String messageExternalID = generateExternalId(message.receivedAt);
        String receivedAt = checkAndConvertToSFDateTimeFormat(message.receivedAt);
        Log.d(TAG, "messageExternalID=>" + messageExternalID);
        String payload = new JSONObject()
            .put("Sender__c", message.sender)
            .put("Received_At__c", receivedAt)
            .put("Created_From__c", "SMS")
            .put("Device__c", GlobalConstants.DEVICE_NAME)
            .put("External_Id__c", messageExternalID)
            .put("Content__c", formattedContent)
            .put("Original_Content__c", message.content)
            .toString();
        Log.d(TAG, "the payload is=>" + payload);
        return payload;
    }

    private static String checkAndConvertToSFDateTimeFormat(String receivedAt) {
        if (isNumeric(receivedAt)) {
            try {
                // Convert numeric input (milliseconds since 1970) to a Date object
                long millis = Long.parseLong(receivedAt);
                Date date = new Date(millis);

                // Format the Date object to Salesforce-compatible DateTime string
                SimpleDateFormat sfFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                sfFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // Ensure UTC format
                return sfFormat.format(date);
            } catch (NumberFormatException e) {
                // Handle large or invalid numbers gracefully
                return "";
            }
        }
        else{
            return receivedAt;
        }
    }
    private static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    private static String generateExternalId(String receivedAt) {
        return receivedAt.replaceAll(":", "")
                .replaceAll(" ", "")
                .replaceAll("-", "")
                .replaceAll("\\.", ""); // since . means any character, here we are explicitly
                                        // using slash to escape the . character
    }

    private static boolean isTransactionalMessage(String content) {

        if (content == null || content.isEmpty()) {
            return false; // Return false if content is null or empty
        }

        // Normalize content for case-insensitive matching
        content = content.toLowerCase();

        // Check for the presence of transaction-related keywords
        for(String transactionalKeyWord : GlobalConstants.TRANSACTIONAL_KEYWORDS){
            if(content.contains(transactionalKeyWord)){
                return true;
            }
        }
        return false;
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
