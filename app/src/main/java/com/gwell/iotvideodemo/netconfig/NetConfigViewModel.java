package com.gwell.iotvideodemo.netconfig;

import com.gwell.iotvideo.netconfig.NetConfigInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NetConfigViewModel extends ViewModel {
    private MutableLiveData<NetConfigInfo> mNetConfigInfoViewModel;

    public NetConfigViewModel(MutableLiveData<NetConfigInfo> netConfigInfoViewModel) {
        mNetConfigInfoViewModel = netConfigInfoViewModel;
    }

    public NetConfigViewModel(NetConfigInfo netConfigInfo) {
        mNetConfigInfoViewModel = new MutableLiveData<>();
        updateNetConfigInfo(netConfigInfo);
    }

    public void updateNetConfigInfo(NetConfigInfo netConfigInfo) {
        mNetConfigInfoViewModel.setValue(netConfigInfo);
    }

    public NetConfigInfo getNetConfigInfo() {
        return mNetConfigInfoViewModel.getValue();
    }
}
