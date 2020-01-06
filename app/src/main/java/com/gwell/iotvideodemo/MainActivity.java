package com.gwell.iotvideodemo;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.gwell.http.SubscriberListener;
import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideo.utils.qrcode.QRCode;
import com.gwell.iotvideo.utils.qrcode.QRCodeHelper;
import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceManagerActivity;
import com.gwell.iotvideodemo.accountmgr.login.LoginActivity;
import com.gwell.iotvideodemo.test.TestWebApiActivity;
import com.gwell.iotvideodemo.base.BaseActivity;
import com.gwell.iotvideodemo.netconfig.PrepareNetConfigActivity;
import com.gwell.iotvideodemo.test.TestQRCodeActivity;
import com.gwell.iotvideodemo.videoplayer.CustomCaptureActivity;
import com.gwell.iotvideodemo.videoplayer.VideoPlayerActivity;
import com.gwell.iotvideodemo.test.preview.CameraActivity;
import com.gwell.zxing.CaptureActivity;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private static final int CAPTURE_REQUEST_CODE = 1;

    private NestedScrollView mLlFunctions;
    private ProgressBar mProgressBar;
    private NavigationView mNavigationView;
    private View mNavigationHead;
    private DrawerLayout mDrawer;
    private TextView mTvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLlFunctions = findViewById(R.id.functions);
        mProgressBar = findViewById(R.id.progress_logout);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationHead = mNavigationView.getHeaderView(0);
        mTvAppVersion = mNavigationHead.findViewById(R.id.tv_app_version);
        mTvAppVersion.setText(BuildConfig.VERSION_NAME);
        findViewById(R.id.start_preview).setOnClickListener(this);
        findViewById(R.id.start_record).setOnClickListener(this);
        findViewById(R.id.start_web_api_activity).setOnClickListener(this);
        if (BuildConfig.DEBUG) {
            findViewById(R.id.start_web_api_activity).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.start_qrcode_activity).setOnClickListener(this);
        findViewById(R.id.start_player_activity).setOnClickListener(this);
        findViewById(R.id.start_net_config_activity).setOnClickListener(this);
        findViewById(R.id.start_device_manager_activity).setOnClickListener(this);
        mNavigationHead.findViewById(R.id.logout).setOnClickListener(this);

        //设置log
        applyForStoragePerMission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tvUserId = mNavigationHead.findViewById(R.id.user_id);
        tvUserId.setText(AccountMgr.getInstance().getUserId(this));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_preview:
                startPreview();
                break;
            case R.id.start_record:
                startRecord();
                break;
            case R.id.start_web_api_activity:
                startWebApiActivity();
                break;
            case R.id.start_qrcode_activity:
                startQRCodeActivity();
                break;
            case R.id.start_player_activity:
                startPlayerActivity();
                break;
            case R.id.start_net_config_activity:
                startNetMatchActivity();
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.start_device_manager_activity:
                startDeviceManagerActivity();
                break;
        }
    }

    private void startPreview() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.OPERATE_TYPE, CameraActivity.OPERATE_PREVIEW);
        startActivity(intent);
    }

    private void startRecord() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.OPERATE_TYPE, CameraActivity.OPERATE_RECORD);
        startActivity(intent);
    }

    private void startWebApiActivity() {
        Intent intent = new Intent(this, TestWebApiActivity.class);
        startActivity(intent);
    }

    private void startNetMatchActivity() {
        Intent intent = new Intent(this, PrepareNetConfigActivity.class);
        startActivity(intent);
    }

    private void startPlayerActivity() {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        startActivity(intent);
    }

    private void startQRCodeActivity() {
        Intent intent = new Intent(this, TestQRCodeActivity.class);
        startActivity(intent);
    }

    private void startDeviceManagerActivity() {
        Intent intent = new Intent(this, DeviceManagerActivity.class);
        startActivity(intent);
    }

    private void logout() {
        AccountMgr.getInstance().logout(new SubscriberListener() {
            @Override
            public void onStart() {
                showProgress(true);
                mDrawer.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onSuccess(JsonObject response) {
                showProgress(false);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }

            @Override
            public void onFail(Throwable e) {
                showProgress(false);
                Snackbar.make(mLlFunctions, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLlFunctions.setVisibility(show ? View.GONE : View.VISIBLE);
        mLlFunctions.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLlFunctions.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.openDrawer(GravityCompat.START);
            } else {
                mDrawer.closeDrawer(GravityCompat.START);
            }
            return true;
        } else if (item.getItemId() == R.id.action_menu_scan) {
            startCaptureActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void startCaptureActivity() {
        requestPermissions(new OnPermissionsListener() {
            @Override
            public void OnPermissions(boolean granted) {
                if (granted) {
                    Intent intent = new Intent(MainActivity.this, CustomCaptureActivity.class);
                    startActivityForResult(intent, CAPTURE_REQUEST_CODE);
                }
            }
        }, Manifest.permission.CAMERA);
    }

    @Override
    protected void applyForPermissionResult(int mark, Map<String, Boolean> permissionResult, boolean applyResult) {
        super.applyForPermissionResult(mark, permissionResult, applyResult);
        if (applyResult) {
            Boolean readStorage = permissionResult.get(Manifest.permission.READ_EXTERNAL_STORAGE);
            Boolean writeStorage = permissionResult.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (readStorage != null && writeStorage != null && readStorage && writeStorage) {
                IoTVideoSdk.setDebugMode(true, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CAPTURE_REQUEST_CODE == requestCode) {
            if (data != null) {
                String scanResult = data.getStringExtra(CaptureActivity.KEY_RESULT);
                LogUtils.i(TAG, "scan result = " + scanResult);
                QRCode qrCode = QRCodeHelper.analyse(scanResult);
                LogUtils.i(TAG, "onResultCallback = " + qrCode.toString());
                QRCodeHelper.handleQRCode(qrCode, new SubscriberListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(JsonObject response) {
                        Snackbar.make(mLlFunctions, R.string.success, Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(Throwable e) {
                        Snackbar.make(mLlFunctions, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
