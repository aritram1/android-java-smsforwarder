package com.example.smsforwarderservice.v2.helper;

import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.smsforwarderservice.v2.model.SMSMessageModel;

import java.util.ArrayList;
import java.util.List;

public class Util {
    static final String TAG = GlobalConstants.APP_NAME;

    // Extract SMS messages from a Bundle
    public static SmsMessage[] extractSMSContent(Bundle bundle) {
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) {
            Log.e(TAG, "No PDUs found in the SMS bundle.");
            return null;
        }

        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        return messages;
    }

    // Check and forward SMS to predefined recipients based on conditions
    public static void checkAndForwardAsRequired(Context context, SmsMessage sms) {
        String[] recipients = GlobalConstants.OTP_RECEP_LIST.split(",");

        for (String recipient : recipients) {
            recipient = recipient.trim();
            String recipientNumber = getRecipientIdFromContactNames(context, recipient);

            if (recipientNumber != null && isForwardingAllowed(sms.getOriginatingAddress())) {
                String modifiedContent = generateMessageContent(sms);
                if (modifiedContent != null) {
                    sendSMS(context, recipientNumber, modifiedContent);
                }
            } else {
                Log.d(TAG, "Forwarding is not required or recipient not found.");
            }
        }
    }

    // Send SMS to a given recipient
    private static void sendSMS(Context context, String recipientNumber, String content) {
        try {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
            List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
                SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionInfoList.get(0).getSubscriptionId());
                smsManager.sendTextMessage(recipientNumber.replace(" ", ""), null, content, null, null);
                Log.d(TAG, "SMS sent to " + recipientNumber + ": " + content);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Permission denied for sending SMS: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error sending SMS: " + e.getMessage());
        }
    }

    // Send SMS messages to Salesforce for storage
    public static void sendToSalesforce(SmsMessage[] msgs) {
        if (msgs != null) {
            ArrayList<SMSMessageModel> smsMessages = new ArrayList<>();
            for (SmsMessage sms : msgs) {
                SMSMessageModel model = new SMSMessageModel();
                model.content = sms.getMessageBody();
                model.receivedAt = String.valueOf(sms.getTimestampMillis());
                model.sender = sms.getOriginatingAddress();
                smsMessages.add(model);
            }
            new SFUtil().sendMessagesToSalesforce(smsMessages);
        }
    }

    // Determine if forwarding is allowed based on sender's short code
    private static boolean isForwardingAllowed(String fromShortCode) {
        if (fromShortCode == null) return false;
        return fromShortCode.toUpperCase().contains(GlobalConstants.SHORTCODE_HOTSTAR)
                || fromShortCode.toUpperCase().contains(GlobalConstants.SHORTCODE_HOICHOI)
                || fromShortCode.toUpperCase().contains(GlobalConstants.SHORTCODE_KLIKK);
    }

    // Generate a custom message for forwarding
    private static String generateMessageContent(SmsMessage sms) {
        String content = sms.getMessageBody().toUpperCase();
        String from = sms.getOriginatingAddress().toUpperCase();

        String appName = "";
        String appCode = "";

        if (from.contains(GlobalConstants.SHORTCODE_HOTSTAR) && content.contains("HOTSTAR VERIFICATION CODE")) {
            appName = "Hotstar";
            appCode = content.split(" ")[1];
        } else if (from.contains(GlobalConstants.SHORTCODE_HOICHOI) && content.contains("HOICHOI VERIFICATION CODE")) {
            appName = "Hoichoi";
            appCode = content.split(" ")[7];
        } else if (from.contains(GlobalConstants.SHORTCODE_KLIKK) && content.contains("PHONE NUMBER VERIFICATION IS")) {
            appName = "Klikk";
            appCode = content.split(" ")[9];
        } else {
            Log.d(TAG, "No recognizable app or OTP code found in the SMS.");
            return null;
        }

        return "OTP for " + appName + " App => " + appCode;
    }

    public static String getRecipientIdFromContactNames(Context context, String contactName) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?";
        String[] selectionArgs = {contactName};

        try (Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                if (numberColumnIndex != -1) {
                    return cursor.getString(numberColumnIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching contact by name: " + e.getMessage());
        }

        return null;
    }
}
