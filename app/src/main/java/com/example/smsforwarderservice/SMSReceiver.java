package com.example.smsforwarderservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SalesforceOAuthCallout", "Here");
        Util.processSMSReceivedIntent(context, intent);
    }
}
