package com.tencentcs.iotvideodemo.accountmgr.deviceshare;

import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceList;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.base.MVVMSubscriberListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

class DeviceShareManager {
    private static final String TAG = "DeviceShareManager";

    static boolean isEmailValid(String userName) {
        return userName.contains("@");
    }

    void listSharedUsers(DeviceList.Device device, MutableLiveData<HttpRequestState> httpRequestState) {
        LogUtils.i(TAG, "listSharedUsers");
        AccountMgr.getHttpService().listSharedUsers(device.getDevId(), new MVVMSubscriberListener(httpRequestState));
    }

    void genShareQrcode(DeviceList.Device device, MutableLiveData<HttpRequestState> httpRequestState) {
        AccountMgr.getHttpService().genShareQrcode(device.getDevId(), device.getRemarkName(), device.getRemarkName(), new MVVMSubscriberListener(httpRequestState));
    }

    void shareDevice(String shareId, DeviceList.Device device, MutableLiveData<HttpRequestState> httpRequestState) {
        AccountMgr.getHttpService().accountShare(shareId, device.getDevId(), new MVVMSubscriberListener(httpRequestState));
    }

    void findUser(String account, MutableLiveData<HttpRequestState> httpRequestState) {
        String region = isEmailValid(account) ? null : "86";
        AccountMgr.getHttpService().findUser(region, account, new MVVMSubscriberListener(httpRequestState));
    }

    void cancelShare(ShareList.DataBean.User user, DeviceList.Device device,
                     final MutableLiveData<DeviceShareViewModel.CancelShare> cancelShareMutableLiveData, final int position) {
        AccountMgr.getHttpService().cancelShare(device.getDevId(), user.getAccessId(), new SubscriberListener() {
            @Override
            public void onStart() {
                DeviceShareViewModel.CancelShare requestState = new DeviceShareViewModel.CancelShare();
                requestState.httpRequestState.setStatus(HttpRequestState.Status.START);
                cancelShareMutableLiveData.setValue(requestState);
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                DeviceShareViewModel.CancelShare requestState = new DeviceShareViewModel.CancelShare();
                requestState.httpRequestState.setStatus(HttpRequestState.Status.SUCCESS);
                requestState.httpRequestState.setJsonObject(response);
                requestState.position = position;
                cancelShareMutableLiveData.setValue(requestState);
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                DeviceShareViewModel.CancelShare requestState = new DeviceShareViewModel.CancelShare();
                requestState.httpRequestState.setStatus(HttpRequestState.Status.ERROR);
                requestState.httpRequestState.setE(e);
                cancelShareMutableLiveData.setValue(requestState);
            }
        });
    }
}
