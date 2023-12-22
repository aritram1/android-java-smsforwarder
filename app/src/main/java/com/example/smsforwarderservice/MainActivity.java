package com.example.smsforwarderservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SMSForwarder";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Inside OnCreate method in main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1000);
        }
        if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, 1001);
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
                  sendAllSMS();
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

    private void sendAllSMS() {
        new SendSMSTask().execute();
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
        else if (requestCode == 1001) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Denied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}