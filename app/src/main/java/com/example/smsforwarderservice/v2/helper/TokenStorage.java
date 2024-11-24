package com.example.smsforwarderservice.v2.helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.smsforwarderservice.v2.model.SalesforceResponseModel;

public class TokenStorage {
    private static final String PREFS_NAME = "token_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_INSTANCE_URL = "instance_url";
    private static final String KEY_ISSUED_AT = "issued_at";

    public static void saveToken(Context context, SalesforceResponseModel response) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, response.accessToken);
        editor.putString(KEY_INSTANCE_URL, response.instanceUrl);
        editor.putString(KEY_ISSUED_AT, response.issuedAt);
        editor.apply();
    }

    public static SalesforceResponseModel getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SalesforceResponseModel response = new SalesforceResponseModel();
        response.accessToken = prefs.getString(KEY_ACCESS_TOKEN, null);
        response.instanceUrl = prefs.getString(KEY_INSTANCE_URL, null);
        response.issuedAt = prefs.getString(KEY_ISSUED_AT, null);
        return response;
    }
}
