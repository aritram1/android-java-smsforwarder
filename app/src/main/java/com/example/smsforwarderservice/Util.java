package com.example.smsforwarderservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.ArrayList;

public class Util {
    public static void processSMSReceivedIntent(Context context, Intent intent){
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            //if(intent.getAction().equals("Telephony.Sms.Intents.SMS_RECEIVED_ACTION")){
            System.out.println("intent.hi()=>");
            Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            if(bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<pdus.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);//, "");
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        Toast.makeText(context, "From : "+ msg_from + "Content : " + msgBody, Toast.LENGTH_SHORT).show();
                        // System.out.println("intent.hi2()=>" + "From : "+ msg_from + "Content : " + msgBody);
                    }
                }
                catch(Exception e){
                    Toast.makeText(context, "From : "+ e.getMessage() + "Content : " + e.getCause(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                finally{
                    saveRecords(msgs);
                }
            }
        }

    }

    private static void saveRecords(SmsMessage[] msgs) {
        if(msgs != null) {
            new SFUtil().execute(msgs);
        }
    }
}

