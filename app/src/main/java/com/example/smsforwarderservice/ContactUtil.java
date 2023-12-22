package com.example.smsforwarderservice;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactUtil {

    public static String getRecipientIdFromContactNames(Context context, String contactName) {
        // Replace these constants with the actual column names for name and phone number in your Contacts table
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

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
                return cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
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
