package com.example.smsforwarderservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Util {
    private static final String TAG = "SMSForwarder";
    private static final String additionalRecipients = "Me,Maa";

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

                    // First forward to recipients as required
                    forwardToOtherRecipients(context, msgs[0]);
                    Log.d(TAG, "Send To Recipients is finished");

                    // Then send to salesforce as applicable, this comes as second step
                    // since some OTP sms content produces error in SF
                    if(msgs[0].getMessageBody().contains("OTP")
                        ||  msgs[0].getMessageBody().contains("VERIFICATION CODE")
                        ||  msgs[0].getOriginatingAddress().contains("+"))
                    {
                        // No need to consider this sms
                    }
                    else{
                        sendToSalesforce(msgs);
                        Log.d(TAG, "Send To Salesforce is finished");
                    }

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

        String[] recipients = additionalRecipients.split(",");
        SmsManager smsManager = SmsManager.getDefault();

        for (String recipient : recipients){
            String recipientNumber = ContactUtil.getRecipientIdFromContactNames(context, recipient);
            Log.d(TAG, recipient + " => " + recipientNumber);

            String content = sms.getMessageBody().toUpperCase();
            String modifiedContent = null;
            if(content.contains("HOTSTAR VERIFICATION CODE")) {
                modifiedContent = "OTP for Hotstar App => " + content.split(" ")[1];
            }
            if(content.contains("HOICHOI VERIFICATION CODE")) {
                modifiedContent = "OTP for Hoichoi App  => " + content.split(" ")[6];
            }
            if(sms.getOriginatingAddress() == "VK-KLIKKK" && content.contains("PHONE NUMBER VERIFICATION IS")) {
                modifiedContent = "OTP for Klikk App  => " + content.split(" ")[9];
            }
            if(recipientNumber != null && modifiedContent != null){
                smsManager.sendTextMessage(
                        recipientNumber.replace(" ", ""), // replace space in numbers if there is any
                        null,
                        modifiedContent,                   // The modified content to send
                        null,
                        null
                );
            }
        }
    }

}

