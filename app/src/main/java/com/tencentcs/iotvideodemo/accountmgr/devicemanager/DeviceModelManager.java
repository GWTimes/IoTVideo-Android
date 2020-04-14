package com.tencentcs.iotvideodemo.accountmgr.devicemanager;


import android.text.TextUtils;

import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.IModelListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.Utils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;

import org.json.JSONException;
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
        if (newModel != null) {
            LogUtils.i(TAG, "setDeviceModel " + newModel.toString());
            mDeviceModelMap.put(newModel.deviceId, newModel);
        }
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
                JSONObject targetJSONObject = model.model
                        .getJSONObject(pathArray[0])
                        .getJSONObject(pathArray[1]);
                value = targetJSONObject.getString(getValueName(targetJSONObject, pathArray));
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
                JSONObject targetJSONObject = model.model
                        .getJSONObject(pathArray[0])
                        .getJSONObject(pathArray[1]);
                value = targetJSONObject.getInt(getValueName(targetJSONObject, pathArray));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public void setStringValue(String deviceId, String path, String value, IResultListener<ModelMessage> listener) {
        if (!mDeviceModelMap.containsKey(deviceId)) {
            LogUtils.e(TAG, "getStringValue not contains deviceId " + deviceId);
            listener.onError(0, "getStringValue not contains deviceId");
            return;
        }

        JSONObject jsonObject = getJSONObjectByPath(deviceId, path);
        if (jsonObject == null) {
            LogUtils.e(TAG, "setStringValue not contains json object " + path);
            listener.onError(0, "setStringValue not contains json object");
            return;
        }
        String[] pathArray = path.split("\\.");
        try {
            jsonObject.put(getValueName(jsonObject, pathArray), value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "setStringValue " + path + " " + jsonObject.toString());
        IoTVideoSdk.getMessageMgr().writeProperty(deviceId, path, jsonObject.toString(), listener);
    }

    public void setIntValue(String deviceId, String path, int value, IResultListener<ModelMessage> listener) {
        if (!mDeviceModelMap.containsKey(deviceId)) {
            LogUtils.e(TAG, "setIntValue not contains deviceId " + deviceId);
            listener.onError(0, "setIntValue not contains deviceId");
            return;
        }
        JSONObject jsonObject = getJSONObjectByPath(deviceId, path);
        if (jsonObject == null) {
            LogUtils.e(TAG, "setIntValue not contains json object " + path);
            listener.onError(0, "setIntValue not contains json object");
            return;
        }
        String[] pathArray = path.split("\\.");
        try {
            jsonObject.put(getValueName(jsonObject, pathArray), value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "setIntValue " + path + " " + jsonObject.toString());
        IoTVideoSdk.getMessageMgr().writeProperty(deviceId, path, jsonObject.toString(), listener);
    }

    public JSONObject getJSONObjectByPath(String deviceId, String path) {
        if (!mDeviceModelMap.containsKey(deviceId)) {
            LogUtils.e(TAG, "getJSONObjectByPath not contains deviceId " + deviceId);
            return null;
        }

        DeviceModel model = mDeviceModelMap.get(deviceId);
        if (model == null) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            String[] pathArray = path.split("\\.");
            LogUtils.i(TAG, "getJSONObjectByPath path " + Arrays.toString(pathArray));
            if (model.model.has(pathArray[0]) && model.model.getJSONObject(pathArray[0]).has(pathArray[1])) {
                jsonObject = model.model.getJSONObject(pathArray[0]).getJSONObject(pathArray[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private String getValueName(JSONObject jsonObject, String[] pathArray) {
        if (jsonObject.has("stVal")) {
            return "stVal";
        } else if (jsonObject.has("setVal")) {
            return "setVal";
        } else if (jsonObject.has("ctlVal")) {
            return "ctlVal";
        }
        return "unknown";
    }

    @Override
    public void onNotify(ModelMessage data) {
        if (data == null) {
            return;
        }

        DeviceModel model = mDeviceModelMap.get(data.device);
        if (model == null) {
            DeviceModel newModel = new DeviceModel(data.device, data.path, data.data);
            mDeviceModelMap.put(data.device, newModel);
            LogUtils.i(TAG, "onNotify device model = " + data.device + " " + newModel.toString());
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
            if (path == null) {
                return;
            } else if (path.equals("")) {
                try {
                    model = new JSONObject(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }

            model = new JSONObject();
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
            LogUtils.d(TAG, "setData path " + path + " " + data);
            if (TextUtils.isEmpty(path) || TextUtils.isEmpty(data)) {
                return;
            }

            try {
                String[] pathSplits = path.split("\\.");
                JSONObject tmpJSONObject = model;
                for (int i = 0, size = pathSplits.length; i < size; i++) {
                    if (i == size - 1) {
                        tmpJSONObject.put(pathSplits[i], new JSONObject(data));
                        LogUtils.d(TAG, "put data to json path " + path + " " + data);
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
                    ", model=" + Utils.printJson(model.toString()) +
                    '}';
        }
    }

}
