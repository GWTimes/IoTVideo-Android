package com.tencentcs.iotvideodemo.accountmgr;

import android.content.Context;
import android.content.SharedPreferences;

public class AccountSPUtils {
    private static final String SP_FILE_TEMP = "iotvideo_temp";

    public static final String SECRET_ID = "SECRET_ID";
    public static final String SECRET_KEY = "SECRET_KEY";
    public static final String TOKEN = "TOKEN";
    public static final String ACCESS_ID = "ACCESS_ID";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String VALIDITY_TIMESTAMP = "VALIDITY_TIMESTAMP";
    public static final String ANONYMOUS_TID = "TID";
    public static final String ANONYMOUS_USER = "ANONYMOUS_USER";

    private static class SPHolder {
        private static final AccountSPUtils INSTANCE = new AccountSPUtils();
    }

    public static AccountSPUtils getInstance() {
        return SPHolder.INSTANCE;
    }

    public void putString(Context context, String key, String value) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_TEMP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(Context context, String key, String defaultValue) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_TEMP, Context.MODE_PRIVATE);
        return sf.getString(key, defaultValue);
    }

    public void putInteger(Context context, String key, int value) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_TEMP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInteger(Context context, String key, int defaultValue) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_TEMP, Context.MODE_PRIVATE);
        return sf.getInt(key, defaultValue);
    }

    public void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_TEMP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_TEMP, Context.MODE_PRIVATE);
        return sf.getBoolean(key, defaultValue);
    }

    public void clear(Context context) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_TEMP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isLogin(Context context) {
        int validityTime = getInteger(context, VALIDITY_TIMESTAMP, 0);
        boolean isLogin;
        isLogin = validityTime > (System.currentTimeMillis() / 1000);
        return isLogin;
    }

    public String getUserId(Context context) {
        String userId;
        userId = getString(context, ACCESS_ID, "");
        return userId;
    }

    public boolean isAnonymousUser(Context context){
        return getBoolean(context, ANONYMOUS_USER, false);
    }

    public String getTid(Context context) {
        return getString(context, ANONYMOUS_TID, "");
    }
}
