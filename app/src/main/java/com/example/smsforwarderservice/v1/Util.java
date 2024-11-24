package com.example.smsforwarderservice.v1;

import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Util {
    private static final String TAG = GlobalConstants.APP_NAME; // "SMSForwarder";
    private static final String OTP_RECEP_LIST = GlobalConstants.OTP_RECEP_LIST; // "Me,Maa";

    // Extract the message content
    static SmsMessage[] extractSMSContent(Bundle bundle, Context context) {
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] msgs = new SmsMessage[pdus.length];
        for(int i=0; i<pdus.length; i++){
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String msg_from = msgs[i].getOriginatingAddress();
            String msgBody = msgs[i].getMessageBody().replace("\n", " ");
            String result = "From : " + msg_from + " | Content : " + msgBody;
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SMS Content : " + result);
        }
        // Log.d(TAG, "First!");
        // Log.d(TAG, "Sim count : " + isMultiSimEnabled(context));
        // Log.d(TAG, "Last!");
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
            new SFUtil2().execute(sms_msgs);
        }
    }

    static void checkAndForwardAsRequired(Context context, SmsMessage sms) {

        String[] recipients = OTP_RECEP_LIST.split(",");

        for (String recipient : recipients){
            recipient = recipient.trim();
            String recipientNumber = ContactUtil.getRecipientIdFromContactNames(context, recipient);
            Log.d(TAG, recipientNumber + " => " + recipientNumber);

            // Check if forwarding is required by matching `ALLOWED SHORT CODES` from the constant class
            // if yes, then forward
            String fromShortCode = sms.getOriginatingAddress().toUpperCase();
            if(isForwardingAllowed(fromShortCode) && recipientNumber != null) {
                String modifiedContent = generateMessageContent(sms);

                if(recipientNumber != null && modifiedContent != null){
                    // Since we have dual sim phone instead of
                    // default [like SmsManager smsManager = SmsManager.getDefault()],
                    // we will use the subscription ID of the first SIM card and
                    // get the sms manager from there
                    try {
                        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
                        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                        if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
                            // Return the subscription ID of the first SIM card
                            SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionInfoList.get(0).getSubscriptionId());

                            // Send / forward the message with this SMSManager record
                            smsManager.sendTextMessage(
                                    recipientNumber.replace(" ", ""), // replace space in numbers if there is any
                                    null,
                                    modifiedContent,                   // The modified content to send
                                    null,
                                    null
                            );
                        }
                    }
                    catch(SecurityException e){
                        Log.d(TAG, "Error occurred inside checkAndForwardAsRequired => " + e.getMessage());
                    }
                }
            }
            else{
                Log.d(TAG, "Forwarding is not required");
            }
        }
    }

    private static boolean isForwardingAllowed(String fromShortCode) {
        boolean allowed = false;
        if(fromShortCode.contains(GlobalConstants.SHORTCODE_HOTSTAR) || fromShortCode.contains(GlobalConstants.SHORTCODE_HOICHOI) || fromShortCode.contains(GlobalConstants.SHORTCODE_KLIKK)) {
            allowed = true;
        }
        Log.d(TAG, "Inside isForwardingAllowed=> allowed equals => " + allowed);
        return allowed;
    }

    private static String generateMessageContent(SmsMessage sms) {

        String content = sms.getMessageBody().toUpperCase();
        String from = sms.getOriginatingAddress().toUpperCase();

        String appName = "";
        String appCode = "";
        String modifiedContent = "";

        /*
        // # debugging loop below
        for(int i =0; i<content.split(" ").length; i++){
            String each = content.split(" ")[i];
            Log.d(TAG, i + "th value is=>" + each);
        }
        */

        if(from.contains(GlobalConstants.SHORTCODE_HOTSTAR) && content.contains("HOTSTAR VERIFICATION CODE")) {
            appName = "Hotstar";
            appCode = content.split(" ")[1];
        }
        if(from.contains(GlobalConstants.SHORTCODE_HOICHOI) && content.contains("HOICHOI VERIFICATION CODE")) {
            appName = "Hoichoi";
            appCode = content.split(" ")[7];
        }
        if(from.contains(GlobalConstants.SHORTCODE_KLIKK) && content.contains("PHONE NUMBER VERIFICATION IS")) {
            appName = "Klikk";
            appCode = content.split(" ")[9];
        }
        modifiedContent = "OTP for " + appName  + " App  => " + appCode;

        Log.d(TAG, "From is =>" + from);
        Log.d(TAG, "Content is " + content);
        Log.d(TAG, "Modified Content is " + modifiedContent);

        return modifiedContent;

    }

    // For testing
    /*
    public static boolean isMultiSimEnabled(Context context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // Check for API level 22 (Lollipop MR1) or higher, because getPhoneCount() is not available below this API level
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                Log.d(TAG, "Active modem count=>" + telephonyManager.getActiveModemCount());
                Log.d(TAG, "Active sim count=>" + telephonyManager.getPhoneCount());
                return telephonyManager.getPhoneCount() > 1;
            }
        }
        return false;
    }
    */

}

