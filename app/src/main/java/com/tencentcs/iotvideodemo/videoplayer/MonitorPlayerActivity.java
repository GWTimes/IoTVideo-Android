package com.tencentcs.iotvideodemo.videoplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MonitorPlayerActivity extends BaseActivity {
    private static final String TAG = "MonitorPlayerActivity";

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_player);
        fragmentManager = getSupportFragmentManager();
        String deviceId = getIntent().getStringExtra("deviceID");
        MonitorPlayerFragment fragment = new MonitorPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("deviceID", deviceId);
        fragment.setArguments(bundle);
        showFragment(fragment);
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.monitor_1, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int orientation = getRequestedOrientation();
        LogUtils.i(TAG, "onResume orientation = " + orientation);
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            if (getResources().getDisplayMetrics().widthPixels > getResources().getDisplayMetrics().heightPixels) {
                onOrientationChanged(Configuration.ORIENTATION_LANDSCAPE);
            } else {
                onOrientationChanged(Configuration.ORIENTATION_PORTRAIT);
            }
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ||
                orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            onOrientationChanged(Configuration.ORIENTATION_LANDSCAPE);
        } else {
            onOrientationChanged(Configuration.ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onOrientationChanged(newConfig.orientation);
    }

    private void onOrientationChanged(int orientation) {
        LogUtils.i(TAG, "onOrientationChanged orientation = " + orientation);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(lp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    }
}
