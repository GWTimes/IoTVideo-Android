package com.gwell.iotvideodemo.netconfig;

import com.google.gson.JsonObject;
import com.gwell.http.utils.HttpUtils;
import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.messagemgr.IResultListener;
import com.gwell.iotvideo.netconfig.DeviceInfo;
import com.gwell.iotvideo.netconfig.NetConfig;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.base.HttpRequestState;
import com.gwell.iotvideodemo.base.SimpleSubscriberListener;

import androidx.lifecycle.MutableLiveData;

class NetConfigHelper {

    private NetConfigViewModel mNetConfigViewModel;

    NetConfigHelper(NetConfigViewModel model) {
        mNetConfigViewModel = model;
    }

    void bindDevice(String did, MutableLiveData<HttpRequestState> httpRequestStateMutableLiveData) {
        AccountMgr.getInstance().deviceBind(did, new SimpleSubscriberListener(httpRequestStateMutableLiveData) {
            @Override
            public void onSuccess(JsonObject response) {
                super.onSuccess(response);
                BindDeviceResult bindDeviceResult = HttpUtils.JsonToEntity(response.toString(), BindDeviceResult.class);
                if (bindDeviceResult != null && bindDeviceResult.getData() != null) {
                    int subscribeResult = NetConfig.getInstance().subscribeDevice(bindDeviceResult.getData().getToken());
                    LogUtils.i("NetConfigHelper", "subscribeDevice result = " + subscribeResult);
                }
            }
        });
    }

    void findDevices() {
        DeviceInfo[] deviceInfos = IoTVideoSdk.getNetConfig().newWiredNetConfig().getDeviceList();
        mNetConfigViewModel.getLanDeviceData().setValue(deviceInfos);
        if(deviceInfos != null){
            for (DeviceInfo deviceInfo : deviceInfos) {
                LogUtils.d("NetConfigHelper", "findDevices " + deviceInfo);
            }
        }
    }

    void getNetConfigToken(IResultListener listener) {
        IoTVideoSdk.getNetConfig().getNetConfigToken(listener);
    }
}
