package com.gwell.iotvideodemo.netconfig.ap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseFragment;

public class APNetConfigFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ap_net_config, container, false);
    }
}
