package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.IModelListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;

import androidx.annotation.Nullable;

public class DeviceOTAActivity extends BaseActivity implements View.OnClickListener, IModelListener {
    private static final String TAG = "DeviceOTAActivity";

    private TextView mTvCurrentVersion, mTvLatestVersion, mTvOTAMode, mProgress;

    private String mDeviceId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_ota);
        mTvCurrentVersion = findViewById(R.id.tv_current_version);
        mTvLatestVersion = findViewById(R.id.tv_latest_version);
        mTvOTAMode = findViewById(R.id.tv_ota_mode);
        mProgress = findViewById(R.id.tv_progress);
        findViewById(R.id.btn_update_latest_version).setOnClickListener(this);
        findViewById(R.id.btn_start_ota).setOnClickListener(this);
        findViewById(R.id.btn_get_progress).setOnClickListener(this);
        if (getIntent() != null) {
            String devId = getIntent().getStringExtra("deviceID");
            if (!TextUtils.isEmpty(devId)) {
                mDeviceId = devId;
                LogUtils.i(TAG, "mDeviceId = " + mDeviceId);
            }
        }
//        displayOTAInfo();
        IoTVideoSdk.getMessageMgr().addModelListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IoTVideoSdk.getMessageMgr().removeModelListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_latest_version:
                getLatestVersion();
                break;
            case R.id.btn_start_ota:
                sendOTARequest();
                break;
            case R.id.btn_get_progress:
                getOTAProgress();
                break;
        }
    }

    private void getLatestVersion() {
        DeviceModelManager.getInstance().getLatestVersion(mDeviceId, new IResultListener<ModelMessage>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(ModelMessage msg) {
                LogUtils.i(TAG, "getLatestVersion msg " + msg.toString());
                Snackbar.make(mTvLatestVersion, "发送成功", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Snackbar.make(mTvLatestVersion, "发送失败 " + errorCode + " " + errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void sendOTARequest() {
        DeviceModelManager.getInstance().startOTA(mDeviceId, new IResultListener<ModelMessage>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(ModelMessage msg) {
                LogUtils.i(TAG, "startOTA msg " + msg.toString());
                Snackbar.make(mTvLatestVersion, "发送成功", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Snackbar.make(mTvLatestVersion, "发送失败 " + errorCode + " " + errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void getOTAProgress() {
        int progress = DeviceModelManager.getInstance().getOTAProgress(mDeviceId);
        mProgress.setText("当前进度 " + progress);
    }

    private void displayOTAInfo() {
        mTvLatestVersion.setText(DeviceModelManager.getInstance().getStringValue(mDeviceId, "ProReadonly._otaVersion"));
    }

    @Override
    public void onNotify(ModelMessage data) {
        LogUtils.i(TAG, "onModeChanged deviceId:" + data.device + ", path:" + data.path + ", data:" + data.data);
        if ("ProReadonly._otaVersion".equals(data.path)) {
            mTvLatestVersion.setText(data.data);
        } else if ("ProReadonly._otaUpgrade".equals(data.path)) {
            mProgress.setText(data.data);
        }
    }
}
