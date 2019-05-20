package com.isra.israel.travelmem.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public class FirebaseSessionSPDAO {

    private static final String SP_NAME = "firebase_session";
    private static final String KEY_UID = "uid";
    private static final String KEY_ID_TOKEN = "id_token";

    @Nullable
    public static String getUid(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(KEY_UID, null);
    }

    public static void setUid(Context context, String Uid) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_UID, Uid);
        editor.apply();
    }

    @Nullable
    public static String getIdToken(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(KEY_ID_TOKEN, null);
    }

    public static void setIdToken(Context context, String idToken) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ID_TOKEN, idToken);
        editor.apply();
    }
}
