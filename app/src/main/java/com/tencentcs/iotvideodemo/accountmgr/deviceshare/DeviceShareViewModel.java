package com.tencentcs.iotvideodemo.accountmgr.deviceshare;

import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceList;
import com.tencentcs.iotvideodemo.base.HttpRequestState;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

class DeviceShareViewModel extends ViewModel {
    private MutableLiveData<DeviceList.Device> mDeviceData;

    private MutableLiveData<HttpRequestState> mQueryShareListData;

    private MutableLiveData<HttpRequestState> mGenShareQRCodeData;

    private MutableLiveData<HttpRequestState> mDeviceShareData;

    private MutableLiveData<HttpRequestState> mFindUserData;

    private MutableLiveData<CancelShare> mCancelShareData;

    private DeviceShareManager mManager;

    DeviceShareViewModel(DeviceList.Device netConfigInfo) {
        mDeviceData = new MutableLiveData<>();
        mQueryShareListData = new MutableLiveData<>();
        mGenShareQRCodeData = new MutableLiveData<>();
        mDeviceShareData = new MutableLiveData<>();
        mFindUserData = new MutableLiveData<>();
        mCancelShareData = new MutableLiveData<>();
        mManager = new DeviceShareManager();
        updateDevice(netConfigInfo);
    }

    void updateDevice(DeviceList.Device device) {
        mDeviceData.setValue(device);
    }

    MutableLiveData<HttpRequestState> getQueryShareListData() {
        return mQueryShareListData;
    }

    MutableLiveData<HttpRequestState> getGenShareQRCodeData() {
        return mGenShareQRCodeData;
    }

    MutableLiveData<HttpRequestState> getDeviceShareData() {
        return mDeviceShareData;
    }

    MutableLiveData<HttpRequestState> getFindUserData() {
        return mFindUserData;
    }

    MutableLiveData<CancelShare> getCancelShareData() {
        return mCancelShareData;
    }

    void listSharedUsers() {
        mManager.listSharedUsers(mDeviceData.getValue(), mQueryShareListData);
    }

    void genShareQrcode() {
        mManager.genShareQrcode(mDeviceData.getValue(), mGenShareQRCodeData);
    }

    void shareDevice(String shareId) {
        mManager.shareDevice(shareId, mDeviceData.getValue(), mDeviceShareData);
    }

    void findUser(String account) {
        mManager.findUser(account, mFindUserData);
    }

    void cancelShare(ShareList.DataBean.User user, int position) {
        mManager.cancelShare(user, mDeviceData.getValue(), mCancelShareData, position);
    }

    static class CancelShare {
        HttpRequestState httpRequestState;
        int position;
        CancelShare() {
            httpRequestState = new HttpRequestState();
        }
    }
}
