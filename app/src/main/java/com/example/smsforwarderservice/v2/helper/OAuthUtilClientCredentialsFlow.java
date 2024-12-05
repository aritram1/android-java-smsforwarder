package com.example.smsforwarderservice.v2.helper;

import android.util.Log;

import com.example.smsforwarderservice.v2.AppContextProvider;
import com.example.smsforwarderservice.v2.model.SalesforceResponseModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OAuthUtilClientCredentialsFlow {
    private static final String TAG = GlobalConstants.APP_NAME;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Perform login using client credentials flow without any user
     * interaction
     * @param callback A callback to handle the result.
     */
    public static void loginWithClientCredentialsFlow(Callback callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            SalesforceResponseModel sfResponse = new SalesforceResponseModel();
            try {
                URL url = new URL(GlobalConstants.TOKEN_ENDPOINT_FOR_CLIENT_CREDENTIALS_FLOW);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty(
                        GlobalConstants.CONTENT_TYPE_HEADER_NAME,
                        GlobalConstants.CONTENT_TYPE_APPLICATION_FORM_URL_ENCODED);
                connection.setDoOutput(true);

                // Build the request body
                String body = String.format(
                        "grant_type=%s" +
                        "&client_id=%s" +
                        "&client_secret=%s",
                        GlobalConstants.GRANT_TYPE_CLIENT_CREDENTIALS,
                        GlobalConstants.CLIENT_ID,
                        GlobalConstants.CLIENT_SECRET
                );

                Log.d(TAG, "Request Body: " + body);

                // Send the request
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body.getBytes());
                    Log.d(TAG, "Request sent successfully.");
                }

                // Handle the response
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    sfResponse = parseSuccessResponse(connection);
                    ExpensoTokenStorageService.saveToken(AppContextProvider.getContext(), sfResponse);

                    // Pass result back via callback
                    if (callback != null) {
                        callback.onSuccess(sfResponse);
                    }
                } else {
                    handleErrorResponse(connection);
                    if (callback != null) {
                        callback.onFailure("Failed to retrieve token. Response Code: " + responseCode);
                    }
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Error during password-based OAuth2 login: ", e);
                if (callback != null) {
                    callback.onFailure("Exception occurred: " + e.getMessage());
                }
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private static SalesforceResponseModel parseSuccessResponse(HttpURLConnection connection) throws Exception {
        SalesforceResponseModel sfResponse = new SalesforceResponseModel();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            JSONObject response = new JSONObject(br.readLine());
            sfResponse.accessToken = response.getString("access_token");
            sfResponse.instanceUrl = response.getString("instance_url");
            sfResponse.id = response.getString("id");
            sfResponse.tokenType = response.getString("token_type");
            sfResponse.issuedAt = response.getString("issued_at");
            sfResponse.signature = response.getString("signature");
            Log.d(TAG, "Token successfully retrieved: " + sfResponse.accessToken);
        }
        return sfResponse;
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
            Log.e(TAG, "Error reading Salesforce error response: ", e);
        }
    }

    /**
     * Callback interface for asynchronous results.
     */
    public interface Callback {
        void onSuccess(SalesforceResponseModel sfResponse);
        void onFailure(String error);
    }
}