package com.example.smsforwarderservice;
class _SendSMSTask{

}

//import android.os.AsyncTask;
//import android.util.Log;
//import java.util.ArrayList;
//
//class SendSMSTask extends AsyncTask<Void, Void, Void> {
//
//    private static final String TAG = "SMSForwarder";
//
//    @Override
//    protected Void doInBackground(ArrayList<SMSMessageModel>... params) {
//        ArrayList<SMSMessageModel> allSMS = Util.getAllSMS();
//        if (allSMS.size() > 0) {
//            // Send List of SMS
//            SFUtil sfutil = new SFUtil();
//            if (sfutil.sf_response == null || sfutil.sf_response.accessToken == null) sfutil.loginToSalesforce();
//            try {
//                sfutil.saveToSalesforce(allSMS);
//            } catch (Exception e) {
//                Log.e(TAG, "Error occurred while saving all sms to SF", e);
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        // Update UI or perform any post-execution tasks if needed
//        // For example, you can update a TextView here
//        TextView tv = findViewById(R.id.infoTextViewId);
//        tv.setText(String.valueOf(allSMS.size()));
//    }
//
//
//}
