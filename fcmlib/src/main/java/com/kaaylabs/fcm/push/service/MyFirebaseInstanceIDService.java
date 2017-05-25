package com.kaaylabs.fcm.push.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kaaylabs.fcm.push.Config;
import com.kaaylabs.fcm.push.util.AppLog;
import com.kaaylabs.fcm.push.util.Constants;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        try {
            sharedPreferences.edit().putBoolean(Constants.SHARED_KEY_HAS_TOKEN, true).apply();
            sharedPreferences.edit().putString(Constants.SHARED_KEY_TOKEN, token).apply();
        } catch (Exception e) {
            AppLog.handleException(TAG, e);
            sharedPreferences.edit().putBoolean(Constants.SHARED_KEY_HAS_TOKEN, false).apply();
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.INTENT_REGISTRATION_COMPLETE));

    }
}

