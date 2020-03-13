package com.tencentcs.iotvideodemo.netconfig;

import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.DataMessage;
import com.tencentcs.iotvideo.messagemgr.IModelListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.netconfig.DeviceInfo;
import com.tencentcs.iotvideo.netconfig.NetConfigInfo;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideodemo.base.HttpRequestState;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NetConfigViewModel extends ViewModel {
    private MutableLiveData<NetConfigInfo> mNetConfigInfoData;
    private MutableLiveData<HttpRequestState> mBindStateData;
    private MutableLiveData<DeviceInfo[]> mLanDeviceData;
    private MutableLiveData<String> mDeviceNetConfigData;

    private NetConfigHelper mNetConfigHelper;

    NetConfigViewModel(MutableLiveData<NetConfigInfo> netConfigInfoViewModel) {
        mNetConfigInfoData = netConfigInfoViewModel;
        mBindStateData = new MutableLiveData<>();
        mLanDeviceData = new MutableLiveData<>();
        mDeviceNetConfigData = new MutableLiveData<>();
        mNetConfigHelper = new NetConfigHelper(this);
        addNetConfigListener();
    }

    NetConfigViewModel(NetConfigInfo netConfigInfo) {
        mNetConfigInfoData = new MutableLiveData<>();
        mBindStateData = new MutableLiveData<>();
        mLanDeviceData = new MutableLiveData<>();
        mDeviceNetConfigData = new MutableLiveData<>();
        mNetConfigHelper = new NetConfigHelper(this);
        updateNetConfigInfo(netConfigInfo);
        addNetConfigListener();
    }

    void updateNetConfigInfo(NetConfigInfo netConfigInfo) {
        mNetConfigInfoData.setValue(netConfigInfo);
    }

    public NetConfigInfo getNetConfigInfo() {
        return mNetConfigInfoData.getValue();
    }

    public MutableLiveData<HttpRequestState> getBindStateData() {
        return mBindStateData;
    }

    public MutableLiveData<DeviceInfo[]> getLanDeviceData() {
        return mLanDeviceData;
    }

    public MutableLiveData<String> getNetConfigStateData() {
        return mDeviceNetConfigData;
    }

    public void findDevice() {
        mNetConfigHelper.findDevices();
    }

    public void bindDevice(String devId) {
        mNetConfigHelper.bindDevice(devId, mBindStateData);
    }

    public void getNetConfigToken(IResultListener<DataMessage> listener) {
        mNetConfigHelper.getNetConfigToken(listener);
    }

    private void addNetConfigListener() {
        IoTVideoSdk.getMessageMgr().addModelListener(new IModelListener() {
            @Override
            public void onNotify(ModelMessage data) {
                mDeviceNetConfigData.setValue(data.data);
            }
        });
    }
}
