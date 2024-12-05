package com.example.smsforwarderservice.v2.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.smsforwarderservice.v2.model.SalesforceResponseModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OAuthUtilAuthorizationCodeFlow {
    static final String TAG = GlobalConstants.APP_NAME;

    // Launch the authorization flow in a browser
    public static void launchAuthFlow(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null. Unable to launch OAuth flow.");
            return;
        }

        String authUrl = String.format(
                "%s?response_type=code&client_id=%s&redirect_uri=%s",
                GlobalConstants.AUTH_ENDPOINT,
                GlobalConstants.CLIENT_ID,
                GlobalConstants.REDIRECT_URI
        );

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Ensure it opens from non-activity contexts
        context.startActivity(browserIntent);
    }

    // Exchange authorization code for an access token
    public static SalesforceResponseModel exchangeCodeForToken(String code, Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null. Unable to exchange code for token.");
            return null;
        }

        HttpURLConnection connection = null;
        SalesforceResponseModel sfResponse = new SalesforceResponseModel();
        try {
            URL url = new URL(GlobalConstants.TOKEN_ENDPOINT);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty(GlobalConstants.CONTENT_TYPE_HEADER_NAME, GlobalConstants.CONTENT_TYPE_APPLICATION_FORM_URL_ENCODED);
            connection.setDoOutput(true);

            String body = String.format(
                    "grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=%s",
                    code,
                    GlobalConstants.CLIENT_ID,
                    GlobalConstants.CLIENT_SECRET,
                    GlobalConstants.REDIRECT_URI
            );

            try (OutputStream os = connection.getOutputStream()) {
                os.write(body.getBytes());
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    JSONObject response = new JSONObject(br.readLine());
                    sfResponse.accessToken = response.getString("access_token");
                    sfResponse.instanceUrl = response.getString("instance_url");
                    sfResponse.id = response.getString("id");
                    sfResponse.tokenType = response.getString("token_type");
                    sfResponse.issuedAt = response.getString("issued_at");
                    sfResponse.signature = response.getString("signature");

                    // Save token to storage for reuse
                    ExpensoTokenStorageService.saveToken(context, sfResponse);
                }
            } else {
                Log.e(TAG, "Failed to exchange code for token. HTTP response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error exchanging code for token: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return sfResponse;
    }
}