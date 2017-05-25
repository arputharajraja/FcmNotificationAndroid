package com.kaaylabs.fcm.push.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kaaylabs.fcm.push.Config;
import com.kaaylabs.fcm.push.util.AppLog;
import com.kaaylabs.fcm.push.util.Constants;


public class RegistrationService extends IntentService {

    private static String tag = RegistrationService.class.getSimpleName();

    /**
     * Constructor
     */
    public RegistrationService() {
        super(tag);
    }

    /**
     * This Intent is responsible for get token from FCM server and send
     * broadcast. After we get token, we save this token to shared preferences.
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);

        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            sharedPreferences.edit().putBoolean(Constants.SHARED_KEY_HAS_TOKEN, true).apply();
            sharedPreferences.edit().putString(Constants.SHARED_KEY_TOKEN, refreshedToken).apply();

        } catch (Exception e) {
            AppLog.handleException(tag, e);
            sharedPreferences.edit().putBoolean(Constants.SHARED_KEY_HAS_TOKEN, false).apply();
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.INTENT_REGISTRATION_COMPLETE));
    }
}
