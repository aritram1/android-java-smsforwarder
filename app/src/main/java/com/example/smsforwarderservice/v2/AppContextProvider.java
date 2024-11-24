package com.example.smsforwarderservice.v2;

import android.app.Application;
import android.content.Context;

public class AppContextProvider extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContextProvider.context = getApplicationContext();
    }

    public static Context getContext() {
        return AppContextProvider.context;
    }
}
