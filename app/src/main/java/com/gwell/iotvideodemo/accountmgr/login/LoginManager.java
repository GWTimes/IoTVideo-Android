package com.gwell.iotvideodemo.accountmgr.login;

import android.content.Context;

import com.google.gson.JsonObject;
import com.gwell.iotvideo.http.HttpCode;
import com.gwell.iotvideo.utils.JSONUtils;
import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.MyApp;
import com.gwell.iotvideodemo.accountmgr.AccountSPUtils;
import com.gwell.iotvideodemo.base.HttpRequestState;
import com.gwell.iotvideodemo.base.SimpleSubscriberListener;

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
            AccountMgr.getHttpService().emailCheckCode(account, flag, new SimpleSubscriberListener(request));
        } else {
            AccountMgr.getHttpService().mobileCheckCode("86", account, flag, new SimpleSubscriberListener(request));
        }
    }

    void login(String account, String password, String uuid, MutableLiveData<HttpRequestState> request) {
        SimpleSubscriberListener subscriberListener = new SimpleSubscriberListener(request) {
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
            AccountMgr.getHttpService().emailRegister(MyApp.CID, mCurrentAccount, pwd, vcode, new SimpleSubscriberListener(request));
        } else {
            AccountMgr.getHttpService().mobileRegister(MyApp.CID, "86", mCurrentAccount, pwd, vcode, new SimpleSubscriberListener(request));
        }
    }

    void retrieve(String pwd, String vcode, MutableLiveData<HttpRequestState> request) {
        if (isEmailValid(mCurrentAccount)) {
            AccountMgr.getHttpService().emailResetPwd(mCurrentAccount, pwd, vcode, new SimpleSubscriberListener(request));
        } else {
            AccountMgr.getHttpService().mobileResetPwd("86", mCurrentAccount, pwd, vcode, new SimpleSubscriberListener(request));
        }
    }

    private void saveLoginInfo(String accessId, String accessToken, int validityTime) {
        String realToken = accessToken.substring(0, 96);
        String secretKey = accessToken.substring(96, 128);
        AccountMgr.setSecretInfo(accessId, secretKey, realToken);
        IoTVideoSdk.register(Long.valueOf(accessId), accessToken);

        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.ACCESS_ID, accessId);
        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.SECRET_KEY, secretKey);
        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.TOKEN, realToken);
        AccountSPUtils.getInstance().putInteger(mContext, AccountSPUtils.VALIDITY_TIMESTAMP, validityTime);
    }
}
