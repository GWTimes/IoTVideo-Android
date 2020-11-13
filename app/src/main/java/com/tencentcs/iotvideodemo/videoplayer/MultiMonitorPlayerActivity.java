package com.tencentcs.iotvideodemo.videoplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.settings.DeviceSettingsSPUtils;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MultiMonitorPlayerActivity extends BaseActivity {
    private static final String TAG = "MultiMonitorPlayerActivity";
    private static final int MAX_DEVICE = 4;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_monitor_palyer);
        GridLayout rootView = findViewById(R.id.root_view);

        fragmentManager = getSupportFragmentManager();

        String[] deviceIDArray = new String[0];
        ArrayList<Integer> sourceIDArray = new ArrayList<>();
        if (getIntent() != null) {
            String[] tempDeviceArray = getIntent().getStringArrayExtra("deviceIDArray");
            if (tempDeviceArray != null) {
                deviceIDArray = tempDeviceArray;
            }
            ArrayList<Integer> tempSourceList = getIntent().getIntegerArrayListExtra("sourceIDArray");
            if (tempSourceList != null) {
                sourceIDArray = tempSourceList;
            }
        }

        int deviceCount = Math.min(MAX_DEVICE, deviceIDArray.length);
        spilt2Grid(rootView, deviceCount);
        for (int i = 0; i < deviceCount; i++) {
            int resId = addFrameLayout2Root(rootView);
            int sourceId = sourceIDArray.size() > i ? sourceIDArray.get(i) :
                    DeviceSettingsSPUtils.getInstance().default_sourceId(this);
            showFragment(resId, deviceIDArray[i], (short) sourceId);
        }
    }

    private void spilt2Grid(GridLayout rootView, int deviceCount) {
        int column = (int) Math.ceil(Math.sqrt(deviceCount));
        int row = (int) Math.ceil(deviceCount * 1.0f / column);
        rootView.setColumnCount(column);
        rootView.setRowCount(row);
        LogUtils.i(TAG, "spilt2Grid column = " + column + ", row = " + row);
    }

    private int addFrameLayout2Root(GridLayout rootView) {
        FrameLayout frameLayout = new FrameLayout(this);
        rootView.addView(frameLayout);
        frameLayout.setId(View.generateViewId());
        GridLayout.LayoutParams layoutParams = (GridLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.width = 0;
        layoutParams.height = 0;
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        return frameLayout.getId();
    }

    private void showFragment(int resId, String deviceId, short sourceId) {
        MonitorPlayerFragment fragment = new MonitorPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("deviceID", deviceId);
        bundle.putBoolean("isMultiCall", true);
        bundle.putShort("sourceID", sourceId);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        fragmentTransaction.commit();
    }
}
