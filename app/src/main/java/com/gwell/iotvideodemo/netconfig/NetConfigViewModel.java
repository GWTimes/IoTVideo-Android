package com.gwell.iotvideodemo.netconfig;

import com.gwell.iotvideo.messagemgr.DataMessage;
import com.gwell.iotvideo.utils.rxjava.IResultListener;
import com.gwell.iotvideo.netconfig.DeviceInfo;
import com.gwell.iotvideo.netconfig.NetConfigInfo;
import com.gwell.iotvideodemo.base.HttpRequestState;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NetConfigViewModel extends ViewModel {
    private MutableLiveData<NetConfigInfo> mNetConfigInfoData;
    private MutableLiveData<HttpRequestState> mNetConfigStateData;
    private MutableLiveData<DeviceInfo[]> mLanDeviceData;

    private NetConfigHelper mNetConfigHelper;

    NetConfigViewModel(MutableLiveData<NetConfigInfo> netConfigInfoViewModel) {
        mNetConfigInfoData = netConfigInfoViewModel;
        mNetConfigStateData = new MutableLiveData<>();
        mLanDeviceData = new MutableLiveData<>();
        mNetConfigHelper = new NetConfigHelper(this);
    }

    NetConfigViewModel(NetConfigInfo netConfigInfo) {
        mNetConfigInfoData = new MutableLiveData<>();
        mNetConfigStateData = new MutableLiveData<>();
        mLanDeviceData = new MutableLiveData<>();
        mNetConfigHelper = new NetConfigHelper(this);
        updateNetConfigInfo(netConfigInfo);
    }

    void updateNetConfigInfo(NetConfigInfo netConfigInfo) {
        mNetConfigInfoData.setValue(netConfigInfo);
    }

    public NetConfigInfo getNetConfigInfo() {
        return mNetConfigInfoData.getValue();
    }

    public MutableLiveData<HttpRequestState> getNetConfigStateData() {
        return mNetConfigStateData;
    }

    public MutableLiveData<DeviceInfo[]> getLanDeviceData() {
        return mLanDeviceData;
    }

    public void findDevice() {
        mNetConfigHelper.findDevices();
    }

    public void bindDevice(String did) {
        mNetConfigHelper.bindDevice(did, mNetConfigStateData);
    }

    public void getNetConfigToken(IResultListener<DataMessage> listener) {
        mNetConfigHelper.getNetConfigToken(listener);
    }
}
