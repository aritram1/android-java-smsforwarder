package com.example.smsforwarderservice.v2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.smsforwarderservice.v2.helper.GlobalConstants;


public class SMSListeningService extends Service {
    static final String TAG = GlobalConstants.APP_NAME;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SMSListeningService created and running...");
        // Any setup logic for the service can go here
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SMSListeningService started.");
        // Keep the service running unless explicitly stopped
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This is not a bound service, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SMSListeningService destroyed.");
    }
}
