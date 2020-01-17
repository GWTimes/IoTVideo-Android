package com.gwell.iotvideodemo.accountmgr.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";

    static final int OPERATE_LOGIN = 0;
    static final int OPERATE_REGISTER = 1;
    static final int OPERATE_RETRIEVE_PASSWORD = 2;

    static final int STATE_VCODE_START = 0;
    static final int STATE_VCODE_SUCCESS = 1;
    static final int STATE_VCODE_ERROR = 2;
    static final int STATE_START = 3;
    static final int STATE_SUCCESS = 4;
    static final int STATE_ERROR = 5;

    private LoginManager mLoginManager;
    private Context mContext;

    private MutableLiveData<Integer> mOperator;
    private MutableLiveData<LoginState> mLoginState;

    LoginViewModel(Context context) {
        mLoginState = new MutableLiveData<>();
        mOperator = new MutableLiveData<>();
        mOperator.setValue(OPERATE_LOGIN);
        mContext = context;
        mLoginManager = new LoginManager(mContext, this);
    }

    MutableLiveData<Integer> getOperator() {
        return mOperator;
    }

    MutableLiveData<LoginState> getLoginState() {
        return mLoginState;
    }

    int getOperateType() {
        int type = OPERATE_LOGIN;
        if (mOperator.getValue() != null) {
            type = mOperator.getValue();
        }
        return type;
    }

    void checkCode(String account) {
        if (mOperator.getValue() == null) {
            return;
        }
        int flag = mOperator.getValue();
        mLoginManager.checkCode(account, flag);
    }

    void login(String account, String password) {
        mLoginManager.login(account, password);
    }

    void register(String account, String password, String vcode) {
        mLoginManager.register(account, password, vcode);
    }

    void retrieve(String account, String password, String vcode) {
        mLoginManager.retrieve(account, password, vcode);
    }

    static class LoginState {
        int state;
        String json;
        Throwable e;

        LoginState(int state, String json, Throwable e) {
            this.state = state;
            this.json = json;
            this.e = e;
        }
    }
}
