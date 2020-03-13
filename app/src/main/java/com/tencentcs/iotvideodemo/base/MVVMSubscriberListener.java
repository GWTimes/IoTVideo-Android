package com.tencentcs.iotvideodemo.base;

import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;

import androidx.annotation.NonNull;

import androidx.lifecycle.MutableLiveData;

public class MVVMSubscriberListener implements SubscriberListener {
    private MutableLiveData<HttpRequestState> httpRequestState;

    public MVVMSubscriberListener(MutableLiveData<HttpRequestState> httpRequestState) {
        this.httpRequestState = httpRequestState;
    }

    @Override
    public void onStart() {
        HttpRequestState requestState = new HttpRequestState();
        requestState.setStatus(HttpRequestState.Status.START);
        httpRequestState.setValue(requestState);
    }

    @Override
    public void onSuccess(@NonNull JsonObject response) {
        HttpRequestState requestState = new HttpRequestState();
        requestState.setStatus(HttpRequestState.Status.SUCCESS);
        requestState.setJsonObject(response);
        httpRequestState.setValue(requestState);
    }

    @Override
    public void onFail(@NonNull Throwable e) {
        HttpRequestState requestState = new HttpRequestState();
        requestState.setStatus(HttpRequestState.Status.ERROR);
        requestState.setE(e);
        httpRequestState.setValue(requestState);
    }
}
