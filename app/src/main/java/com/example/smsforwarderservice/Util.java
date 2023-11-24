package com.example.smsforwarderservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class Util {
    private static final String TAG = "SMSForwarder";
    public static void processSMSReceivedIntent(Context context, Intent intent){
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.d(TAG, "SMS/Intent received");
            Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            if(bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<pdus.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        String result = "From : " + msg_from + "Content : " + msgBody;
                        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "SMS Content : " + result);
                    }
                    Log.d(TAG, "All Messages are processed =>" + msgs != null ? "Y" : "N");
                    sendToSalesforce(msgs);
                    Log.d(TAG, "Send To Salesforce is finished");
                }
                catch(Exception e){
                    Log.e(TAG, "Error occurred in processing SMS in receiver :" + e.getMessage());
                    Toast.makeText(context, "From : "+ e.getMessage() + "Content : " + e.getCause(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private static void sendToSalesforce(SmsMessage[] msgs) {
        if(msgs != null) {
            new SFUtil().execute(msgs);
        }
    }
}

