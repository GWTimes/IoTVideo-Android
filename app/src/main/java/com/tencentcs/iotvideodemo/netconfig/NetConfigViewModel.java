package com.tencentcs.iotvideodemo.netconfig;

import android.text.TextUtils;

import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.DataMessage;
import com.tencentcs.iotvideo.netconfig.DeviceInfo;
import com.tencentcs.iotvideo.netconfig.INetConfigResultListener;
import com.tencentcs.iotvideo.netconfig.NetConfigInfo;
import com.tencentcs.iotvideo.netconfig.NetConfigResult;
import com.tencentcs.iotvideo.netconfig.ap.APNetConfig;
import com.tencentcs.iotvideo.netconfig.data.NetMatchTokenResult;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideodemo.base.HttpRequestState;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NetConfigViewModel extends ViewModel {
    private static final String TAG = "NetConfigViewModel";

    private MutableLiveData<NetConfigInfo> mNetConfigInfoData;
    private MutableLiveData<HttpRequestState> mBindStateData;
    private MutableLiveData<DeviceInfo[]> mLanDeviceData;
    private MutableLiveData<DataMessage> mSendNetConfigInfoViaAPData;
    private MutableLiveData<DataMessage> mGetNetConfigTokenData;
    private MutableLiveData<NetConfigResult> mDeviceOnlineData;

    private NetConfigHelper mNetConfigHelper;
    private INetConfigResultListener mDeviceOnlineListener;
    private String mBindingDeviceId;

    NetConfigViewModel(NetConfigInfo netConfigInfo) {
        mNetConfigInfoData = new MutableLiveData<>();
        mBindStateData = new MutableLiveData<>();
        mLanDeviceData = new MutableLiveData<>();
        mSendNetConfigInfoViaAPData = new MutableLiveData<>();
        mGetNetConfigTokenData = new MutableLiveData<>();
        mDeviceOnlineData = new MutableLiveData<>();
        mNetConfigHelper = new NetConfigHelper(this);
        updateNetConfigInfo(netConfigInfo);
    }

    private boolean isMainThread() {
        return ArchTaskExecutor.getInstance().isMainThread();
    }

    void updateNetConfigInfo(NetConfigInfo netConfigInfo) {
        if (isMainThread()) {
            mNetConfigInfoData.setValue(netConfigInfo);
        } else {
            mNetConfigInfoData.postValue(netConfigInfo);
        }
    }

    public MutableLiveData<HttpRequestState> getBindStateData() {
        return mBindStateData;
    }

    MutableLiveData<DataMessage> getGetNetConfigTokenData() {
        return mGetNetConfigTokenData;
    }

    void getNetConfigToken() {
        mNetConfigHelper.getNetConfigToken(new IResultListener<DataMessage>() {
            @Override
            public void onStart() {
                LogUtils.i(TAG, "getNetConfigToken start");
                mGetNetConfigTokenData.postValue(new DataMessage(0, 101, -1, null));
            }

            @Override
            public void onSuccess(DataMessage msg) {
                LogUtils.i(TAG, "getNetConfigToken onSuccess : " + msg);
                byte[] token = msg.data;
                if (token != null) {
                    String tokenStr = new String(token);
                    NetMatchTokenResult result = JSONUtils.JsonToEntity(tokenStr, NetMatchTokenResult.class);
                    NetConfigInfo netConfigInfo = getNetConfigInfo();
                    netConfigInfo.setNetMatchId(result.getToken());
                    updateNetConfigInfo(netConfigInfo);
                }
                if (isMainThread()) {
                    mGetNetConfigTokenData.setValue(msg);
                } else {
                    mGetNetConfigTokenData.postValue(msg);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.i(TAG, "getNetConfigToken errorCode : " + errorCode + " " + errorMsg);
                if (isMainThread()) {
                    mGetNetConfigTokenData.setValue(new DataMessage(0, 101, errorCode, errorMsg.getBytes()));
                } else {
                    mGetNetConfigTokenData.postValue(new DataMessage(0, 101, errorCode, errorMsg.getBytes()));
                }
            }
        });
    }

    void registerDeviceOnlineCallback() {
        if (mDeviceOnlineListener == null) {
            mDeviceOnlineListener = new INetConfigResultListener() {

                @Override
                public void onNetConfigResult(NetConfigResult result) {
                    if (isMainThread()) {
                        mDeviceOnlineData.setValue(result);
                    } else {
                        mDeviceOnlineData.postValue(result);
                    }
                }
            };
        }
        IoTVideoSdk.getNetConfig().registerDeviceOnlineCallback(mDeviceOnlineListener);
    }

    void unregisterDeviceOnlineCallback() {
        IoTVideoSdk.getNetConfig().unregisterDeviceOnlineCallback(mDeviceOnlineListener);
        mDeviceOnlineListener = null;
    }

    public NetConfigInfo getNetConfigInfo() {
        return mNetConfigInfoData.getValue();
    }

    public MutableLiveData<DeviceInfo[]> getLanDeviceData() {
        return mLanDeviceData;
    }

    public MutableLiveData<NetConfigInfo> getNetConfigInfoLiveData() {
        return mNetConfigInfoData;
    }

    public MutableLiveData<DataMessage> getSendNetConfigInfoViaAPData() {
        return mSendNetConfigInfoViaAPData;
    }

    public MutableLiveData<NetConfigResult> getDeviceOnlineData() {
        return mDeviceOnlineData;
    }

    public void findDevice() {
        mNetConfigHelper.findDevices();
    }

    public void rebindDevice() {
        bindDevice(mBindingDeviceId);
    }

    public void bindDevice(String devId) {
        if (mLanDeviceData.getValue() != null) {
            for (DeviceInfo deviceInfo : mLanDeviceData.getValue()) {
                if (devId.equals(deviceInfo.tencentID)) {
                    String devId2 = String.valueOf(deviceInfo.deviceID);
                    bindDevice(devId, devId2);
                    return;
                }
            }
        }
        bindDevice(devId, null);
    }

    public void bindDevice(String devId, String devId2) {
        if (TextUtils.isEmpty(devId)) {
            LogUtils.e(TAG, "invalid deviceId");
            return;
        }
        if (mBindStateData.getValue() != null && mBindStateData.getValue().getStatus() == HttpRequestState.Status.START) {
            LogUtils.e(TAG, "is binding");
            return;
        }
        mNetConfigHelper.bindDevice(devId, devId2, mBindStateData);
        mBindingDeviceId = devId;
    }

    public int sendNetConfigInfoViaAP(String deviceId) {
        NetConfigInfo netConfigInfo = getNetConfigInfo();
        APNetConfig apNetConfig = IoTVideoSdk.getNetConfig().newAPNetConfig();
        String netConfigString = apNetConfig.toNetConfigString(netConfigInfo);
        apNetConfig.sendMessage(deviceId, netConfigString, new IResultListener<DataMessage>() {
            @Override
            public void onStart() {
                LogUtils.i(TAG, "sendNetConfigInfoViaAP to device " + deviceId);
            }

            @Override
            public void onSuccess(DataMessage msg) {
                LogUtils.i(TAG, "sendNetConfigInfoViaAP onSuccess : " + msg);
                if (isMainThread()) {
                    mSendNetConfigInfoViaAPData.setValue(msg);
                } else {
                    mSendNetConfigInfoViaAPData.postValue(msg);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.i(TAG, "sendNetConfigInfoViaAP errorCode : " + errorCode + " " + errorMsg);
                if (isMainThread()) {
                    mSendNetConfigInfoViaAPData.setValue(new DataMessage(0, 101, errorCode, errorMsg.getBytes()));
                } else {
                    mSendNetConfigInfoViaAPData.postValue(new DataMessage(0, 101, errorCode, errorMsg.getBytes()));
                }
            }
        });
        return 0;
    }
}
