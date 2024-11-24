package com.example.smsforwarderservice.v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.smsforwarderservice.v2.helper.GlobalConstants;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = GlobalConstants.APP_NAME;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device booted. Starting SMS listening service.");
            Intent serviceIntent = new Intent(context, SMSListeningService.class);
            context.startService(serviceIntent);
        }
    }
}
