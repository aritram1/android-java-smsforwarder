package com.example.smsforwarderservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.utils.StopLogic;

public class SMSForwarderService extends Service {

    private boolean isRunning;
    private SMSReceiver smsReceiver;
    private Thread backgroundThread;
    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        this.backgroundThread = new Thread(myTask);
        this.isRunning = false;
        // this.smsReceiver = new SMSReceiver();
        // IntentFilter intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        // this.registerReceiver(this.smsReceiver, intentFilter);

        Toast.makeText(this, "Background Service Created", Toast.LENGTH_SHORT).show();
    }

    private final Runnable myTask = new Runnable() {
        @Override
        public void run() {
            // Toast.makeText(context, "The Bg Service is running!", Toast.LENGTH_SHORT).show();
            System.out.println("Start of the Run method!");
            smsReceiver = new SMSReceiver();
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(smsReceiver, intentFilter);
            int elapsedTime = 0;
            int DELAY = 1000;
            while(true){
             try {
                 Thread.sleep(DELAY);
                 System.out.println("Service is running for " + elapsedTime);
             } catch (InterruptedException e) {
                 throw new RuntimeException(e);
             }
             elapsedTime = elapsedTime + DELAY;
            }
            // System.out.println("Finish of the Run method!");
            // stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // super.onStartCommand(intent, flags, startId);
        if (!this.isRunning) {
            this.isRunning = true;
            Toast.makeText(context, "Background Service Started", Toast.LENGTH_SHORT).show();
            this.backgroundThread.start();
        } else {
            Toast.makeText(context, "Background Service Is Already Running", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY; // Return sticky to keep the service running even if the app goes into the background
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.smsReceiver);
        this.isRunning = false;
        Toast.makeText(context, "Background Service Stopped", Toast.LENGTH_SHORT).show();
    }
}
