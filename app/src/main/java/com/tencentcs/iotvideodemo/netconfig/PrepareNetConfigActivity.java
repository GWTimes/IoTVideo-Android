package com.tencentcs.iotvideodemo.netconfig;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.tencentcs.iotvideo.netconfig.NetConfigInfo;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.WifiUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class PrepareNetConfigActivity extends BaseActivity {
    private static final String TAG = "PrepareNetConfigActivity";

    private EditText mInputWifiPassword;
    private Spinner mWifiSpinner;

    private List<String> mWifiList;
    private String mSelectedWifiSSID;
    private String mSelectedWifiPWD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_net_config);

        mInputWifiPassword = findViewById(R.id.input_wifi_pwd);
        mWifiSpinner = findViewById(R.id.spinner_wifi_list);
        mWifiList = new ArrayList<>();
        getAllWifiInfo();
        mWifiSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    getAllWifiInfo();
                }
                return false;
            }
        });
        mWifiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedWifiSSID = mWifiList.get(i);
                LogUtils.i(TAG, "onItemSelected " + mSelectedWifiSSID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        findViewById(R.id.start_net_config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedWifiPWD = mInputWifiPassword.getText().toString();
                startNetConfig();
            }
        });
    }

    private void getAllWifiInfo() {
        requestPermissions(new OnPermissionsListener() {
            @Override
            public void OnPermissions(boolean granted) {
                if (granted) {
                    LogUtils.i(TAG, "getAllWifiInfo");
                    mWifiList = WifiUtils.getAllWifiInfo(getApplicationContext());
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(PrepareNetConfigActivity.this, R.layout.simple_spinner_item, mWifiList);
                    mWifiSpinner.setAdapter(arrayAdapter);
                }
            }
        }, R.string.location_permission_tip, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private void startNetConfig() {
        NetConfigInfo netConfigInfo = new NetConfigInfo(mSelectedWifiSSID, mSelectedWifiPWD, (byte) 2);
        Intent intent = new Intent(this, NetConfigActivity.class);
        intent.putExtra("NetConfigInfo", netConfigInfo);
        startActivity(intent);
    }
}
