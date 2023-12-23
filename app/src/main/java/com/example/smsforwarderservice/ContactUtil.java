package com.example.smsforwarderservice;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactUtil {

    private static final String TAG = "SMSForwarder";

    public static String getRecipientIdFromContactNames(Context context, String contactName) {
        // Replace these constants with the actual column names for name and phone number in your Contacts table
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Log.d(TAG, "Hello from getRecipientIdFromContactNames method");
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{contactName};

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        try {
            if (cursor != null && cursor.moveToFirst()) {
                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                // Check if the column index is valid (not -1)
                if (numberColumnIndex != -1) {
                    return cursor.getString(numberColumnIndex);
                } else {
                    // Handle the case where the column is not found
                    Log.d(TAG, "I state that Column is not found");
                    return "Column not found";
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Return null if no matching contact is found
        return null;
    }
}
