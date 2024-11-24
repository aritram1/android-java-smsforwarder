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

public class OAuthUtilPasswordFlow {
    static final String TAG = GlobalConstants.APP_NAME;

    // Perform login using the username-password flow and return the token
    public static SalesforceResponseModel loginWithPasswordFlow() {
        HttpURLConnection connection = null;
        SalesforceResponseModel sfResponse = new SalesforceResponseModel();
        try {
            // Build the login URL
            URL url = new URL(GlobalConstants.TOKEN_ENDPOINT);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty(GlobalConstants.CONTENT_TYPE_HEADER_NAME, GlobalConstants.CONTENT_TYPE_APPLICATION_FORM_URL_ENCODED);
            connection.setDoOutput(true);

            // Prepare the request body
            String body = String.format(
                    "grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
                    GlobalConstants.CLIENT_ID,
                    GlobalConstants.CLIENT_SECRET,
                    GlobalConstants.USERNAME,
                    GlobalConstants.PASSWORD
            );

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                os.write(body.getBytes());
            }

            // Check the response
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    JSONObject response = new JSONObject(br.readLine());
                    sfResponse.accessToken = response.getString("access_token");
                    sfResponse.instanceUrl = response.getString("instance_url");
                    sfResponse.id = response.getString("id");
                    sfResponse.tokenType = response.getString("token_type");
                    sfResponse.issuedAt = response.getString("issued_at");
                    sfResponse.signature = response.getString("signature");

                    // Optionally save the token for reuse
                    TokenStorage.saveToken(AppContextProvider.getContext(), sfResponse);
                }
            } else {
                Log.e(TAG, "Failed to authenticate. HTTP response code: " + connection.getResponseCode());
                handleErrorResponse(connection);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during password-based OAuth2 login: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return sfResponse;
    }

    // Handle error responses from the server
    private static void handleErrorResponse(HttpURLConnection connection) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            Log.e(TAG, "Error response from server: " + response.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error reading error response: " + e.getMessage());
        }
    }
}
