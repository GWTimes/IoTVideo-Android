package com.gwell.iotvideodemo.accountmgr.deviceshare;

import com.google.gson.JsonObject;
import com.gwell.http.SubscriberListener;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceList;
import com.gwell.iotvideodemo.base.HttpRequestState;
import com.gwell.iotvideodemo.base.SimpleSubscriberListener;

import androidx.lifecycle.MutableLiveData;

class DeviceShareManager {
    private static final String TAG = "DeviceShareManager";

    static boolean isEmailValid(String userName) {
        return userName.contains("@");
    }

    void listSharedUsers(DeviceList.Device device, MutableLiveData<HttpRequestState> httpRequestState) {
        LogUtils.i(TAG, "listSharedUsers");
        AccountMgr.getInstance().listSharedUsers(device.getDevId(), new SimpleSubscriberListener(httpRequestState));
    }

    void genShareQrcode(DeviceList.Device device, MutableLiveData<HttpRequestState> httpRequestState) {
        AccountMgr.getInstance().genShareQrcode(device.getDevId(), device.getDeviceName(), device.getDeviceName(), new SimpleSubscriberListener(httpRequestState));
    }

    void shareDevice(String shareId, DeviceList.Device device, MutableLiveData<HttpRequestState> httpRequestState) {
        AccountMgr.getInstance().accountShare(shareId, device.getDevId(), new SimpleSubscriberListener(httpRequestState));
    }

    void findUser(String account, MutableLiveData<HttpRequestState> httpRequestState) {
        String region = isEmailValid(account) ? null : "86";
        AccountMgr.getInstance().findUser(region, account, new SimpleSubscriberListener(httpRequestState));
    }

    void cancelShare(ShareList.DataBean.User user, DeviceList.Device device,
                     final MutableLiveData<DeviceShareViewModel.CancelShare> cancelShareMutableLiveData, final int position) {
        AccountMgr.getInstance().cancelShare(device.getDevId(), user.getIvUid(), new SubscriberListener() {
            @Override
            public void onStart() {
                DeviceShareViewModel.CancelShare requestState = new DeviceShareViewModel.CancelShare();
                requestState.httpRequestState.setStatus(HttpRequestState.Status.START);
                cancelShareMutableLiveData.setValue(requestState);
            }

            @Override
            public void onSuccess(JsonObject response) {
                DeviceShareViewModel.CancelShare requestState = new DeviceShareViewModel.CancelShare();
                requestState.httpRequestState.setStatus(HttpRequestState.Status.SUCCESS);
                requestState.httpRequestState.setJsonObject(response);
                requestState.position = position;
                cancelShareMutableLiveData.setValue(requestState);
            }

            @Override
            public void onFail(Throwable e) {
                DeviceShareViewModel.CancelShare requestState = new DeviceShareViewModel.CancelShare();
                requestState.httpRequestState.setStatus(HttpRequestState.Status.ERROR);
                requestState.httpRequestState.setE(e);
                cancelShareMutableLiveData.setValue(requestState);
            }
        });
    }
}
