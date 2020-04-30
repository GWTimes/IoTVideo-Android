package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonParser;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.IModelListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class DeviceOTAActivity extends BaseActivity implements View.OnClickListener, IModelListener {
    private static final String TAG = "DeviceOTAActivity";

    private TextView mTvLatestVersion, mProgress;
    private LinearLayout mLLVersionInfo;
    private AlertDialog mCurrentAlertDialog;

    private String mDeviceId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_ota);
        mTvLatestVersion = findViewById(R.id.tv_latest_version);
        mProgress = findViewById(R.id.tv_progress);
        mLLVersionInfo = findViewById(R.id.ll_version_info);
        findViewById(R.id.btn_get_latest_version).setOnClickListener(this);
        if (getIntent() != null) {
            String devId = getIntent().getStringExtra("deviceID");
            if (!TextUtils.isEmpty(devId)) {
                mDeviceId = devId;
                LogUtils.i(TAG, "mDeviceId = " + mDeviceId);
            }
        }
        IoTVideoSdk.getMessageMgr().addModelListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IoTVideoSdk.getMessageMgr().removeModelListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_latest_version) {
            getLatestVersion();
        }
    }

    private void getLatestVersion() {
        DeviceModelHelper.updateLatestVersion(mDeviceId, new IResultListener<ModelMessage>() {
            @Override
            public void onStart() {
                LogUtils.i(TAG, "updateLatestVersion starr");
            }

            @Override
            public void onSuccess(ModelMessage msg) {
                LogUtils.i(TAG, "updateLatestVersion msg " + msg.toString());
                Snackbar.make(mTvLatestVersion, "发送成功", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.i(TAG, "updateLatestVersion error " + errorCode + " " + errorMsg);
                Snackbar.make(mTvLatestVersion, "发送失败 " + errorCode + " " + errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void sendOTARequest() {
        DeviceModelHelper.startOTA(mDeviceId, new IResultListener<ModelMessage>() {
            @Override
            public void onStart() {
                LogUtils.i(TAG, "startOTA start");
            }

            @Override
            public void onSuccess(ModelMessage msg) {
                LogUtils.i(TAG, "startOTA msg " + msg.toString());
                Snackbar.make(mTvLatestVersion, "发送成功", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.i(TAG, "startOTA error " + errorCode + " " + errorMsg);
                Snackbar.make(mTvLatestVersion, "发送失败 " + errorCode + " " + errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onNotify(ModelMessage data) {
        LogUtils.i(TAG, "onModeChanged deviceId:" + data.device + ", path:" + data.path + ", data:" + data.data);
        if (TextUtils.isEmpty(data.data)) {
            Snackbar.make(mTvLatestVersion, "服务器返回数据无效", Snackbar.LENGTH_LONG).show();
            return;
        }
        if ("ProReadonly._otaVersion".equals(data.path) || "Action._otaVersion".equals(data.path)) {
            mLLVersionInfo.setVisibility(View.VISIBLE);
            JsonParser jsonParser = new JsonParser();
            String version = jsonParser.parse(data.data).getAsJsonObject().get("stVal").getAsString();
            if (TextUtils.isEmpty(version)) {
                mTvLatestVersion.setText("已是最新版本");
                mProgress.setText(100 + "%");
            } else {
                mTvLatestVersion.setText(version);
                mProgress.setText(0 + "%");
                showOTADialog(version);
            }
        } else if ("ProReadonly._otaUpgrade".equals(data.path) || "Action._otaUpgrade".equals(data.path)) {
            JsonParser jsonParser = new JsonParser();
            String progress = jsonParser.parse(data.data).getAsJsonObject().get("stVal").getAsString();
            mProgress.setText(progress + "%");
        }
    }

    private void showOTADialog(String version) {
        if (mCurrentAlertDialog != null && mCurrentAlertDialog.isShowing()) {
            mCurrentAlertDialog.dismiss();
        }
        String msg = String.format("%s%s%s", "设备当前待更新版本：\n\n", version, "\n\n是否确认更新到该版本？");
        mCurrentAlertDialog = new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendOTARequest();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
}
