package com.tencentcs.iotvideodemo.accountmgr.devicemanager;


import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.IModelListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;

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

    JsonElement getJsonElement(String deviceId, String path) {
        if (!mDeviceModelMap.containsKey(deviceId)) {
            LogUtils.e(TAG, "getStringValue not contains deviceId " + deviceId);
            return null;
        }

        DeviceModel model = mDeviceModelMap.get(deviceId);
        if (model == null || model.model == null) {
            return null;
        }
        JsonElement value;
        value = getJsonElementByPath(model.model, path);
        LogUtils.i(TAG, "getValue " + path + " " + value);
        return value;
    }

    void setJsonElement(String deviceId, String path, JsonElement value, IResultListener<ModelMessage> listener) {
        LogUtils.i(TAG, "setValue " + path + " " + value);
        IoTVideoSdk.getMessageMgr().writeProperty(deviceId, path, value.toString(), listener);
    }

    private JsonElement getJsonElementByPath(JsonObject srcJsonObject, String path) {
        if (srcJsonObject == null) {
            return null;
        }
        JsonElement jsonElement;
        JsonObject subJsonObject;
        String[] pathArray = path.split("\\.", 2);
        if (pathArray.length <= 1) {
            jsonElement = srcJsonObject.get(pathArray[0]);
            return jsonElement;
        } else {
            subJsonObject = srcJsonObject.getAsJsonObject(pathArray[0]);
            return getJsonElementByPath(subJsonObject, pathArray[1]);
        }
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
        public JsonObject model;

        public DeviceModel(String deviceId, JsonObject model) {
            this.deviceId = deviceId;
            this.model = model;
        }

        DeviceModel(String deviceId, String path, String data) {
            this.deviceId = deviceId;
            if (path == null) {
                //do nothing
            } else if (path.equals("")) {
                JsonParser jsonParser = new JsonParser();
                model = jsonParser.parse(data).getAsJsonObject();
            } else {
                setData(path, data);
            }
            LogUtils.d(TAG, "new DeviceModel " + model);
        }

        void setData(String path, String data) {
            LogUtils.d(TAG, "setData path " + path + " " + data);
            if (TextUtils.isEmpty(path) || TextUtils.isEmpty(data)) {
                return;
            }
            if (model == null) {
                model = new JsonObject();
            }

            JsonParser jsonParser = new JsonParser();
            String[] pathSplits = path.split("\\.");
            JsonObject jsonObject = model;
            for (int i = 0, size = pathSplits.length; i < size; i++) {
                if (i == size - 1) {
                    jsonObject.add(pathSplits[i], jsonParser.parse(data));
                } else {
                    if (jsonObject.getAsJsonObject(pathSplits[i]) != null) {
                        jsonObject = jsonObject.getAsJsonObject(pathSplits[i]);
                    } else {
                        jsonObject.add(pathSplits[i], new JsonObject());
                        jsonObject = jsonObject.getAsJsonObject(pathSplits[i]);
                    }
                }
            }

            LogUtils.d(TAG, "setData result " + model);
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
