package com.gwell.iotvideodemo.netconfig;

import com.gwell.iotvideo.netconfig.DeviceInfo;
import com.gwell.iotvideo.netconfig.NetConfigInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NetConfigViewModel extends ViewModel {
    static final int START_BIND = 1;
    static final int BIND_SUCCESS = 2;
    static final int BIND_ERROR = 3;

    private MutableLiveData<NetConfigInfo> mNetConfigInfoData;
    private MutableLiveData<Integer> mNetConfigStateData;
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

    void updateNetConfigState(Integer value) {
        mNetConfigStateData.setValue(value);
    }

    int getNetConfigState() {
        Integer value = mNetConfigStateData.getValue();
        return value == null ? 0 : value;
    }

    public MutableLiveData<Integer> getNetConfigStateData() {
        return mNetConfigStateData;
    }

    public MutableLiveData<DeviceInfo[]> getLanDeviceData() {
        return mLanDeviceData;
    }

    public void findDevice() {
        mNetConfigHelper.findDevices();
    }

    public void bindDevice(String did) {
        mNetConfigHelper.bindDevice(did);
    }
}
