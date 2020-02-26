package com.gwell.iotvideodemo;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.messagemgr.EventMessage;
import com.gwell.iotvideo.messagemgr.IEventListener;
import com.gwell.iotvideo.messagemgr.IModelListener;
import com.gwell.iotvideo.messagemgr.ModelMessage;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideo.utils.UrlHelper;
import com.gwell.iotvideo.utils.qrcode.QRCode;
import com.gwell.iotvideo.utils.qrcode.QRCodeHelper;
import com.gwell.iotvideo.utils.rxjava.SubscriberListener;
import com.gwell.iotvideodemo.accountmgr.AccountSPUtils;
import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceManagerActivity;
import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceModelManager;
import com.gwell.iotvideodemo.accountmgr.login.LoginActivity;
import com.gwell.iotvideodemo.base.BaseActivity;
import com.gwell.iotvideodemo.netconfig.PrepareNetConfigActivity;
import com.gwell.iotvideodemo.test.TestActivity;
import com.gwell.iotvideodemo.utils.AppSPUtils;
import com.gwell.iotvideodemo.videoplayer.CustomCaptureActivity;
import com.gwell.zxing.CaptureActivity;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.net.Uri;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private static final int CAPTURE_REQUEST_CODE = 1;

    private ProgressBar mProgressBar;
    private NavigationView mNavigationView;
    private View mNavigationHead;
    private DrawerLayout mDrawer;
    private TextView mTvAppVersion;

    private TextView mTvIvToken;
    private Switch mSwitchServer;

    private long mFirstTimeClickBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progress_logout);
        mNavigationView = findViewById(R.id.nav_view);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationHead = mNavigationView.getHeaderView(0);
        mTvAppVersion = mNavigationHead.findViewById(R.id.tv_app_version);
        mTvAppVersion.setText(BuildConfig.VERSION_NAME);
        if (BuildConfig.DEBUG) {
            findViewById(R.id.start_test_activity).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.device_manager).setOnClickListener(this);
        findViewById(R.id.net_config).setOnClickListener(this);
        findViewById(R.id.start_test_activity).setOnClickListener(this);
        findViewById(R.id.github).setOnClickListener(this);
        mNavigationHead.findViewById(R.id.logout).setOnClickListener(this);

        mTvIvToken = mNavigationHead.findViewById(R.id.iv_token);

        if (com.gwell.iotvideo.BuildConfig.DEBUG) {
            mNavigationHead.findViewById(R.id.iv_token_row).setVisibility(View.VISIBLE);
            mNavigationHead.findViewById(R.id.switch_server_row).setVisibility(View.VISIBLE);
        }

        mSwitchServer = mNavigationHead.findViewById(R.id.switch_server);
        mSwitchServer.setChecked(UrlHelper.getInstance().getServerType() == UrlHelper.SERVER_DEV);
        mSwitchServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isDebugServer = (UrlHelper.getInstance().getServerType() == UrlHelper.SERVER_DEV);
                if (isDebugServer != isChecked) {
                    AccountSPUtils.getInstance().putInteger(MainActivity.this, AccountSPUtils.VALIDITY_TIMESTAMP, 0);
                    AppSPUtils.getInstance().putBoolean(MainActivity.this, AppSPUtils.NEED_SWITCH_SERVER_TYPE, true);
                    AppSPUtils.getInstance().putInteger(MainActivity.this, AppSPUtils.SERVER_TYPE,
                            isChecked ? UrlHelper.SERVER_DEV : UrlHelper.SERVER_RELEASE);
                    Toast.makeText(getApplicationContext(), "重启应用后生效", Toast.LENGTH_LONG).show();
                }
            }
        });

        String realToken = AccountSPUtils.getInstance().getString(this, AccountSPUtils.IV_TOKEN, "");
        String secretKey = AccountSPUtils.getInstance().getString(this, AccountSPUtils.SECRET_KEY, "");
        mTvIvToken.setText(String.format("%s%s", realToken, secretKey));

        //设置log
        applyForStoragePerMission();

        registerNotify();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tvUserId = mNavigationHead.findViewById(R.id.user_id);
        tvUserId.setText(AccountSPUtils.getInstance().getUserId(this));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.net_config:
                startNetMatchActivity();
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.device_manager:
                startDeviceManagerActivity();
                break;
            case R.id.start_test_activity:
                startTestActivity();
                break;
            case R.id.github:
                Intent intent = new Intent();
                intent.setData(Uri.parse("https://github.com/GWTimes/IoTVideo-Android"));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                break;
        }
    }

    private void startNetMatchActivity() {
        Intent intent = new Intent(this, PrepareNetConfigActivity.class);
        startActivity(intent);
    }

    private void startDeviceManagerActivity() {
        Intent intent = new Intent(this, DeviceManagerActivity.class);
        startActivity(intent);
    }

    private void startTestActivity() {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    private void logout() {
        AccountMgr.getHttpService().logout(new SubscriberListener() {
            @Override
            public void onStart() {
                showProgress(true);
                mDrawer.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                showProgress(false);
                AccountSPUtils.getInstance().clear(MainActivity.this);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                showProgress(false);
                Snackbar.make(mProgressBar, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - mFirstTimeClickBack < 2000) {
                finish();
            } else {
                mFirstTimeClickBack = currentTime;
            }
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
                    public void onSuccess(@NonNull JsonObject response) {
                        Snackbar.make(mProgressBar, R.string.success, Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(@NonNull Throwable e) {
                        Snackbar.make(mProgressBar, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void registerNotify() {
        IoTVideoSdk.getMessageMgr().addEventListener(new IEventListener() {
            @Override
            public void onNotify(EventMessage data) {
                Toast.makeText(getApplicationContext(), data.data, Toast.LENGTH_LONG).show();
            }
        });

        IoTVideoSdk.getMessageMgr().addModelListener(new IModelListener() {
            @Override
            public void onNotify(ModelMessage data) {
                Toast.makeText(getApplicationContext(), "deviceId:" + data.device +
                        ", path:" + data.path + ", data:" + data.data, Toast.LENGTH_LONG).show();
            }
        });

        //监听模型变化
        IoTVideoSdk.getMessageMgr().addModelListener(DeviceModelManager.getInstance());
    }
}
