package com.tencentcs.iotvideodemo.accountmgrtc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;
import com.tencentcs.iotvideodemo.MainActivity;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceModelManager;
import com.tencentcs.iotvideodemo.accountmgrtc.data.TencentLoginResponse;
import com.tencentcs.iotvideodemo.accountmgrtc.data.TencentRegisterResponse;
import com.tencentcs.iotvideodemo.accountmgrtc.httpservice.TencentcsAccountMgr;
import com.tencentcs.iotvideodemo.accountmgrtc.httpservice.TencentcsAccountSPUtils;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TencentcsLoginActivity extends BaseActivity {
    private static final String TAG = "TencentcsLoginActivity";

    private Handler mHandler;

    private EditText mEtSecretId, mEtSecretKey, mEtToken, mEtUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tencentcs_login);
        mEtSecretId = findViewById(R.id.et_secret_id);
        mEtSecretKey = findViewById(R.id.et_secret_key);
        mEtToken = findViewById(R.id.et_token);
        mEtUserName = findViewById(R.id.et_user_name);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
        mHandler = new Handler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void loginClicked() {
        String secretId = "";
        String secretKey = "";
        String token = "";
        String userName = "";
        secretId = mEtSecretId.getText().toString();
        secretKey = mEtSecretKey.getText().toString();
        token = mEtToken.getText().toString();
        userName = mEtUserName.getText().toString();

        if (!TextUtils.isEmpty(secretId) && !TextUtils.isEmpty(secretKey) && !TextUtils.isEmpty(userName)) {
            TencentcsAccountMgr.init(secretId, secretKey, token);
            TencentcsAccountSPUtils.getInstance().putString(TencentcsLoginActivity.this, TencentcsAccountSPUtils.SECRET_ID, secretId);
            TencentcsAccountSPUtils.getInstance().putString(TencentcsLoginActivity.this, TencentcsAccountSPUtils.SECRET_KEY, secretKey);
            TencentcsAccountSPUtils.getInstance().putString(TencentcsLoginActivity.this, TencentcsAccountSPUtils.TOKEN, token);
            register(userName);
        } else {
            Snackbar.make(mEtSecretId, "SecretId、SecretKey和用户名都不能为空", Snackbar.LENGTH_LONG).show();
        }
    }

    private void register(String userName) {
        TencentcsAccountMgr.getHttpService().CreateAppUsr(userName, new SubscriberListener() {
            @Override
            public void onStart() {
                LogUtils.i(TAG, "start register");
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG, "register " + response.toString());
                TencentRegisterResponse registerResponse = JSONUtils.JsonToEntity(response.toString(), TencentRegisterResponse.class);
                if (registerResponse != null && registerResponse.getResponse() != null
                        && !TextUtils.isEmpty(registerResponse.getResponse().getAccessId())) {
                    if (registerResponse.getResponse().isNewRegist()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                login(registerResponse.getResponse().getAccessId());
                            }
                        }, 2000);
                    } else {
                        login(registerResponse.getResponse().getAccessId());
                    }
                } else {
                    Snackbar.make(mEtSecretId, response.toString(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                LogUtils.i(TAG, "register " + e.getMessage());
                Snackbar.make(mEtSecretId, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void login(String accessId) {
        String uniqueId = Utils.getPhoneUuid(this);
        int ttlMinutes = 24 * 60;
        TencentcsAccountMgr.getHttpService().CreateUsrToken(accessId, uniqueId, ttlMinutes, new SubscriberListener() {
            @Override
            public void onStart() {
                LogUtils.i(TAG, "start login");
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG, "login " + response.toString());
                TencentLoginResponse loginResponse = JSONUtils.JsonToEntity(response.toString(), TencentLoginResponse.class);
                if (loginResponse != null && loginResponse.getResponse() != null
                        && !TextUtils.isEmpty(loginResponse.getResponse().getAccessToken())) {
                    loginSuccess(loginResponse.getResponse().getAccessId(), loginResponse.getResponse().getAccessToken(),
                            loginResponse.getResponse().getExpireTime());
                } else {
                    Snackbar.make(mEtSecretId, response.toString(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                LogUtils.i(TAG, "register " + e.getMessage());
                Snackbar.make(mEtSecretId, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loginSuccess(String accessId, String accessToken, int validityTime) {
        Snackbar.make(mEtSecretId, "登录成功", Snackbar.LENGTH_LONG).show();
        TencentcsAccountSPUtils.getInstance().putString(this, TencentcsAccountSPUtils.ACCESS_ID, accessId);
        TencentcsAccountSPUtils.getInstance().putString(this, TencentcsAccountSPUtils.ACCESS_TOKEN, accessToken);
        TencentcsAccountSPUtils.getInstance().putInteger(this, TencentcsAccountSPUtils.VALIDITY_TIMESTAMP, validityTime);
        TencentcsAccountMgr.setAccessId(accessId);
        //注册IoTVideo
        IoTVideoSdk.register(Long.valueOf(accessId), accessToken);
        //监听物模型变化
        IoTVideoSdk.getMessageMgr().addModelListener(DeviceModelManager.getInstance());
        startActivity(new Intent(TencentcsLoginActivity.this, MainActivity.class));
        finish();
    }
}
