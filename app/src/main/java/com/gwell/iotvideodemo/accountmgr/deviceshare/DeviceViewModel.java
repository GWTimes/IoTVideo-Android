package com.gwell.iotvideodemo.accountmgr.deviceshare;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceList;

public class DeviceViewModel extends ViewModel {
    private MutableLiveData<DeviceList.Device> mDeviceViewModel;

    public DeviceViewModel(MutableLiveData<DeviceList.Device> netConfigInfoViewModel) {
        mDeviceViewModel = netConfigInfoViewModel;
    }

    public DeviceViewModel(DeviceList.Device netConfigInfo) {
        mDeviceViewModel = new MutableLiveData<>();
        updateDevice(netConfigInfo);
    }

    public void updateDevice(DeviceList.Device netConfigInfo) {
        mDeviceViewModel.setValue(netConfigInfo);
    }

    public DeviceList.Device getDevice() {
        return mDeviceViewModel.getValue();
    }
}
