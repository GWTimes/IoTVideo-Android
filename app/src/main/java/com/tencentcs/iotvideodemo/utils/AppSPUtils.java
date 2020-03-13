package com.tencentcs.iotvideodemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSPUtils {
    private static final String SP_FILE_PERMANENT = "iotvideo_permanent";

    public static final String UNIQUE_ID = "UNIQUE_ID";
    public static final String SERVER_TYPE = "SERVER_TYPE";
    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String NEED_SWITCH_SERVER_TYPE = "NEED_SWITCH_SERVER_TYPE";

    private static class SPHolder {
        private static final AppSPUtils INSTANCE = new AppSPUtils();
    }

    public static AppSPUtils getInstance() {
        return AppSPUtils.SPHolder.INSTANCE;
    }

    public void putString(Context context, String key, String value) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_PERMANENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(Context context, String key, String defaultValue) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_PERMANENT, Context.MODE_PRIVATE);
        return sf.getString(key, defaultValue);
    }

    public void putInteger(Context context, String key, int value) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_PERMANENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInteger(Context context, String key, int defaultValue) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_PERMANENT, Context.MODE_PRIVATE);
        return sf.getInt(key, defaultValue);
    }

    public void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_PERMANENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_PERMANENT, Context.MODE_PRIVATE);
        return sf.getBoolean(key, defaultValue);
    }
}
