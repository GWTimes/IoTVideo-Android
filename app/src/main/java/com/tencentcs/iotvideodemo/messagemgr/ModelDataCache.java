package com.tencentcs.iotvideodemo.messagemgr;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencentcs.iotvideo.utils.LogUtils;

public class ModelDataCache {
    private final static String TAG = "ModelDataCache";
    private String allModelData;
    JsonObject jsonObject = null;

    private static class Instance{
        private static ModelDataCache INSTANCE = new ModelDataCache();
    }

    private ModelDataCache() {

    }

    public static ModelDataCache getInstance() {
        return Instance.INSTANCE;
    }

    public void updateData(String jsonData) {
        LogUtils.i(TAG,"updateData:" + jsonData);
        allModelData = jsonData;
        JsonParser parser = new JsonParser();
        jsonObject = parser.parse(allModelData).getAsJsonObject();
    }

    public JsonObject getDeviceVersionInfo() {
        if (null == jsonObject) {
            return null;
        }
        return jsonObject.getAsJsonObject("ProConst").getAsJsonObject("_versionInfo");
    }
}
