package com.gwell.iotvideodemo.accountmgr.login;

import android.content.Context;

import com.google.gson.JsonObject;
import com.gwell.iotvideo.http.HttpCode;
import com.gwell.iotvideo.utils.JSONUtils;
import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideo.utils.rxjava.SubscriberListener;
import com.gwell.iotvideodemo.MyApp;
import com.gwell.iotvideodemo.accountmgr.AccountSPUtils;
import com.gwell.iotvideodemo.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_ERROR;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_START;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_SUCCESS;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_ERROR;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_START;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_SUCCESS;

class LoginManager {
    private static final String TAG = "LoginManager";

    private LoginViewModel mLoginViewModel;
    private Context mContext;

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

    void checkCode(String account, int flag) {
        SubscriberListener subscriberListener = new SubscriberListener() {
            @Override
            public void onStart() {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_VCODE_START, null, null));
            }

            @Override
            public void onSuccess(JsonObject response) {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_VCODE_SUCCESS, response.toString(), null));
            }

            @Override
            public void onFail(Throwable e) {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_VCODE_ERROR, null, e));
            }
        };
        if (isEmailValid(account)) {
            AccountMgr.getHttpService().emailCheckCode(account, flag, subscriberListener);
        } else {
            AccountMgr.getHttpService().mobileCheckCode("86", account, flag, subscriberListener);
        }
    }

    void login(String account,
               String password,
               String uuid) {
        SubscriberListener subscriberListener = new SubscriberListener() {
            @Override
            public void onStart() {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_START, null, null));
            }

            @Override
            public void onSuccess(JsonObject response) {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_SUCCESS, response.toString(), null));
                LoginInfo loginInfo = JSONUtils.JsonToEntity(response.toString(), LoginInfo.class);
                if (loginInfo != null && loginInfo.getCode() == HttpCode.ERROR_0) {
                    LogUtils.i(TAG, "login success : " + loginInfo.toString());
                    if (loginInfo.getData() != null) {
                        String token = loginInfo.getData().getIvToken();
                        int validityTime = loginInfo.getData().getExpireTime();
                        saveLoginInfo(loginInfo.getData().getAccessId(), token, validityTime);
                    }
                }
            }

            @Override
            public void onFail(Throwable e) {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_ERROR, null, e));
            }
        };
        AccountMgr.getHttpService().accountLogin(account, password, uuid, subscriberListener);
    }

    void register(String account,
                  String pwd,
                  String vcode) {
        SubscriberListener subscriberListener = new SubscriberListener() {
            @Override
            public void onStart() {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_START, null, null));
            }

            @Override
            public void onSuccess(JsonObject response) {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_SUCCESS, response.toString(), null));
            }

            @Override
            public void onFail(Throwable e) {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_ERROR, null, e));
            }
        };
        if (isEmailValid(account)) {
            AccountMgr.getHttpService().emailRegister(MyApp.CID, account, pwd, vcode, subscriberListener);
        } else {
            AccountMgr.getHttpService().mobileRegister(MyApp.CID, "86", account, pwd, vcode, subscriberListener);
        }
    }

    void retrieve(String account,
                  String pwd,
                  String vcode) {
        SubscriberListener subscriberListener = new SubscriberListener() {
            @Override
            public void onStart() {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_START, null, null));
            }

            @Override
            public void onSuccess(JsonObject response) {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_SUCCESS, response.toString(), null));
            }

            @Override
            public void onFail(Throwable e) {
                mLoginViewModel.getLoginState().setValue(new LoginViewModel.LoginState(STATE_ERROR, null, e));
            }
        };
        if (isEmailValid(account)) {
            AccountMgr.getHttpService().emailResetPwd(account, pwd, vcode, subscriberListener);
        } else {
            AccountMgr.getHttpService().mobileResetPwd("86", account, pwd, vcode, subscriberListener);
        }
    }

    private void saveLoginInfo(String accessId, String token, int validityTime) {
        String realToken = token.substring(0, 96);
        String secretKey = token.substring(96, 128);
        AccountMgr.setSecretInfo(accessId, secretKey, realToken);
        IoTVideoSdk.register(Long.valueOf(accessId), token);

        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.ACCESS_ID, accessId);
        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.SECRET_KEY, secretKey);
        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.IV_TOKEN, realToken);
        AccountSPUtils.getInstance().putInteger(mContext, AccountSPUtils.VALIDITY_TIMESTAMP, validityTime);
    }
}
