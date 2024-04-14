package com.example.smsforwarderservice;

import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class Util {
    private static final String TAG = GlobalConstants.APP_NAME; // "SMSForwarder";
    private static final String OTP_RECEP_LIST = GlobalConstants.OTP_RECEP_LIST; // "Me, Maa";

    // Extract the message content
    static SmsMessage[] extractSMSContent(Bundle bundle, Context context) {
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] msgs = new SmsMessage[pdus.length];
        for(int i=0; i<pdus.length; i++){
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String msg_from = msgs[i].getOriginatingAddress();
            String msgBody = msgs[i].getMessageBody();
            String result = "From : " + msg_from + "Content : " + msgBody;
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SMS Content : " + result);
        }
        return msgs;
    }

    static void sendToSalesforce(SmsMessage[] msgs) {
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

    static void forwardToOtherRecipients(Context context, SmsMessage sms) {

        String[] recipients = OTP_RECEP_LIST.split(",");
        SmsManager smsManager = SmsManager.getDefault();

        for (String recipient : recipients){
            recipient = recipient.trim();
            String recipientNumber = ContactUtil.getRecipientIdFromContactNames(context, recipient);
            Log.d(TAG, recipient + " => " + recipientNumber);

            String content = sms.getMessageBody().toUpperCase();
            String modifiedContent = null;
            String appName = "";
            String appCode = "";
            String from = sms.getOriginatingAddress();
            for(int i =0; i<content.split(" ").length; i++){
                String each = content.split(" ")[i];
                Log.d(TAG, i + "th value is=>" + each);
            }
            if(from.contains("HOTSTAR") && content.contains("HOTSTAR VERIFICATION CODE")) {
                appName = "Hotstar";
                appCode = content.split(" ")[1];
            }
            if(from.contains("HOICHOI") && content.contains("HOICHOI VERIFICATION CODE")) {
                appName = "Hoichoi";
                appCode = content.split(" ")[6];
            }
            if(from.contains("KLIKK") && content.contains("PHONE NUMBER VERIFICATION IS")) {
                appName = "Klikk";
                appCode = content.split(" ")[9];
            }
            modifiedContent = "OTP for " + appName  + " App  => " + appCode;
            Log.d(TAG, "sms.getOriginatingAddress() is =>" + sms.getOriginatingAddress() + "<=");
            // Log.d(TAG, "Content is " + content);
            // Log.d(TAG, "Modified Content is " + modifiedContent);
//            if(recipientNumber != null && modifiedContent != null){
//                smsManager.sendTextMessage(
//                        recipientNumber.replace(" ", ""), // replace space in numbers if there is any
//                        null,
//                        modifiedContent,                   // The modified content to send
//                        null,
//                        null
//                );
//            }
        }
    }

}

