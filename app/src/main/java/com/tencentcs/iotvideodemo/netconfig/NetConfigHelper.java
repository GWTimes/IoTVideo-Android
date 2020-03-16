package com.tencentcs.iotvideodemo.netconfig;

import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.messagemgr.DataMessage;
import com.tencentcs.iotvideo.netconfig.DeviceInfo;
import com.tencentcs.iotvideo.netconfig.NetConfig;
import com.tencentcs.iotvideo.netconfig.wired.WiredNetConfig;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.base.MVVMSubscriberListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

class NetConfigHelper {
    private static final String TAG = "NetConfigHelper";

    private NetConfigViewModel mNetConfigViewModel;

    NetConfigHelper(NetConfigViewModel model) {
        mNetConfigViewModel = model;
    }

    void bindDevice(String devId, MutableLiveData<HttpRequestState> httpRequestStateMutableLiveData) {
        MVVMSubscriberListener mvvmSubscriberListener = new MVVMSubscriberListener(httpRequestStateMutableLiveData) {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                super.onSuccess(response);
                BindDeviceResult bindDeviceResult = JSONUtils.JsonToEntity(response.toString(),
                        BindDeviceResult.class);
                NetConfig.getInstance().subscribeDevice(bindDeviceResult.getData().getDevToken(), devId);
            }
        };
        AccountMgr.getHttpService().deviceBind(devId, true, mvvmSubscriberListener);
    }

    void findDevices() {
        IoTVideoSdk.getNetConfig().newWiredNetConfig().getDeviceList(new WiredNetConfig.FindDeviceCallBack() {
            @Override
            public void onResult(DeviceInfo[] deviceInfos) {
                mNetConfigViewModel.getLanDeviceData().setValue(deviceInfos);
                if(deviceInfos != null){
                    for (DeviceInfo deviceInfo : deviceInfos) {
                        LogUtils.d(TAG, "findDevices " + deviceInfo);
                    }
                }
            }
        });
    }

    void getNetConfigToken(IResultListener<DataMessage> listener) {
        IoTVideoSdk.getNetConfig().getNetConfigToken(listener);
    }
}
