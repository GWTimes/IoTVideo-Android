package com.gwell.iotvideodemo.netconfig;

import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.messagemgr.DataMessage;
import com.gwell.iotvideo.utils.rxjava.IResultListener;
import com.gwell.iotvideo.netconfig.DeviceInfo;
import com.gwell.iotvideo.netconfig.wired.WiredNetConfig;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.base.HttpRequestState;
import com.gwell.iotvideodemo.base.SimpleSubscriberListener;

import androidx.lifecycle.MutableLiveData;

class NetConfigHelper {
    private static final String TAG = "NetConfigHelper";

    private NetConfigViewModel mNetConfigViewModel;

    NetConfigHelper(NetConfigViewModel model) {
        mNetConfigViewModel = model;
    }

    void bindDevice(String did, MutableLiveData<HttpRequestState> httpRequestStateMutableLiveData) {
        AccountMgr.getHttpService().deviceBind(did, true, new SimpleSubscriberListener(httpRequestStateMutableLiveData));
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
