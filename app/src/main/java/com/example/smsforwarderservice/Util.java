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

        String pupu = ContactUtil.getRecipientIdFromContactNames(context, "pupu");
        String chotoBhai = ContactUtil.getRecipientIdFromContactNames(context, "bhai aritra");
        String munnuMa = ContactUtil.getRecipientIdFromContactNames(context, "Munnu ma");
        Log.d("pupu =>", pupu);
        Log.d("chotoBhai =>", chotoBhai);
        Log.d("munnuMa =>", munnuMa);

//        if (recipientId != null) {
//            // The recipientId variable now contains the phone number corresponding to the contact name "John Doe"
//        } else {
//            // Handle the case where the contact name is not found
//        }
//
//        SmsManager smsManager = SmsManager.getDefault();
//        String recipient1 = "+919903711090";
//        String recipient2 = "+918420922326";
//        String recipient3 = "+917596952864";
//        String content = sms.getMessageBody().toUpperCase();
//        if(content.contains("HOTSTAR VERIFICATION CODE")) {
//            String modifiedContent = "HOTSTAR => " + content.split(" ")[1];
//            smsManager.sendTextMessage(recipient1, null, modifiedContent, null, null);
//            smsManager.sendTextMessage(recipient2, null, modifiedContent, null, null);
//            smsManager.sendTextMessage(recipient3, null, modifiedContent, null, null);
//        }
//        else if(content.contains("HOICHOI VERIFICATION CODE")) {
//            String modifiedContent = "HOICHOI => " + content.split(" ")[6];
//            smsManager.sendTextMessage(recipient1, null, modifiedContent, null, null);
//            smsManager.sendTextMessage(recipient2, null, modifiedContent, null, null);
//            smsManager.sendTextMessage(recipient3, null, modifiedContent, null, null);
//        }
    }



    ////////////////////////////////
//    SmsManager smsManager = SmsManager.getDefault();
//            Log.d(TAG, "Default sms manager =>" + smsManager.toString());
//            Log.d(TAG, "I am in forwarding content =>");
//    String recipient = "+919903711090";
//    // if(matchingTexts.contains(sms.getMessageBody().toUpperCase())){
//    SMSMessageModel sms1 = msgs.get(0);
//            if(sms1.content.toUpperCase().contains("HOTSTAR")){
//        // Log.d(TAG, "matchingTexts=>" + matchingTexts);
//        Log.d(TAG, "For forwarding content =>" + sms1.content);
//        Log.d(TAG, "recipient=>" + recipient);
//        String sms_content = "Hey Pal"; //sms1.content.toUpperCase().replace("VERIFICATION", "");
//        smsManager.sendTextMessage(recipient, null, sms_content, null, null);
//        smsManager.sendTextMessage("+918420922326", null, sms_content, null, null);
//        smsManager.sendTextMessage("+917596952864", null, sms_content, null, null);
//        Log.d(TAG, "After sending=>" + recipient);
//    }
    //////////////////////////////

//    static ArrayList<SMSMessageModel> getAllSMS(Context context){
//        return (ArrayList<SMSMessageModel>) SMSReader.readSMSFromInbox(context);
//    }
}

