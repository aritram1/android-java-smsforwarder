package com.example.smsforwarderservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SMSForwarder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Inside OnCreate method in main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1000);
        }

        /*
        Button startButton = findViewById(R.id.startButtonId);
        startButton.setOnClickListener(view -> {
            try {
                new SFUtil().execute();
                Toast.makeText(this, "Started!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error!!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */



        Button startButton = findViewById(R.id.startButtonId);
        Button stopButton = findViewById(R.id.stopButtonId);
        startButton.setOnClickListener(view -> {
            try {
//                Intent smsForwarderServiceIntent = new Intent(this, SMSForwarderService.class);
//                startService(smsForwarderServiceIntent);
//                Toast.makeText(this, "Started!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error!!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        stopButton.setOnClickListener(view -> {
//            Intent smsForwarderServiceIntent = new Intent(this, SMSForwarderService.class);
//            stopService(smsForwarderServiceIntent);
//            Toast.makeText(this, "Stopped!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "Inside onRequest Permission Result");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Denied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}