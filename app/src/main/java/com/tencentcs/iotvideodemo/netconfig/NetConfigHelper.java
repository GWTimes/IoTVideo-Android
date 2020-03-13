package com.tencentcs.iotvideodemo.netconfig;

import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.messagemgr.DataMessage;
import com.tencentcs.iotvideo.netconfig.DeviceInfo;
import com.tencentcs.iotvideo.netconfig.wired.WiredNetConfig;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideodemo.MyApp;
import com.tencentcs.iotvideodemo.accountmgrtc.httpservice.TencentcsAccountMgr;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.base.MVVMSubscriberListener;

import androidx.lifecycle.MutableLiveData;

class NetConfigHelper {
    private static final String TAG = "NetConfigHelper";

    private NetConfigViewModel mNetConfigViewModel;

    NetConfigHelper(NetConfigViewModel model) {
        mNetConfigViewModel = model;
    }

    void bindDevice(String devId, MutableLiveData<HttpRequestState> httpRequestStateMutableLiveData) {
        if (MyApp.ENABLE_TENCENTCS) {
            TencentcsAccountMgr.getHttpService().CreateBinding(TencentcsAccountMgr.getAccessId(), devId, "owner", true,
                    new MVVMSubscriberListener(httpRequestStateMutableLiveData));
        } else {
            AccountMgr.getHttpService().deviceBind(devId, true, new MVVMSubscriberListener(httpRequestStateMutableLiveData));
        }
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
