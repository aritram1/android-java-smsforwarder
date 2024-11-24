package com.example.smsforwarderservice.v1;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SFUtil extends AsyncTask<ArrayList<SMSMessageModel>, Void, Void> {

    public SalesforceResponseModel sf_response;
    private static final String TAG = "SMSForwarder";

    @Override
    protected Void doInBackground(ArrayList<SMSMessageModel>... params) {
        if (sf_response == null || sf_response.accessToken == null) loginToSalesforce();
        try{
            sendPlatformEvents(params[0]);
            // saveToSalesforce(params[0]);
        }
        catch(Exception e){
            Log.e(TAG, "Error occurred while saving data to salesforce", e);
            e.printStackTrace();
        }
        return null;
    }

    void loginToSalesforce() {
        HttpURLConnection connection = null;
        try {
            String urlString = GlobalConstants.TOKEN_ENDPOINT + "?" +
                    "grant_type=" + GlobalConstants.GRANT_TYPE_PASSWORD +
                    "&client_id=" + GlobalConstants.CLIENT_ID +
                    "&client_secret=" + GlobalConstants.CLIENT_SECRET +
                    "&username=" + GlobalConstants.USERNAME +
                    "&password=" + GlobalConstants.PASSWORD;
            URL url = new URL(urlString);
            Log.d(TAG, "Login Endpoint url is =>"  + url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(GlobalConstants.POST);

            // Set header properties
            connection.setRequestProperty(GlobalConstants.CONTENT_TYPE_HEADER_NAME, GlobalConstants.CONTENT_TYPE_APPLICATION_FORM_URL_ENCODED);

            // Read the response and get the HTTP response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Convert the response to String
                    String strResponse = response.toString();
                    Log.d(TAG, "Login Response :" + strResponse);

                    // Parse the JSON response to obtain the access token
                    JSONObject jObj = new JSONObject(strResponse);
                    sf_response = new SalesforceResponseModel();
                    sf_response.accessToken = jObj.getString(GlobalConstants.ACCESS_TOKEN);
                    sf_response.instanceUrl = jObj.getString(GlobalConstants.INSTANCE_URL);
                    sf_response.id = jObj.getString(GlobalConstants.ID);
                    sf_response.tokenType = jObj.getString(GlobalConstants.TOKEN_TYPE);
                    sf_response.issuedAt = jObj.getString(GlobalConstants.ISSUED_AT);
                    sf_response.signature = jObj.getString(GlobalConstants.SIGNATURE);
                }
            } else {
                Log.e(TAG, "Error obtaining access token. HTTP response code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception during OAuth call-out : " + e.getMessage());
        } finally {
            if (connection != null) {
                Log.d(TAG, "Login Method finishes with token : " + sf_response.accessToken);
                connection.disconnect();
            }
        }
    }

    void sendPlatformEvents(ArrayList<SMSMessageModel> msgs) {
        Log.d(TAG, "sendPlatformEvents method starts with token : " + sf_response.accessToken);
        try {
            Log.d(TAG, "Inside try block for sendPlatformEvents");

            URL url = new URL(sf_response.instanceUrl + "/services/data/v59.0/sobjects/FinPlan__SMS_Message_PE__e");
            Log.d(TAG, "sendPlatformEvents Endpoint url is =>"  + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + sf_response.accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true); // Enable input and output streams

            SMSMessageModel sms = msgs.get(0);
            String sender = sms.sender;//getOriginatingAddress();
            String content = sms.content.replace("\n", " ");
            content = content.length() < 255
                    ? content
                    : content.substring(0, 255);
            String receivedAt = String.valueOf(sms.receivedAt);
            String deviceName = Build.MODEL; //(Build.MANUFACTURER) + "-" + Build.MODEL
            String createdFrom = "SMS";

            Log.d(TAG, "sender=" + sender);
            Log.d(TAG, "content=" + content);
            Log.d(TAG, "createdFrom=" + createdFrom);
            Log.d(TAG, "receivedAt=" + receivedAt);
            Log.d(TAG, "deviceName=" + deviceName);

            String jsonPayload = "{"
                    + "\"FinPlan__Sender__c\":\"" + sender + "\","
                    + "\"FinPlan__Content__c\":\"" + content + "\","
                    + "\"FinPlan__Device__c\":\"" + deviceName + "\","
                    + "\"FinPlan__Created_From__c\":\"" + createdFrom + "\","
                    + "\"FinPlan__Received_At__c\":\"" + receivedAt + "\""
                    + "}";

            Log.d(TAG, "jsonPayload=>" + jsonPayload);

            // Write the JSON payload to the output stream
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(jsonPayload.getBytes());
            }

            // Get the HTTP response code
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Save to Salesforce call-out responseCode=>" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Log or process the response as needed
                Log.d(TAG, "Salesforce API Response: " + response);
            } else {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    String line;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    Log.e(TAG, "here Error Response: " + errorResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "Error making Salesforce API callout. HTTP response code: " + connection.getResponseCode());
            }

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception during Salesforce API callout: " + e.getMessage());
        }
    }

    void saveToSalesforce(ArrayList<SMSMessageModel> msgs) {
        Log.d(TAG, "Save to Salesforce method starts with token : " + sf_response.accessToken);
        try {
            Log.d(TAG, "Inside try block for save to salesforce");
            URL url = new URL(sf_response.instanceUrl + GlobalConstants.RESOURCE_URL);
            Log.d(TAG, "Save to SF Endpoint url is =>"  + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + sf_response.accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true); // Enable input and output streams

            SMSMessageModel sms = msgs.get(0);
            String sender = sms.sender;//getOriginatingAddress();
            String content = sms.content.replace("\n", " ");
            content = content.length() < 255
                                    ? content
                                    : content.substring(0, 255);
            String receivedAt = String.valueOf(sms.receivedAt);
            String deviceName = Build.MODEL; //(Build.MANUFACTURER) + "-" + Build.MODEL
            String createdFrom = "SMS";

            Log.d(TAG, "sender=" + sender);
            Log.d(TAG, "content=" + content);
            Log.d(TAG, "createdFrom=" + createdFrom);
            Log.d(TAG, "receivedAt=" + receivedAt);
            Log.d(TAG, "deviceName=" + deviceName);

            String jsonPayload = "{"
                    + "\"FinPlan__Sender__c\":\"" + sender + "\","
                    + "\"FinPlan__Content__c\":\"" + content + "\","
                    + "\"FinPlan__Device__c\":\"" + deviceName + "\","
                    + "\"FinPlan__Created_From__c\":\"" + createdFrom + "\","
                    + "\"FinPlan__Received_At__c\":\"" + receivedAt + "\""
                    + "}";

            Log.d(TAG, "jsonPayload=>" + jsonPayload);

            // Write the JSON payload to the output stream
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(jsonPayload.getBytes());
            }

            // Get the HTTP response code
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Save to Salesforce call-out responseCode=>" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Log or process the response as needed
                Log.d(TAG, "Salesforce API Response: " + response);
            } else {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    String line;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    Log.e(TAG, "here Error Response: " + errorResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "Error making Salesforce API callout. HTTP response code: " + connection.getResponseCode());
            }

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception during Salesforce API callout: " + e.getMessage());
        }
    }
}