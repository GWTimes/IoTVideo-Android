package com.tencentcs.iotvideodemo.videoplayer;

import android.os.Bundle;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

public class MultiMonitorPlayerActivity extends BaseActivity {
    private static final String TAG = "MultiMonitorPlayerActivity";

    private static final int MAX_MULTI_MONITOR = 2;

    private String[] mDeviceIdArray = new String[MAX_MULTI_MONITOR];

    private MonitorPlayerFragment[] mPlayerFragmentArray = new MonitorPlayerFragment[MAX_MULTI_MONITOR];
    private int[] mPlayerFragmentIdArray = {R.id.monitor_1, R.id.monitor_2};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_monitor_palyer);
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < mPlayerFragmentArray.length; i++) {
            mPlayerFragmentArray[i] = (MonitorPlayerFragment) fragmentManager.findFragmentById(mPlayerFragmentIdArray[i]);
        }

        if (getIntent() != null) {
            String[] deviceIDArray = getIntent().getStringArrayExtra("deviceIDArray");
            if (deviceIDArray != null) {
                for (int i = 0; i < deviceIDArray.length && i < MAX_MULTI_MONITOR; i++) {
                    mDeviceIdArray[i] = deviceIDArray[i];
                    LogUtils.i(TAG, "mDeviceId" + i + " : " + mDeviceIdArray[i]);
                    mPlayerFragmentArray[i].setDeviceId(mDeviceIdArray[i]);
                }
            }
        }
    }
}
