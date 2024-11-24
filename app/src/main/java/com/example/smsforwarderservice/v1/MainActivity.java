package com.example.smsforwarderservice.v1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smsforwarderservice.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = GlobalConstants.APP_NAME; // "SMSForwarder"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        String[] allPermissions = {
                Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_PHONE_STATE
        };

        List<String> permissionsNeeded = new ArrayList<>();

        for (String permission : allPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), 1002);
        } else {
            // All permissions are already granted, proceed with further setup
            proceedWithAppFunctionality();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1002) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
                proceedWithAppFunctionality();
            } else {
                Toast.makeText(this, "Some permissions denied! The app may not work correctly.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void proceedWithAppFunctionality() {
        // This method would contain all the actions to be performed once all permissions are granted
        Toast.makeText(this, "Setting up app functionalities.", Toast.LENGTH_SHORT).show();
        // Further setup code here, e.g., initializing views, setting up services, etc.
    }

}
