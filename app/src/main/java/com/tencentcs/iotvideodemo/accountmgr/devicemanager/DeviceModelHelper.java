package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;

public class DeviceModelHelper {
    private static DeviceModelManager sDeviceModelManager = DeviceModelManager.getInstance();

    public static boolean isOnline(String deviceId) {
        return sDeviceModelManager.getIntValue(deviceId, "ProReadonly._online") == 1;
    }

    public static void getLatestVersion(String deviceId, IResultListener<ModelMessage> listener) {
        sDeviceModelManager.setStringValue(deviceId, "Action._otaVersion", "", listener);
    }

    public static String getOTAVersion(String deviceId) {
        return DeviceModelManager.getInstance().getStringValue(deviceId, "ProReadonly._otaVersion");
    }

    public static void startOTA(String deviceId, IResultListener<ModelMessage> listener) {
        sDeviceModelManager.setIntValue(deviceId, "Action._otaUpgrade", 1, listener);
    }

    public static int getOTAProgress(String deviceId) {
        return sDeviceModelManager.getIntValue(deviceId, "ProReadonly._otaUpgrade");
    }
}
