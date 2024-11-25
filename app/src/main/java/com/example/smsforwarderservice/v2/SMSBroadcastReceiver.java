package com.example.smsforwarderservice.v2;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.smsforwarderservice.v2.helper.GlobalConstants;
import com.example.smsforwarderservice.v2.helper.Util;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    static final String TAG = GlobalConstants.APP_NAME;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = Util.extractSMSContent(bundle);
            if (msgs != null && msgs.length > 0) {
                Util.checkAndForwardAsRequired(context, msgs[0]);
                Util.sendToSalesforce(msgs);
            }
        }
    }
}
