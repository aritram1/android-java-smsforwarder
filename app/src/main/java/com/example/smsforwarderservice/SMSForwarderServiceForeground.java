package com.example.smsforwarderservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SMSForwarderServiceForeground extends Service {

    private static final int FOREGROUND_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        Util.processSMSReceivedIntent(context, intent);

        // Start service as a foreground service
        startForeground(FOREGROUND_ID, createNotification());

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "sms_channel",
                    "SMS Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, "sms_channel");
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        return builder
                .setContentTitle("SMSReceiverService")
                .setContentText("Running in the background")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }
}

