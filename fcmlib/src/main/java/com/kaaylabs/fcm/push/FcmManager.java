package com.kaaylabs.fcm.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.kaaylabs.fcm.push.interfaces.FcmListener;
import com.kaaylabs.fcm.push.interfaces.IFcmManager;
import com.kaaylabs.fcm.push.service.RegistrationService;
import com.kaaylabs.fcm.push.util.Constants;
import com.kaaylabs.fcm.push.util.NotificationUtils;

public class FcmManager implements IFcmManager{
    /**
     * Class instance
     */
    private static FcmManager instance = null;

    /**
     * Context
     */
    private Context mContext;

    /**
     * Shared preferences saves token and isRegistered etc.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * FCM Listener (OnRegistered, onMessage)
     */
    private FcmListener mFcmListener;

    /**
     * Private constructor and call init()
     * @param mContext
     */
    private FcmManager(Context mContext) {
        this.mContext = mContext;
        init();
    }

    /**
     * Singleton instance method
     * @param context
     * @return
     */
    public static FcmManager getInstance(Context context){
        if(instance == null)
            instance = new FcmManager(context);
        return instance;
    }

    /**
     * Initializes shared preferences, checks google play services if available,
     * and register device to FCM server.
     */
    public void init() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, new IntentFilter(Constants.INTENT_REGISTRATION_COMPLETE));
        mContext.startService(new Intent(mContext, RegistrationService.class));
    }

    /**
     * Subscribe to topic
     */
    @Override
    public void subscribeTopic() {
        if(getToken() != null)
            FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
    }

    /**
     * Unsucbsribe from topic
     */
    @Override
    public void unSubscribeTopic() {
        if(getToken() != null)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Config.TOPIC_GLOBAL);
    }


    /**
     * Register listener
     * @param mFcmListener
     */
    @Override
    public void registerListener(FcmListener mFcmListener) {
        this.mFcmListener = mFcmListener;

        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(mContext);
    }

    /**
     * Unregister listener. No longer need to notify.
     */
    @Override
    public void unRegisterListener() {
        mFcmListener = null;
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
    }

    /**
     * Called by service when message received. Notify Listener
     * if it s not null.
     */
    public void onMessage(RemoteMessage remoteMessage) {
        if(mFcmListener != null)
            mFcmListener.onMessage(remoteMessage);
    }

    /**
     * get Token from shared preferences. Noo need to go FCM server every time
     * we need token.
     * @return
     */
    private String getToken() {
        boolean hasToken = mSharedPreferences.getBoolean(Constants.SHARED_KEY_HAS_TOKEN, false);
        if(hasToken)
            return mSharedPreferences.getString(Constants.SHARED_KEY_TOKEN,"");
        else
            return null;
    }

    /**
     * Message receiver onReceive method called when registration ID(Token) is
     * available
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean hasToken = mSharedPreferences.getBoolean(Constants.SHARED_KEY_HAS_TOKEN, false);

            if(hasToken && mFcmListener != null){
                String token = mSharedPreferences.getString(Constants.SHARED_KEY_TOKEN,"");
                mFcmListener.onDeviceRegistered(token);
            }
             if (mFcmListener != null ) {
                 // checking for type intent filter
                 if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                     // gcm successfully registered
                     // now subscribe to `global` topic to receive app wide notifications
                     FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                 } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                     // new push notification is received
                     mFcmListener.onFcmMessage(intent.getStringExtra("message"));
                 }
             }
        }
    };
}
