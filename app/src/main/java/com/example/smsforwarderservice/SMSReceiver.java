package com.example.smsforwarderservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSForwarder";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Inside SMS Receiver");
        Util.processSMSReceivedIntent(context, intent);
    }
}
