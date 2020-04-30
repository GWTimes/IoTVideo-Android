package com.tencentcs.iotvideodemo.videoplayer;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MultiMonitorPlayerActivity extends BaseActivity {
    private static final String TAG = "MultiMonitorPlayerActivity";

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_monitor_palyer);
        LinearLayout rootView = findViewById(R.id.root_view);

        fragmentManager = getSupportFragmentManager();

        String[] deviceIDArray = new String[0];
        if (getIntent() != null) {
            String[] tempArray = getIntent().getStringArrayExtra("deviceIDArray");
            if (tempArray != null) {
                deviceIDArray = tempArray;
            }
        }

        int viewCount = rootView.getChildCount();
        int deviceCount = deviceIDArray.length;
        for (int i = viewCount - 1; i >= 0; i--) {
            if (i < viewCount && i < deviceCount) {
                MonitorPlayerFragment fragment = new MonitorPlayerFragment();
                LogUtils.i(TAG, "mDeviceId " + i + " : " + deviceIDArray[i]);
                Bundle bundle = new Bundle();
                bundle.putString("deviceID", deviceIDArray[i]);
                bundle.putSerializable(MonitorConfig.class.getSimpleName(), MonitorConfig.defaultConfig());
                fragment.setArguments(bundle);
                showFragment(rootView.getChildAt(i).getId(), fragment);
            } else {
                rootView.removeViewAt(i);
            }
        }
    }

    private void showFragment(int resId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        fragmentTransaction.commit();
    }
}
