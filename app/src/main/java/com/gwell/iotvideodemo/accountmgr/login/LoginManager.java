package com.gwell.iotvideodemo.accountmgr.login;

import com.google.gson.JsonObject;
import com.gwell.http.SubscriberListener;
import com.gwell.iotvideo.accountmgr.AccountMgr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_ERROR;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_START;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_SUCCESS;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_ERROR;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_START;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_SUCCESS;

public class LoginManager {
    private LoginViewModel mLoginViewModel;

    LoginManager(LoginViewModel loginViewModel) {
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
            AccountMgr.getInstance().emailCheckCode(account, flag, subscriberListener);
        } else {
            AccountMgr.getInstance().mobileCheckCode("86", account, flag, subscriberListener);
        }
    }

    void login(String account,
               String password) {
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
        AccountMgr.getInstance().accountLogin(account, password, subscriberListener);
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
            AccountMgr.getInstance().emailRegister(account, pwd, vcode, subscriberListener);
        } else {
            AccountMgr.getInstance().mobileRegister("86", account, pwd, vcode, subscriberListener);
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
            AccountMgr.getInstance().emailResetPwd(account, pwd, vcode, subscriberListener);
        } else {
            AccountMgr.getInstance().mobileResetPwd("86", account, pwd, vcode, subscriberListener);
        }
    }
}
