package com.tencentcs.iotvideodemo.accountmgr.login;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.vas.VasMgr;
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils;
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceModelManager;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.base.MVVMSubscriberListener;
import com.tencentcs.iotvideodemo.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (TextUtils.isEmpty(userName)) {
            return false;
        }
        return userName.contains("@");
    }

    static boolean isPasswordValid(String password) {
        return password.length() >= 4 && password.length() <= 20;
    }

    void checkCode(String account, int flag, MutableLiveData<HttpRequestState> request) {
        mCurrentAccount = account;
        if (isEmailValid(account)) {
            //邮箱不用单独获取验证码
            HttpRequestState httpRequestState = new HttpRequestState();
            httpRequestState.setStatus(HttpRequestState.Status.SUCCESS);
            request.setValue(httpRequestState);
        } else {
            SafeCheckCode safeCheckCode = mLoginViewModel.getSafeCheckCode().getValue();
            if (safeCheckCode == null || safeCheckCode.isExpired()) {
                LogUtils.e(TAG, "safe check code is expired");
                return;
            }
            AccountMgr.getHttpService().mobileCheckCode("86", account, flag, safeCheckCode.ticket, safeCheckCode.randstr,
                    new MVVMSubscriberListener(request));
            safeCheckCode.setExpired(true);
        }
    }

    void login(String account, String password, String uuid, MutableLiveData<HttpRequestState> request) {
        MVVMSubscriberListener subscriberListener = new MVVMSubscriberListener(request) {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LoginInfo loginInfo = JSONUtils.JsonToEntity(response.toString(), LoginInfo.class);
                if (loginInfo != null) {
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
        if (isEmailValid(account)) {
            AccountMgr.getHttpService().emailLogin(account, password, uuid, subscriberListener);
        } else {
            AccountMgr.getHttpService().mobileLogin(account, "86", password, uuid, subscriberListener);
        }
    }

    void login(String uuid, MutableLiveData<HttpRequestState> request) {
        login("", "", uuid, request);
    }

    void loginAnonymous(int ttlMinutes, String tid, String oldAccessToken, MutableLiveData<HttpRequestState> request) {
        if (Utils.isOemVersion()) {
            HttpRequestState httpRequestState = new HttpRequestState();
            httpRequestState.setStatus(HttpRequestState.Status.ERROR);
            request.setValue(httpRequestState);
            return;
        }
        MVVMSubscriberListener subscriberListener = new MVVMSubscriberListener(request) {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LoginInfo loginInfo = JSONUtils.JsonToEntity(response.toString(), LoginInfo.class);
                if (loginInfo != null) {
                    LogUtils.i(TAG, "Anonymous login success : " + loginInfo.toString());
                    LoginInfo.DataBean loginData = loginInfo.getData();
                    if (loginData != null && loginData.isLoginDataValid()) {
                        final String accessToken = loginInfo.getData().getAccessToken();
                        final int validityTime = loginInfo.getData().getExpireTime();
                        initIoTVideo(loginInfo.getData().getAccessId(), accessToken);
                        saveLoginInfo(loginInfo.getData().getAccessId(), accessToken, validityTime);
                        super.onSuccess(response);
                    } else {
                        super.onFail(new Throwable("service feedback data null"));
                    }
                }
            }
        };

        AccountMgr.getHttpService().createAnonymousAccessToken(ttlMinutes, tid, oldAccessToken, subscriberListener);
    }

    void register(String pwd, String vcode, MutableLiveData<HttpRequestState> request) {
        if (isEmailValid(mCurrentAccount)) {
            if (Utils.isOemVersion()) {
                //邮箱获取验证码和注册一个接口完成
                SafeCheckCode safeCheckCode = mLoginViewModel.getSafeCheckCode().getValue();
                if (safeCheckCode != null && !safeCheckCode.isExpired()) {
                    AccountMgr.getHttpService().emailCheckCode(mCurrentAccount, pwd, 0, safeCheckCode.ticket,
                            safeCheckCode.randstr, new MVVMSubscriberListener(request));
                } else {
                    LogUtils.e(TAG, "register fail, invalid safe check code");
                }
            } else {
                AccountMgr.getHttpService().emailRegister(mCurrentAccount, pwd, vcode, new MVVMSubscriberListener(request));
            }
        } else {
            AccountMgr.getHttpService().mobileRegister("86", mCurrentAccount, pwd, vcode, new MVVMSubscriberListener(request));
        }
    }

    void register(String userName, MutableLiveData<HttpRequestState> request) {
        AccountMgr.getHttpService().emailRegister(userName, "", "", new MVVMSubscriberListener(request));
    }

    void retrieve(String pwd, String vcode, MutableLiveData<HttpRequestState> request) {
        if (isEmailValid(mCurrentAccount)) {
            if (Utils.isOemVersion()) {
                //邮箱获取验证码和找回密码一个接口完成
                SafeCheckCode safeCheckCode = mLoginViewModel.getSafeCheckCode().getValue();
                if (safeCheckCode != null && !safeCheckCode.isExpired()) {
                    AccountMgr.getHttpService().emailCheckCode(mCurrentAccount, pwd, 1, safeCheckCode.ticket,
                            safeCheckCode.randstr, new MVVMSubscriberListener(request));
                } else {
                    LogUtils.e(TAG, "retrieve fail, invalid safe check code");
                }
            } else {
                AccountMgr.getHttpService().emailResetPwd(mCurrentAccount, pwd, vcode, new MVVMSubscriberListener(request));
            }
        } else {
            AccountMgr.getHttpService().mobileResetPwd("86", mCurrentAccount, pwd, vcode, new MVVMSubscriberListener(request));
        }
    }

    String getCurrentAccount() {
        return mCurrentAccount;
    }

    private void initIoTVideo(String accessId, String accessToken) {
        //注册IoTVideo
        IoTVideoSdk.register(Long.valueOf(accessId), accessToken);
        //注册帐号体系
        AccountMgr.setAccessInfo(accessId, accessToken);
        //监听物模型变化
        IoTVideoSdk.getMessageMgr().addModelListener(DeviceModelManager.getInstance());
        //设置vas模块公共参数
        Map<String, Object> publicParams = new HashMap<>();
        publicParams.put("accessId", accessId);
        VasMgr.updatePublicParams(publicParams);
    }

    private void saveLoginInfo(String accessId, String accessToken, int validityTime) {
        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.ACCESS_ID, accessId);
        AccountSPUtils.getInstance().putString(mContext, AccountSPUtils.ACCESS_TOKEN, accessToken);
        AccountSPUtils.getInstance().putInteger(mContext, AccountSPUtils.VALIDITY_TIMESTAMP, validityTime);
    }
}
