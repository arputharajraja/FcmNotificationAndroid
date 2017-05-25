package com.kaaylabs.fcm.push.interfaces;

public interface IFcmManager {

    /**
     * Subscribe topic. Topics are like channel.
     * Every device which subscribe to channel
     * get notification.
     */
    void subscribeTopic();

    /**
     * Un subscribe from subscribed topic.
     */
    void unSubscribeTopic();


    void registerListener(FcmListener gcmListener);

    void unRegisterListener();
}
