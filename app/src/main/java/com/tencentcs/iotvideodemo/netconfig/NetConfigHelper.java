package com.tencentcs.iotvideodemo.netconfig;

import android.text.TextUtils;

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
import com.tencentcs.iotvideodemo.BuildConfig;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.base.MVVMSubscriberListener;
import com.tencentcs.iotvideodemo.utils.Utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

class NetConfigHelper {
    private static final String TAG = "NetConfigHelper";

    private NetConfigViewModel mNetConfigViewModel;

    NetConfigHelper(NetConfigViewModel model) {
        mNetConfigViewModel = model;
    }

    void bindDevice(String devId, MutableLiveData<HttpRequestState> httpRequestStateMutableLiveData) {
        bindDevice(devId, null, httpRequestStateMutableLiveData);
    }

    void bindDevice(String devId, String devId2, MutableLiveData<HttpRequestState> httpRequestStateMutableLiveData) {
        MVVMSubscriberListener mvvmSubscriberListener = new MVVMSubscriberListener(httpRequestStateMutableLiveData) {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                super.onSuccess(response);
                BindDeviceResult bindDeviceResult = JSONUtils.JsonToEntity(response.toString(),
                        BindDeviceResult.class);
                NetConfig.getInstance().subscribeDevice(bindDeviceResult.getData().getDevToken(), devId);
            }
        };
        if (Utils.isOemVersion()) {
            AccountMgr.getHttpService().deviceBind(devId2, devId, devId2, 0, true, mvvmSubscriberListener);
        } else {
            AccountMgr.getHttpService().deviceBind(devId, true, mvvmSubscriberListener);
        }
    }

    void findDevices() {
        IoTVideoSdk.getNetConfig().newWiredNetConfig().getDeviceList(new WiredNetConfig.FindDeviceCallBack() {
            @Override
            public void onResult(DeviceInfo[] deviceInfos) {
                if(deviceInfos != null){
                    mNetConfigViewModel.getLanDeviceData().setValue(deviceInfos);
                    for (DeviceInfo deviceInfo : deviceInfos) {
                        LogUtils.d(TAG, "findDevices " + deviceInfo);
                    }
                } else {
                    mNetConfigViewModel.getLanDeviceData().setValue(new DeviceInfo[0]);
                }
            }
        });
    }

    void getNetConfigToken(IResultListener<DataMessage> listener) {
        IoTVideoSdk.getNetConfig().getNetConfigToken(listener);
    }
}
