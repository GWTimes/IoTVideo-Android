package com.tencentcs.iotvideodemo.accountmgr.login;

import android.content.Context;

import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.http.HttpCode;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils;
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceModelManager;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.base.MVVMSubscriberListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

class LoginManager {
    private static final String TAG = "LoginManager";

    private LoginViewModel mLoginViewModel;
    private Context mContext;
    private String mCurrentAccount;

    LoginManager(Context context, LoginViewModel loginViewModel) {
        mContext = context;
        mLoginViewModel = loginViewModel;
    }

    static boolean isPhoneValid(String userName) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(userName);
        return m.matches() && userName.length() >= 7 && userName.length() <= 12;
    }

    static boolean isEmailValid(String userName) {
        return userName.contains("@");
    }

    static boolean isPasswordValid(String password) {
        return password.length() >= 4 && password.length() <= 20;
    }

    void checkCode(String account, int flag, MutableLiveData<HttpRequestState> request) {
        mCurrentAccount = account;
        if (isEmailValid(account)) {
            AccountMgr.getHttpService().emailCheckCode(account, flag, new MVVMSubscriberListener(request));
        } else {
            AccountMgr.getHttpService().mobileCheckCode("86", account, flag, new MVVMSubscriberListener(request));
        }
    }

    void login(String account, String password, String uuid, MutableLiveData<HttpRequestState> request) {
        MVVMSubscriberListener subscriberListener = new MVVMSubscriberListener(request) {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LoginInfo loginInfo = JSONUtils.JsonToEntity(response.toString(), LoginInfo.class);
                if (loginInfo != null && loginInfo.getCode() == HttpCode.ERROR_0) {
                    LogUtils.i(TAG, "login success : " + loginInfo.toString());
                    LoginInfo.DataBean loginData = loginInfo.getData();
                    if (loginData != null) {
                        if (loginData.isLoginDataValid()) {
                            super.onSuccess(response);
                            String accessToken = loginInfo.getData().getAccessToken();
                            int validityTime = loginInfo.getData().getExpireTime();
                            initIoTVideo(loginInfo.getData().getAccessId(), accessToken);
                            saveLoginInfo(loginInfo.getData().getAccessId(), accessToken, validityTime);
                        } else {
                            super.onFail(new Throwable(loginData.toString()));
                        }
                    } else {
                        super.onFail(new Throwable("service feedback data null"));
                    }
                }
            }
        };
        AccountMgr.getHttpService().accountLogin(account, password, uuid, subscriberListener);
    }

    void register(String pwd, String vcode, MutableLiveData<HttpRequestState> request) {
        if (isEmailValid(mCurrentAccount)) {
            AccountMgr.getHttpService().emailRegister(mCurrentAccount, pwd, vcode, new MVVMSubscriberListener(request));
        } else {
            AccountMgr.getHttpService().mobileRegister("86", mCurrentAccount, pwd, vcode, new MVVMSubscriberListener(request));
        }
    }

    void retrieve(String pwd, String vcode, MutableLiveData<HttpRequestState> request) {
        if (isEmailValid(mCurrentAccount)) {
            AccountMgr.getHttpService().emailResetPwd(mCurrentAccount, pwd, vcode, new MVVMSubscriberListener(request));
        } else {
            AccountMgr.getHttpService().mobileResetPwd("86", mCurrentAccount, pwd, vcode, new MVVMSubscriberListener(request));
        }
    }

    private void initIoTVideo(String accessId, String accessToken) {
        String realToken = accessToken.substring(0, 96);
        String secretKey = accessToken.substring(96, 128);
        //注册IoTVideo
        IoTVideoSdk.register(Long.valueOf(accessId), accessToken);
        //注册帐号体系
        AccountMgr.setSecretInfo(accessId, secretKey, realToken);
        //监听物模型变化
        IoTVideoSdk.getMessageMgr().addModelListener(DeviceModelManager.getInstance());
    }

    private void saveLoginInfo(String accessId, String accessToken, int validityTime) {
        String realToken = accessToken.substring(0, 96);
        String secretKey = accessToken.substring(96, 128);
        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.ACCESS_ID, accessId);
        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.SECRET_KEY, secretKey);
        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.TOKEN, realToken);
        AccountSPUtils.getInstance().putInteger(mContext, AccountSPUtils.VALIDITY_TIMESTAMP, validityTime);
    }
}
