package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;

public class DeviceModelHelper {
    private static DeviceModelManager sDeviceModelManager = DeviceModelManager.getInstance();

    public static boolean isOnline(String deviceId) {
        String path1 = "ProReadonly._online.ctlVal";
        String path2 = "ProReadonly._online.stVal";
        JsonElement value = sDeviceModelManager.getJsonElement(deviceId, path1);
        if (value == null) {
            value = sDeviceModelManager.getJsonElement(deviceId, path2);
        }
        if (value instanceof JsonPrimitive) {
            return value.getAsInt() == 1;
        }
        return false;
    }

    public static void updateLatestVersion(String deviceId, IResultListener<ModelMessage> listener) {
        String path = "Action._otaVersion";
        JsonElement jsonElement = sDeviceModelManager.getJsonElement(deviceId, path);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("ctlVal")) {
                jsonObject.addProperty("ctlVal", "");
                sDeviceModelManager.setJsonElement(deviceId, path, jsonObject, listener);
            } else if (jsonObject.has("stVal")) {
                jsonObject.addProperty("stVal", "");
                sDeviceModelManager.setJsonElement(deviceId, path, jsonObject, listener);
            }
        }
    }

    public static void startOTA(String deviceId, IResultListener<ModelMessage> listener) {
        String path = "Action._otaUpgrade";
        JsonElement jsonElement = sDeviceModelManager.getJsonElement(deviceId, path);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("ctlVal")) {
                jsonObject.addProperty("ctlVal", 1);
                sDeviceModelManager.setJsonElement(deviceId, path, jsonObject, listener);
            } else if (jsonObject.has("stVal")) {
                jsonObject.addProperty("stVal", 1);
                sDeviceModelManager.setJsonElement(deviceId, path, jsonObject, listener);
            }
        }
    }
}
