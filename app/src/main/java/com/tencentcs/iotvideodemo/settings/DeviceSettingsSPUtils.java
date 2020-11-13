package com.tencentcs.iotvideodemo.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceSettingsSPUtils {
    private static final String SP_FILE_TEMP = "com.tencentcs.iotvideodemo_preferences";

    public static final String supportAudioTalk = "supportAudioTalk";
    public static final String supportVideoTalk = "supportVideoTalk";
    public static final String media_decode_audio = "media_decode_audio";
    public static final String media_decode_video = "media_decode_video";
    public static final String media_encode_audio = "media_encode_audio";
    public static final String default_definition = "default_definition";
    public static final String default_sourceId = "default_sourceId";

    private static class SPHolder {
        private static final DeviceSettingsSPUtils INSTANCE = new DeviceSettingsSPUtils();
    }

    public static DeviceSettingsSPUtils getInstance() {
        return SPHolder.INSTANCE;
    }

    public boolean supportAudioTalk(Context context) {
        return getBoolean(context, supportAudioTalk, false);
    }

    public boolean supportVideoTalk(Context context) {
        return getBoolean(context, supportVideoTalk, false);
    }

    public boolean media_decode_audio(Context context) {
        return getBoolean(context, media_decode_audio, false);
    }

    public boolean media_encode_audio(Context context) {
        return getBoolean(context, media_encode_audio, false);
    }

    public boolean media_decode_video(Context context) {
        return getBoolean(context, media_decode_video, false);
    }

    public int default_definition(Context context) {
        String definition =  getString(context, default_definition, "2");
        int result = 0;
        try {
            result = Integer.parseInt(definition);
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    public short default_sourceId(Context context) {
        String sourceId = getString(context, default_sourceId, "0");
        short result = 0;
        try {
            result = Short.parseShort(sourceId);
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getString(Context context, String key, String defaultValue) {
        SharedPreferences sf = context.getSharedPreferences(SP_FILE_TEMP, Context.MODE_PRIVATE);
        return sf.getString(key, defaultValue);
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
}
