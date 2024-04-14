package com.example.smsforwarderservice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = GlobalConstants.APP_NAME; // "SMSForwarder"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        // Check if the app has the required SMS permissions
        boolean smsPermissionsGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;

        // Check if the app has the required CONTACTS permissions
        boolean contactsPermissionsGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED;

        // If the app doesn't have the SMS permissions, request them
        if (!smsPermissionsGranted) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS},
                    1000);
        }

        // If the app doesn't have the CONTACTS permissions, request them
        if (!contactsPermissionsGranted) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                    1001);
        }

        // If the app already has the SMS and CONTACTS permissions, proceed with further setup
        if (smsPermissionsGranted && contactsPermissionsGranted) {
            // TBA - Add further setup code here
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 || requestCode == 1001) {
            // Check if all requested permissions are granted
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            // Add your logic after getting permissions here
            if (allPermissionsGranted)
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "Permissions denied! The app may not work correctly.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
