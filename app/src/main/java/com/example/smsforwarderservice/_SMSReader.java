package com.example.smsforwarderservice;
public class _SMSReader {

}

//import android.content.ContentResolver;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import java.util.ArrayList;
//
//public class SMSReader {
//
//    public static ArrayList<SMSMessageModel> readSMSFromInbox(Context context) {
//
//        // URI for SMS content provider
//        Uri inboxUri = Uri.parse("content://sms/inbox");
//
//        // Columns to retrieve
//        String[] projection = new String[]{"_id", "address", "body", "date"};
//
//        // List to store SMSMessageModel objects
//        ArrayList<SMSMessageModel> smsMessages = new ArrayList<>();
//
//        // Perform the query
//        ContentResolver contentResolver = context.getContentResolver();
//        Cursor cursor = contentResolver.query(inboxUri, projection, null, null, "date desc");
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                // Extract SMS details
//                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
//                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
//                long receivedAt = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
//
//                // Create an SMSMessageModel object
//                SMSMessageModel smsMessage = new SMSMessageModel();
//                smsMessage.content = body;
//                smsMessage.receivedAt = String.valueOf(receivedAt);
//                smsMessage.sender = address;
//
//                // Add the SMSMessageModel object to the list
//                smsMessages.add(smsMessage);
//
//            } while (cursor.moveToNext());
//
//            cursor.close();
//        }
//
//        return smsMessages;
//    }
//}
