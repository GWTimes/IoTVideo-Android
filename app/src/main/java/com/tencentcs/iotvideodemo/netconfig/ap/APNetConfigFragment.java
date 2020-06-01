package com.tencentcs.iotvideodemo.netconfig.ap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.IoTVideoError;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.DataMessage;
import com.tencentcs.iotvideo.netconfig.DeviceInfo;
import com.tencentcs.iotvideo.netconfig.NetConfigInfo;
import com.tencentcs.iotvideo.netconfig.NetConfigResult;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.Utils;
import com.tencentcs.iotvideo.utils.qrcode.QRCode;
import com.tencentcs.iotvideo.utils.qrcode.QRCodeHelper;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModel;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModelFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class APNetConfigFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "APNetConfigFragment";

    private static final String AP_FIXED = "jwsd-";

    private enum APNetConfigState {
        NotConnectAp, ConnectAp, SearchingDevice, FindDevice, NotFindDevice, SendingNetInfo, SendNetInfoError,
        WaitingDeviceOnline, Binding, BindError, End
    }

    private NetConfigViewModel mNetConfigInfoViewModel;
    private TextView mTvNetConfigInfo;
    private TextView mTvDeviceInfo;
    private TextView mTvNetConfigState;
    private Button mBtnSendNetConfigInfo;

    private DeviceInfo mLanDeviceInfo;
    private APNetConfigState mNetConfigState = APNetConfigState.NotConnectAp;
    private NetWorkStateReceiver mNetWorkStateReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ap_net_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvNetConfigInfo = view.findViewById(R.id.net_config_info);
        mTvDeviceInfo = view.findViewById(R.id.device_info);
        mTvNetConfigState = view.findViewById(R.id.net_config_state);
        mBtnSendNetConfigInfo = view.findViewById(R.id.send_net_config_info);
        mNetConfigInfoViewModel = ViewModelProviders.of(getActivity(), new NetConfigViewModelFactory())
                .get(NetConfigViewModel.class);
        mTvNetConfigInfo.setText(R.string.getting_netcofing_token);
        mNetConfigInfoViewModel.getNetConfigInfoLiveData().observe(getActivity(), new Observer<NetConfigInfo>() {
            @Override
            public void onChanged(NetConfigInfo netConfigInfo) {
                if (!TextUtils.isEmpty(netConfigInfo.getNetMatchId())) {
                    createQRCodeAndDisplay(netConfigInfo);
                    mTvNetConfigState.setVisibility(View.VISIBLE);
                    mBtnSendNetConfigInfo.setVisibility(View.VISIBLE);
                }
            }
        });
        mNetConfigInfoViewModel.getSendNetConfigInfoViaAPData().observe(getActivity(), new Observer<DataMessage>() {
            @Override
            public void onChanged(DataMessage msg) {
                LogUtils.i(TAG, "send ap net config result " + msg);
                if (msg.error != 0) {
                    updateNetConfigState(APNetConfigState.SendNetInfoError);
                } else {
                    updateNetConfigState(APNetConfigState.WaitingDeviceOnline);
                }
            }
        });
        mNetConfigInfoViewModel.getLanDeviceData().observe(getActivity(), new Observer<DeviceInfo[]>() {
            @Override
            public void onChanged(DeviceInfo[] deviceInfos) {
                if (deviceInfos != null && deviceInfos.length > 0) {
                    mLanDeviceInfo = deviceInfos[0];
                    updateNetConfigState(APNetConfigState.FindDevice);
                } else {
                    updateNetConfigState(APNetConfigState.NotFindDevice);
                }
            }
        });
        mNetConfigInfoViewModel.getDeviceOnlineData().observe(getActivity(), new Observer<NetConfigResult>() {
            @Override
            public void onChanged(NetConfigResult result) {
                if (result != null && result.getData() != null) {int errorCode = result.getData().getErrorcode();
                    if (errorCode != 0 && errorCode != IoTVideoError.ASrv_binderror_dev_has_bind_other) {
                        updateNetConfigState(APNetConfigState.End);
                        mTvNetConfigState.setText(String.format("设备已联网，但无法绑定 : %s", Utils.getErrorDescription(errorCode)));
                    }
                }
            }
        });
        mNetConfigInfoViewModel.getBindStateData().observe(getActivity(), new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState httpRequestState) {
                switch (httpRequestState.getStatus()) {
                    case START:
                        updateNetConfigState(APNetConfigState.Binding);
                        break;
                    case SUCCESS:
                        updateNetConfigState(APNetConfigState.End);
                        break;
                    case ERROR:
                        updateNetConfigState(APNetConfigState.BindError);
                        break;
                }
            }
        });
        mBtnSendNetConfigInfo.setOnClickListener(this);
        mTvNetConfigState.setOnClickListener(this);
        registerNetBroadcastReceiver(getActivity());
        updateDeviceInfoText();
        if (mLanDeviceInfo != null) {
            mTvDeviceInfo.setText(String.format("deviceId : %s", mLanDeviceInfo.tencentID));
        }
    }

    @Override
    public void onDestroyView() {
        LogUtils.i(TAG, "unregister NetBroadcastReceiver");
        getActivity().unregisterReceiver(mNetWorkStateReceiver);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_net_config_info) {
            if (mLanDeviceInfo == null) {
                LogUtils.i(TAG, "can not find device, please confirm had connected to device AP!");
                return;
            }
            if (mNetConfigState.ordinal() < APNetConfigState.FindDevice.ordinal()) {
                Snackbar.make(v, "无法发送配网信息", Snackbar.LENGTH_SHORT).show();
                return;
            }
            updateNetConfigState(APNetConfigState.SendingNetInfo);
            int sendResult = mNetConfigInfoViewModel.sendNetConfigInfoViaAP(mLanDeviceInfo.tencentID);
            if (sendResult == -1 && isVisible()) {
                Snackbar.make(v, "无法发送配网信息", Snackbar.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.net_config_state) {
            if (mNetConfigState == APNetConfigState.NotFindDevice || mNetConfigState == APNetConfigState.SendNetInfoError) {
                updateNetConfigState(APNetConfigState.SearchingDevice);
                mNetConfigInfoViewModel.findDevice();
            } else if (mNetConfigState == APNetConfigState.BindError) {
                mNetConfigInfoViewModel.rebindDevice();
            }
        }
    }

    private void createQRCodeAndDisplay(NetConfigInfo netConfigInfo) {
        String netConfigString = IoTVideoSdk.getNetConfig().newAPNetConfig().toNetConfigString(netConfigInfo);

        QRCode analyseQrCode = QRCodeHelper.analyse(netConfigString);

        mTvNetConfigInfo.setText(String.format("%s\n\n%s", netConfigString, analyseQrCode.toString()));
    }

    private void updateNetConfigState(APNetConfigState state) {
        if (mNetConfigState == APNetConfigState.End) {
            LogUtils.e(TAG, "net config is ending");
            return;
        }
        if (mNetConfigState.ordinal() >= APNetConfigState.Binding.ordinal() &&
                state.ordinal() < APNetConfigState.Binding.ordinal()) {
            LogUtils.e(TAG, "the device has connected to network, app has nothing to do but binding");
            return;
        }
        if (mNetConfigState.ordinal() >= APNetConfigState.WaitingDeviceOnline.ordinal() &&
                state.ordinal() < APNetConfigState.WaitingDeviceOnline.ordinal()) {
            LogUtils.e(TAG, "the net info has sent to device, app has nothing to do but waiting it online");
            return;
        }
        if (state != mNetConfigState) {
            mNetConfigState = state;
            LogUtils.i(TAG, "updateNetConfigState " + mNetConfigState);
            updateDeviceInfoText();
        }
    }

    private void updateDeviceInfoText() {
        switch (mNetConfigState) {
            case NotConnectAp:
                mTvDeviceInfo.setText("");
                mTvNetConfigState.setText("请连接设备热点");
                break;
            case ConnectAp:
                mTvDeviceInfo.setText("");
                mTvNetConfigState.setText("已连接设备热点");
                updateNetConfigState(APNetConfigState.SearchingDevice);
                mNetConfigInfoViewModel.findDevice();
                break;
            case SearchingDevice:
                mTvDeviceInfo.setText("");
                mTvNetConfigState.setText("正在搜索设备信息...");
                break;
            case FindDevice:
                mTvDeviceInfo.setText(String.format("deviceId : %s", mLanDeviceInfo.tencentID));
                mTvNetConfigState.setText("请发送配网信息");
                break;
            case NotFindDevice:
                mTvDeviceInfo.setText("");
                mTvNetConfigState.setText("未找到设备，请点击重新搜索");
                break;
            case SendingNetInfo:
                mTvNetConfigState.setText("正在发送配网信息...");
                break;
            case SendNetInfoError:
                mTvNetConfigState.setText("配网信息发送失败，请重新发送或点击重新搜索设备");
                break;
            case WaitingDeviceOnline:
                mTvNetConfigState.setText("配网消息发送成功，等待设备联网...\n在此期间，请确保手机已连接可用网络");
                break;
            case Binding:
                mTvNetConfigState.setText("正在绑定设备...");
                break;
            case BindError:
                mTvNetConfigState.setText("绑定失败，点击重新绑定");
                break;
            case End:
                mTvNetConfigState.setText("设备已绑定，流程结束");
                break;
        }
    }

    private void registerNetBroadcastReceiver(@NonNull Context context) {
        LogUtils.i(TAG, "registerNetBroadcastReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetWorkStateReceiver = new NetWorkStateReceiver();
        context.registerReceiver(mNetWorkStateReceiver, filter);
    }

    class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                @NonNull String wifiName = getWifiName(context);
                LogUtils.i(TAG, "network state changed = " + wifiName);
                if (mNetConfigState == APNetConfigState.NotConnectAp) {
                    if (!TextUtils.isEmpty(wifiName)) {
                        updateNetConfigState(APNetConfigState.ConnectAp);
                    }
                } else if (mNetConfigState.ordinal() < APNetConfigState.WaitingDeviceOnline.ordinal()) {
                    if (TextUtils.isEmpty(wifiName)) {
                        updateNetConfigState(APNetConfigState.NotConnectAp);
                    }
                }
            }
        }

        private String getWifiName(@NonNull Context context) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                return "";
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null) {
                return "";
            }
            String ssid = wifiInfo.getSSID();
            if (ssid.length() <= 0) {
                return "";
            }
            int a = ssid.charAt(0);
            if (a == 34) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            if ("<unknown ssid>".equals(ssid)) {
                ssid = "";
            }

            return ssid;
        }
    }
}
