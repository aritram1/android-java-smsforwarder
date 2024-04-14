package com.example.smsforwarderservice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = GlobalConstants.APP_NAME; // SMSForwarder

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Inside onReceive method of SMS receiver!");
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.d(TAG, "SMS/Intent received");
            Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            if(bundle != null){
                try{
                    msgs = Util.extractSMSContent(bundle, context);
                    Log.d(TAG, "All Messages are processed =>" + msgs != null ? "Y" : "N");

                    try{
                        // As first step, check and forward as required
                        Util.checkAndForwardAsRequired(context, msgs[0]);
                        Log.d(TAG, "Send To Recipients is finished");
                    }
                    catch(Exception e){
                        Log.e(TAG, "Error occurred when processing forwardToOtherRecipients: " + e.getMessage());
                        e.printStackTrace();
                    }

                    try{
                        if(msgs[0].getMessageBody().toUpperCase().contains("OTP") || msgs[0].getMessageBody().toUpperCase().contains("VERIFICATION CODE")){
                            // Nothing required
                            Log.d(TAG, "Send To Salesforce is not required");
                        }
                        else{
                            // Then send to Salesforce
                            // Util.sendToSalesforce(msgs);
                            Util.sendToSalesforce(msgs);
                            Log.d(TAG, "Send To Salesforce is finished");
                        }

                    }
                    catch(Exception e){
                        Log.e(TAG, "Error occurred when processing forwardToOtherRecipients: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                catch(Exception e){
                    Log.e(TAG, "Error occurred in processing SMS in receiver :" + e.getMessage());
                    Toast.makeText(context, "From : "+ e.getMessage() + "Content : " + e.getCause(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "onReceive method finishes successfully!");
    }
}
