package com.tencentcs.iotvideodemo.accountmgr.devicemanager;


import android.text.TextUtils;

import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.IModelListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceModelManager implements IModelListener {
    private static final String TAG = "DeviceModelManager";

    private ConcurrentHashMap<String, DeviceModel> mDeviceModelMap = new ConcurrentHashMap<String, DeviceModel>();

    private static class DeviceModelManagerHolder {
        private static final DeviceModelManager INSTANCE = new DeviceModelManager();
    }

    public static DeviceModelManager getInstance() {
        return DeviceModelManagerHolder.INSTANCE;
    }

    public DeviceModel getDeviceModel(String deviceId) {
        return mDeviceModelMap.get(deviceId);
    }

    public void setDeviceModel(DeviceModel newModel) {
        DeviceModel model = mDeviceModelMap.get(newModel.deviceId);
        if (model == null || model.model == null || "{}".equals(model.model.toString())) {
            LogUtils.i(TAG, "setDeviceModel " + newModel.toString());
            mDeviceModelMap.put(newModel.deviceId, newModel);
        }
    }

    public boolean isOnline(String deviceId) {
        return getIntValue(deviceId, "ProReadonly._online") == 1;
    }

    public void getLatestVersion(String deviceId, IResultListener<ModelMessage> listener) {
        setStringValue(deviceId, "Action._otaVersion", "", listener);
    }

    public void startOTA(String deviceId, IResultListener<ModelMessage> listener) {
        setIntValue(deviceId, "Action._otaUpgrade", 1, listener);
    }

    public int getOTAProgress(String deviceId) {
        return getIntValue(deviceId, "ProReadonly._otaUpgrade");
    }

    public String getStringValue(String deviceId, String path) {
        if (!mDeviceModelMap.containsKey(deviceId)) {
            LogUtils.e(TAG, "getStringValue not contains deviceId " + deviceId);
            return "unknown";
        }

        String value = "unknown";
        DeviceModel model = mDeviceModelMap.get(deviceId);
        if (model == null) {
            return value;
        }
        try {
            String[] pathArray = path.split("\\.");
            LogUtils.i(TAG, "getStringValue path " + Arrays.toString(pathArray));
            if (model.model.has(pathArray[0]) && model.model.getJSONObject(pathArray[0]).has(pathArray[1])) {
                value = model.model.getJSONObject(pathArray[0]).getJSONObject(pathArray[1]).getString("stVal");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public int getIntValue(String deviceId, String path) {
        if (!mDeviceModelMap.containsKey(deviceId)) {
            LogUtils.e(TAG, "getStringValue not contains deviceId " + deviceId);
            return -1;
        }

        int value = -1;
        DeviceModel model = mDeviceModelMap.get(deviceId);
        if (model == null) {
            return value;
        }
        try {
            String[] pathArray = path.split("\\.");
            LogUtils.i(TAG, "getIntValue path " + Arrays.toString(pathArray));
            if (model.model.has(pathArray[0]) && model.model.getJSONObject(pathArray[0]).has(pathArray[1])) {
                value = model.model.getJSONObject(pathArray[0]).getJSONObject(pathArray[1]).getInt("stVal");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public void setStringValue(String deviceId, String path, String value, IResultListener<ModelMessage> listener) {
        if (!mDeviceModelMap.containsKey(deviceId)) {
            LogUtils.e(TAG, "getStringValue not contains deviceId " + deviceId);
            return;
        }

        String data = String.format("{\"ctlVal\":\"%s\",\"origin\":\"\",\"t\":0}", value);
        LogUtils.i(TAG, "setStringValue " + path + " " + data);
        IoTVideoSdk.getMessageMgr().writeProperty(deviceId, path, data, listener);
    }

    public void setIntValue(String deviceId, String path, int value, IResultListener<ModelMessage> listener) {
        if (!mDeviceModelMap.containsKey(deviceId)) {
            LogUtils.e(TAG, "getStringValue not contains deviceId " + deviceId);
            return;
        }

        String data = String.format("{\"ctlVal\":%s,\"origin\":\"\",\"t\":0}", value);
        LogUtils.i(TAG, "setIntValue " + path + " " + data);
        IoTVideoSdk.getMessageMgr().writeProperty(deviceId, path, data, listener);
    }

    @Override
    public void onNotify(ModelMessage data) {
        if (data == null) {
            return;
        }

        DeviceModel model = mDeviceModelMap.get(data.device);
        if (model == null) {
            DeviceModel newModel = new DeviceModel(data.device, data.path, data.data);
            LogUtils.i(TAG, "onNotify add " + newModel.toString());
            mDeviceModelMap.put(data.device, newModel);
        } else {
            model.setData(data.path, data.data);
        }
    }

    public static class DeviceModel {
        public String deviceId;
        public JSONObject model;

        public DeviceModel(String deviceId, JSONObject model) {
            this.deviceId = deviceId;
            this.model = model;
        }

        DeviceModel(String deviceId, String path, String data) {
            //LogUtils.d(TAG, "path : " + path + ", data : " + data);

            this.deviceId = deviceId;
            model = new JSONObject();
            if (TextUtils.isEmpty(path)) {
                return;
            }

            try {
                String[] pathSplits = path.split("\\.");

                //LogUtils.d(TAG, "pathSplits " + pathSplits.length);

                JSONObject tmpJSONObject = model;
                for (int i = 0, size = pathSplits.length; i < size; i++) {
                    if (i == size - 1) {
                        tmpJSONObject.put(pathSplits[i], new JSONObject(data));
                    } else {
                        tmpJSONObject.put(pathSplits[i], new JSONObject());
                        tmpJSONObject = tmpJSONObject.getJSONObject(pathSplits[i]);
                    }
                }

                //LogUtils.d(TAG, "model " + model);
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
            }
        }

        void setData(String path, String data) {
            if (TextUtils.isEmpty(path) || TextUtils.isEmpty(data)) {
                return;
            }

            try {
                String[] pathSplits = path.split("\\.");
                JSONObject tmpJSONObject = model;
                for (int i = 0, size = pathSplits.length; i < size; i++) {
                    if (i == size - 1) {
                        tmpJSONObject.put(pathSplits[i], new JSONObject(data));
                    } else {
                        if (tmpJSONObject.has(pathSplits[i])) {
                            tmpJSONObject = tmpJSONObject.getJSONObject(pathSplits[i]);
                        } else {
                            tmpJSONObject.put(pathSplits[i], new JSONObject());
                            tmpJSONObject = tmpJSONObject.getJSONObject(pathSplits[i]);
                        }
                    }
                }
            } catch (Exception e) {
                LogUtils.e(TAG, e.getMessage());
            }
        }

        @Override
        public String toString() {
            return "DeviceModel{" +
                    "deviceId='" + deviceId + '\'' +
                    ", model=" + model +
                    '}';
        }
    }

}
