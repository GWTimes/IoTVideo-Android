package com.tencentcs.iotvideodemo.accountmgr.login;

import android.content.Context;

import com.tencentcs.iotvideodemo.base.HttpRequestState;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";

    enum OperateType {
        Nothing, Login, Register, ResetPwd
    }

    enum Fragment {
        Login, InputAccount, InputPassword, TencentcsLogin
    }

    private LoginManager mLoginManager;

    private MutableLiveData<HttpRequestState> mLoginRequest;
    private MutableLiveData<HttpRequestState> mGetVCodeRequest;
    private MutableLiveData<HttpRequestState> mResetPwdRequest;
    private MutableLiveData<HttpRequestState> mRegisterRequest;

    private MutableLiveData<OperateType> mOperateData;
    private MutableLiveData<Fragment> mFragmentData;

    LoginViewModel(Context context) {
        mLoginRequest = new MutableLiveData<>();
        mGetVCodeRequest = new MutableLiveData<>();
        mResetPwdRequest = new MutableLiveData<>();
        mRegisterRequest = new MutableLiveData<>();
        mOperateData = new MutableLiveData<>();
        mOperateData.setValue(OperateType.Nothing);
        mFragmentData = new MutableLiveData<>();
        mLoginManager = new LoginManager(context, this);
    }

    MutableLiveData<OperateType> getOperateData() {
        return mOperateData;
    }

    MutableLiveData<Fragment> getFragmentData() {
        return mFragmentData;
    }

    MutableLiveData<HttpRequestState> getLoginState() {
        return mLoginRequest;
    }

    MutableLiveData<HttpRequestState> getVCodeState() {
        return mGetVCodeRequest;
    }

    MutableLiveData<HttpRequestState> getResetPwdState() {
        return mResetPwdRequest;
    }

    MutableLiveData<HttpRequestState> getRegisterState() {
        return mRegisterRequest;
    }

    void checkCode(String account, int flag) {
        mLoginManager.checkCode(account, flag, mGetVCodeRequest);
    }

    void login(String account, String password, String uuid) {
        mLoginManager.login(account, password, uuid, mLoginRequest);
    }

    void login(String uuid) {
        mLoginManager.login(uuid, mLoginRequest);
    }

    void register(String password, String vcode) {
        mLoginManager.register(password, vcode, mRegisterRequest);
    }

    void register(String userName) {
        mLoginManager.register(userName, mRegisterRequest);
    }

    void retrieve(String password, String vcode) {
        mLoginManager.retrieve(password, vcode, mResetPwdRequest);
    }
}
