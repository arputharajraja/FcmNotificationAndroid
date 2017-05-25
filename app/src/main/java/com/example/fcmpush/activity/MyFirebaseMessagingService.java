package com.example.fcmpush.activity;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kaaylabs.fcm.push.FcmManager;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        FcmManager.getInstance(this).onMessage(remoteMessage);
    }
}
