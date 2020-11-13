package com.tencentcs.iotvideodemo.videoplayer;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencentcs.iotvideo.iotvideoplayer.IoTVideoView;
import com.tencentcs.iotvideo.iotvideoplayer.render.GestureGLSurfaceView;
import com.tencentcs.iotvideo.messagemgr.AInnerUserDataLister;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.settings.DeviceSettingsActivity;
import com.tencentcs.iotvideodemo.utils.StorageManager;
import com.tencentcs.iotvideodemo.videoplayer.monitor.IoTMonitorPlayerOwner;
import com.tencentcs.iotvideodemo.videoplayer.monitor.MonitorPlayerOwner;
import com.tencentcs.iotvideodemo.videoplayer.monitor.view.IoTMonitorView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MonitorPlayerFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "MonitorPlayerFragment";

    private static final int SETTING_REQUEST_CODE = 1;

    private IoTVideoView mVideoView;
    private IoTMonitorPlayerOwner mMonitorPlayerOwner;
    private String mDeviceId = "";
    private short mSourceId;
    private boolean mIsMultiCall;

    private View mTopView;
    private TextView mTvDeviceName;
    private TextView mTvPersonAndSpeed;
    private IoTMonitorView mIoTMonitorView;
    private int monitorViewerNumber = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monitor_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTopView = view.findViewById(R.id.layout_top);
        mTvDeviceName = view.findViewById(R.id.tv_device_name);
        mTvPersonAndSpeed = view.findViewById(R.id.tv_person_and_speed);
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.iv_setting).setOnClickListener(this);
        mIoTMonitorView = view.findViewById(R.id.tencentcs_gl_surface_view);
        mVideoView = mIoTMonitorView.getIoTVideoView();
        mVideoView.setLongPressedListener(new GestureGLSurfaceView.OnLongPressListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                Intent monitorIntent = new Intent(getActivity(), MonitorPlayerActivity.class);
                monitorIntent.putExtra("deviceID", mDeviceId);
                startActivity(monitorIntent);
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            mDeviceId = bundle.getString("deviceID");
            mSourceId = bundle.getShort("sourceID");
            mIsMultiCall = bundle.getBoolean("isMultiCall");
        }
        if (mIsMultiCall) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mIoTMonitorView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        createMonitorPlayOwner(mDeviceId);
        mTvDeviceName.setText(mDeviceId);
    }

    private void createMonitorPlayOwner(String deviceId) {
        MonitorConfig config;
        if (mIsMultiCall) {
            config = MonitorConfig.simpleConfig(getActivity(), mSourceId);
        } else {
            config = MonitorConfig.defaultConfig(getActivity());
        }
        LogUtils.i(TAG, "createMonitorPlayOwner config = " + config.toString());
        mMonitorPlayerOwner = new IoTMonitorPlayerOwner(this, mVideoView, deviceId, config);
        if (StorageManager.isVideoPathAvailable()) {
            String recordPath = StorageManager.getVideoPath() + File.separator + deviceId;
            File recordFile = new File(recordPath);
            if (recordFile.exists() || recordFile.mkdirs()) {
                mMonitorPlayerOwner.setRecordPath(recordPath);
            }
        }
        if (StorageManager.isPicPathAvailable()) {
            String snapPath = StorageManager.getPicPath() + File.separator + deviceId;
            File snapFile = new File(snapPath);
            if (snapFile.exists() || snapFile.mkdirs()) {
                mMonitorPlayerOwner.setSnapPath(snapPath);
            }
        }
        mIoTMonitorView.setMonitorOwner(mMonitorPlayerOwner);
        mMonitorPlayerOwner.addOnSpeedChangedListener(new MonitorPlayerOwner.OnSpeedChangedListener() {
            @Override
            public void onSpeedChanged(int speed) {
                mTvPersonAndSpeed.setText(String.format("%s人观看  %sKB/S", monitorViewerNumber, (speed / 1000)));
            }
        });
        mMonitorPlayerOwner.setInnerUserDataListener(new AInnerUserDataLister() {
            @Override
            public void onViewerNumberChanged(byte viewerNumber) {
                super.onViewerNumberChanged(viewerNumber);
                LogUtils.i(TAG,"onViewerNumberChanged, viewerNumber:" + viewerNumber);
                monitorViewerNumber = viewerNumber;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mMonitorPlayerOwner.play();
        LogUtils.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMonitorPlayerOwner.stop();
        LogUtils.i(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMonitorPlayerOwner != null) {
            mMonitorPlayerOwner.release();
            mMonitorPlayerOwner = null;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTopView.setVisibility(View.GONE);
        } else {
            mTopView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else if (view.getId() == R.id.iv_setting) {
            if (getActivity() != null) {
                startActivityForResult(new Intent(getActivity(), DeviceSettingsActivity.class), SETTING_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_CODE && data != null && data.getBooleanExtra("hasChanged", false)) {
            if (mMonitorPlayerOwner != null) {
                mMonitorPlayerOwner.release();
                mMonitorPlayerOwner = null;
            }
            createMonitorPlayOwner(mDeviceId);
        }
    }
}
