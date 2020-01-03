package com.gwell.iotvideodemo.netconfig;

import com.google.gson.JsonObject;
import com.gwell.http.SubscriberListener;
import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.netconfig.DeviceInfo;
import com.gwell.iotvideo.netconfig.NetConfig;

import static com.gwell.iotvideodemo.netconfig.NetConfigViewModel.BIND_ERROR;
import static com.gwell.iotvideodemo.netconfig.NetConfigViewModel.BIND_SUCCESS;
import static com.gwell.iotvideodemo.netconfig.NetConfigViewModel.START_BIND;

public class NetConfigHelper {

    private NetConfigViewModel mNetConfigViewModel;

    NetConfigHelper(NetConfigViewModel model) {
        mNetConfigViewModel = model;
    }

    void bindDevice(String did) {
        AccountMgr.getInstance().deviceBind(did, new SubscriberListener() {
            @Override
            public void onStart() {
                mNetConfigViewModel.updateNetConfigState(START_BIND);
            }

            @Override
            public void onSuccess(JsonObject response) {
                mNetConfigViewModel.updateNetConfigState(BIND_SUCCESS);
            }

            @Override
            public void onFail(Throwable e) {
                mNetConfigViewModel.updateNetConfigState(BIND_ERROR);
            }
        });
    }

    public void findDevices() {
        DeviceInfo[] deviceInfos = IoTVideoSdk.getNetConfig().newWiredNetConfig().getDeviceList();
        mNetConfigViewModel.getLanDeviceData().setValue(deviceInfos);
    }
}
