package com.tencentcs.iotvideodemo.netconfig;

import com.tencentcs.iotvideo.netconfig.NetConfigInfo;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

public class NetConfigViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NetConfigViewModel.class)) {
            return (T) new NetConfigViewModel(new NetConfigInfo());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
