package com.example.kishore.instagramlogin;

import android.app.Application;




public class MyApplication extends Application {
    private static MyApplication mInstance;
    private SharedPrefManager sharedPrefManager;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public SharedPrefManager getSharedPrefManager() {
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(this);
        return sharedPrefManager;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


}