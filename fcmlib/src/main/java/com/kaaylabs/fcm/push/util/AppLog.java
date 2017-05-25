package com.kaaylabs.fcm.push.util;


import android.util.Log;

import com.kaaylabs.fcm.push.BuildConfig;


/**
 * The type App log.
 */
public class AppLog {

    private AppLog() {
    }

    /**
     * The constant TAG.
     */
    public static final String TAG = "Session";
    /**
     * The constant isDebug.
     */
    private static final boolean IS_DEBUG = BuildConfig.DEBUG;
    /**
     * The constant URL.
     */
    private static final String URL = "url";
    /**
     * The constant PARAMS.
     */
    private static final String PARAMS = "params";
    /**
     * The constant RESPONSE.
     */
    private static final String RESPONSE = "response";

    /**
     * Log.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void Log(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message + "");
        }
    }

    /**
     * Log url.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void LogURL(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, tag + " \n " + URL + "\n" + message + "");
        }
    }

    /**
     * Log parameters.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void LogParameters(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, tag + " \n " + PARAMS + "\n" + message + "");
        }
    }

    /**
     * Log response.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void LogResponse(String tag, String message) {
        if (BuildConfig.DEBUG) {
                Log.d(TAG, tag + " \n " + RESPONSE + "\n" + message + "");
        }
    }

    /**
     * Handle exception.
     *
     * @param tag the tag
     * @param e   the e
     */
    public static void handleException(String tag, Exception e) {
        if (IS_DEBUG && e != null) {
            Log.d(tag, e.getMessage() + "");
        }
    }

}
