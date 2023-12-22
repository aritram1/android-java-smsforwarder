package com.example.smsforwarderservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;

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
                    forwardToOtherRecipients(context, msgs[0]);
                    Log.d(TAG, "Send To Recipients is finished");
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
            ArrayList<SMSMessageModel> sms_msgs = new ArrayList<SMSMessageModel>();
            for(SmsMessage sms : msgs){
                SMSMessageModel eachSMS = new SMSMessageModel();
                eachSMS.content = sms.getMessageBody();
                eachSMS.receivedAt = String.valueOf(sms.getTimestampMillis());
                eachSMS.sender = sms.getOriginatingAddress();
                sms_msgs.add(eachSMS);
            }
            new SFUtil().execute(sms_msgs);
        }
    }

    private static void forwardToOtherRecipients(Context context, SmsMessage sms) {

        String recipient0 = ContactUtil.getRecipientIdFromContactNames(context, "Me");
        String recipient1 = ContactUtil.getRecipientIdFromContactNames(context, "Pupu");
        String recipient2 = ContactUtil.getRecipientIdFromContactNames(context, "bhai aritra");
        String recipient3 = ContactUtil.getRecipientIdFromContactNames(context, "Munnu ma");
        Log.d(TAG, "Me =>" + recipient0);
        Log.d(TAG, "Pupu =>" + recipient1);
        Log.d(TAG, "chotoBhai =>" + recipient2);
        Log.d(TAG, "munnuMa => " + recipient3);

        String content = sms.getMessageBody().toUpperCase();
        String modifiedContent;
        if(content.contains("HOTSTAR VERIFICATION CODE")) {
            modifiedContent = "OTP for Hotstar App => " + content.split(" ")[1];
            SmsManager smsManager = SmsManager.getDefault();
            if(recipient0 != null) smsManager.sendTextMessage(recipient0.replace(" ", ""), null, modifiedContent, null, null);
             if(recipient1 != null) smsManager.sendTextMessage(recipient1.replace(" ", ""), null, modifiedContent, null, null);
             if(recipient2 != null) smsManager.sendTextMessage(recipient2.replace(" ", ""), null, modifiedContent, null, null);
             if(recipient3 != null) smsManager.sendTextMessage(recipient3.replace(" ", ""), null, modifiedContent, null, null);
        }
        if(content.contains("HOICHOI VERIFICATION CODE")) {
            modifiedContent = "OTP for Hoichoi App  => " + content.split(" ")[6];
            SmsManager smsManager = SmsManager.getDefault();
            if(recipient0 != null) smsManager.sendTextMessage(recipient0.replace(" ", ""), null, modifiedContent, null, null);
             if(recipient1 != null) smsManager.sendTextMessage(recipient1.replace(" ", ""), null, modifiedContent, null, null);
             if(recipient2 != null) smsManager.sendTextMessage(recipient2.replace(" ", ""), null, modifiedContent, null, null);
             if(recipient3 != null) smsManager.sendTextMessage(recipient3.replace(" ", ""), null, modifiedContent, null, null);
        }
    }


//    static ArrayList<SMSMessageModel> getAllSMS(Context context){
//        return (ArrayList<SMSMessageModel>) SMSReader.readSMSFromInbox(context);
//    }
}

