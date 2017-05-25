package com.kaaylabs.fcm.push.interfaces;

import com.google.firebase.messaging.RemoteMessage;

public interface FcmListener {

    void onDeviceRegistered(String deviceToken);

    void onMessage(RemoteMessage remoteMessage);

    void onFcmMessage(String message);
}
