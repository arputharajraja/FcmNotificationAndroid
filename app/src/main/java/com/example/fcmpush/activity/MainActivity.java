package com.example.fcmpush.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fcmpush.R;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.kaaylabs.fcm.push.Config;
import com.kaaylabs.fcm.push.FcmManager;
import com.kaaylabs.fcm.push.interfaces.FcmListener;
import com.kaaylabs.fcm.push.util.Constants;
import com.kaaylabs.fcm.push.util.NotificationUtils;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements FcmListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtRegId;
    private TextView txtMessage;
    private NotificationUtils notificationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);

    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFireBaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString(Constants.SHARED_KEY_TOKEN, null);

        Log.e(TAG, "FCM reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            String regIdToken = getString(R.string.text_fcm) + regId;
            txtRegId.setText(regIdToken);
        } else
            txtRegId.setText(getString(R.string.text_fcm_reg_error));
    }


    @Override
    public void onDeviceRegistered(String deviceToken) {
        Log.e(TAG, "FCM device token: " + deviceToken);
        FcmManager.getInstance(this).subscribeTopic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FcmManager.getInstance(this).registerListener(this);

        displayFireBaseRegId();
    }

    @Override
    public void onMessage(RemoteMessage remoteMessage) {
        Log.e(TAG, " Activity From: " + remoteMessage.getFrom());
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Activity Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData());

            try {
                Gson gson = new Gson();
                JSONObject json = new JSONObject(gson.toJson(remoteMessage.getData()));

                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }

    @Override
    public void onFcmMessage(String message) {
        Toast.makeText(getApplicationContext(), "Push notification: " + message,
                Toast.LENGTH_LONG).show();

        txtMessage.setText(message);
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra(FcmMessageConstants.MESSAGE, message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            String title = json.getString(FcmMessageConstants.NOTIFICATION_TITLE);
            String message = json.getString(FcmMessageConstants.MESSAGE);
            String imageUrl = json.getString(FcmMessageConstants.NOTIFICATION_IMAGE);
            String timestamp = String.valueOf(json.getLong("timestamp"));

            Log.e(TAG, FcmMessageConstants.NOTIFICATION_TITLE + " : " + title);
            Log.e(TAG, FcmMessageConstants.MESSAGE + " : " + message);
            Log.e(TAG, FcmMessageConstants.NOTIFICATION_IMAGE + ": " + imageUrl);
            Log.e(TAG, FcmMessageConstants.NOTIFICATION_TIME + ": " + timestamp);


            if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra(FcmMessageConstants.MESSAGE, message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra(FcmMessageConstants.MESSAGE, message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message,
                            timestamp, resultIntent, R.mipmap.ic_launcher);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message,
                            timestamp, resultIntent, imageUrl, R.mipmap.ic_launcher);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    public void showNotificationMessage(Context context, String title, String message,
                                        String timeStamp, Intent intent, int notificationIcon) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent,
                notificationIcon);
    }

    /**
     * Showing notification with text and image
     */
    public void showNotificationMessageWithBigImage(Context context, String title, String message,
                                                    String timeStamp, Intent intent,
                                                    String imageUrl, int notificationIcon) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl,
                notificationIcon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FcmManager.getInstance(this).unRegisterListener();
    }
}
