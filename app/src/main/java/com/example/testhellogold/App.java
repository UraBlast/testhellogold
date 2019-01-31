package com.example.testhellogold;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static App instance;
    private static Context mContext;

    public static App getInstance() {
        return instance;
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
