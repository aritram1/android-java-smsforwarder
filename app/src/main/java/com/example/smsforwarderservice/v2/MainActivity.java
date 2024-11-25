package com.example.smsforwarderservice.v2;

import com.example.smsforwarderservice.R;
import com.example.smsforwarderservice.v2.helper.GlobalConstants;
import com.example.smsforwarderservice.v2.helper.OAuthUtilPasswordFlow;
import com.example.smsforwarderservice.v2.model.SalesforceResponseModel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = GlobalConstants.APP_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();

        // Add Close event button listener
        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity
                finish();
            }
        });
    }

    /**
     * Check and request necessary permissions.
     */
    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS
        };

        List<String> neededPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }

        if (!neededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toArray(new String[0]), 1001);
        } else {
            proceedWithFunctionality();
        }
    }

    /**
     * Proceed with app functionality once permissions are granted.
     */
    private void proceedWithFunctionality() {
        Toast.makeText(this, "App functionality ready.", Toast.LENGTH_SHORT).show();
        performLogin();
    }

    /**
     * Perform Salesforce login using OAuth password flow.
     */
    private void performLogin() {
        OAuthUtilPasswordFlow.loginWithPasswordFlow(new OAuthUtilPasswordFlow.Callback() {
            @Override
            public void onSuccess(SalesforceResponseModel sfResponse) {
                Log.d(TAG, "Login Successful : Salesforce Response Details:");
                Log.d(TAG, "Login Successful : Access Token: " + sfResponse.accessToken);
                Log.d(TAG, "Login Successful : Instance URL: " + sfResponse.instanceUrl);
                Log.d(TAG, "Login Successful : ID: " + sfResponse.id);
                Log.d(TAG, "Login Successful : Token Type: " + sfResponse.tokenType);
                Log.d(TAG, "Login Successful : Issued At: " + sfResponse.issuedAt);
                Log.d(TAG, "Login Successful : Signature: " + sfResponse.signature);

                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Login Failed: " + error);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Login Failed: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Handle the result of permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                proceedWithFunctionality();
            } else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
