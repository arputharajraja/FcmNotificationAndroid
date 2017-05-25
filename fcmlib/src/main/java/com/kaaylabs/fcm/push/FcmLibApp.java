package com.kaaylabs.fcm.push;

import android.app.Application;


public class FcmLibApp extends Application {
    private static FcmLibApp sInstance;

    public FcmLibApp() {
        super();
        sInstance = this;
    }
    public static FcmLibApp getInstance() {
        return sInstance;
    }

}
