package com.huhaoyu.tutu.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preferences utilities
 * Created by coderhuhy on 15/12/13.
 */
public class PreferencesUtils {

    private static final String LogTag = PreferencesUtils.class.getCanonicalName();
    private static final String TUTU_PREFERENCES = "TUTU_PREFERENCES_KEY";

    private static SharedPreferences preferences;
    private static boolean autoNotify;

    enum TutuPreferenceKey {
        AutoNotification("TUTU_AUTO_NOTIFICATION");
        String key;

        TutuPreferenceKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private PreferencesUtils() {

    }

    public static void init(Context context) {
        preferences = context.getSharedPreferences(TUTU_PREFERENCES, Context.MODE_PRIVATE);
        autoNotify = preferences.getBoolean(TutuPreferenceKey.AutoNotification.getKey(), true);
    }

    public static boolean getAutoNotification() {
        return autoNotify;
    }

    public static void saveAutoNotificaiton(boolean notify) {
        autoNotify = notify;
        preferences.edit().putBoolean(TutuPreferenceKey.AutoNotification.getKey(), notify).apply();
    }

}
