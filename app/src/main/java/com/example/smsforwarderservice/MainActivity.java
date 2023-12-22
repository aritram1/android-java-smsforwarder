package com.example.smsforwarderservice;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SMSForwarder";
    private Context context;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, "Inside OnCreate method in main Activity");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        this.context = this;
//        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1000);
//        }
//        if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_SMS}, 1001);
//        }
//        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1002);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "Inside onRequest Permission Result");
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1000) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Granted!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Denied!", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//        if (requestCode == 1001) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Granted!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Denied!", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//        if (requestCode == 1002) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Granted!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Denied!", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }
}