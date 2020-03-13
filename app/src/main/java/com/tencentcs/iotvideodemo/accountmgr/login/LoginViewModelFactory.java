package com.tencentcs.iotvideodemo.accountmgr.login;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModelFactory implements ViewModelProvider.Factory {
    private Context mContext;

    LoginViewModelFactory(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(mContext);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
