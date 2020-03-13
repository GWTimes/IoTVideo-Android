package com.tencentcs.iotvideodemo.accountmgr.deviceshare;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceList;

public class DeviceViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DeviceShareViewModel.class)) {
            return (T) new DeviceShareViewModel(new DeviceList.Device());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
