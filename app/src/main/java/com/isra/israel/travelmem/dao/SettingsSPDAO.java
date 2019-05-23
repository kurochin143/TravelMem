package com.isra.israel.travelmem.dao;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsSPDAO {

    private static final String SP_NAME = "settings";
    private static final String KEY_IS_NOTIFICATION_ENABLED = "is_notification_enabled";

    public static boolean isNotificationEnabled(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(KEY_IS_NOTIFICATION_ENABLED, false);
    }

    public static void setNotificationEnabled(Context context, boolean isEnabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_IS_NOTIFICATION_ENABLED, isEnabled);
        editor.apply();
    }
}
