package com.gwell.iotvideodemo.accountmgr.devicemanager;


import android.text.TextUtils;

import com.gwell.iotvideo.messagemgr.IModelListener;
import com.gwell.iotvideo.messagemgr.ModelMessage;
import com.gwell.iotvideo.utils.LogUtils;

import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

public class DeviceModelManager implements IModelListener {
    private static final String TAG = "DeviceModelManager";

    private ConcurrentHashMap<Long, DeviceModel> mDeviceModelMap = new ConcurrentHashMap<>();

    private static class DeviceModelManagerHolder {
        private static final DeviceModelManager INSTANCE = new DeviceModelManager();
    }

    public static DeviceModelManager getInstance() {
        return DeviceModelManagerHolder.INSTANCE;
    }

    public DeviceModel getDeviceModel(Long deviceId) {
        return mDeviceModelMap.get(deviceId);
    }

    public void setDeviceModel(DeviceModel model) {
        mDeviceModelMap.put(model.deviceId, model);
    }

    public boolean isOnline(Long deviceId) {
        if (!mDeviceModelMap.containsKey(deviceId)) {
            return false;
        }

        boolean online = false;
        DeviceModel model = mDeviceModelMap.get(deviceId);
        try {
            if (model.model.has("ProReadonly") && model.model.getJSONObject("ProReadonly").has("_online")) {
                online = model.model.getJSONObject("ProReadonly").getJSONObject("_online").getInt("stVal") == 1 ? true : false;
            }
        } catch (Exception e) {

        }
        return online;
    }

    @Override
    public void onNotify(ModelMessage data) {
        if (data == null) {
            return;
        }

        DeviceModel model = mDeviceModelMap.get(data.device);
        if (model == null) {
            mDeviceModelMap.put(data.device, new DeviceModel(data.device, data.path, data.data));
        } else {
            model.setData(data.path, data.data);
        }
    }

    public static class DeviceModel {
        public Long deviceId;
        public JSONObject model;

        DeviceModel(long deviceId, String path, String data) {
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
    }

}
