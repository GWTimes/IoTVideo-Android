package com.tencentcs.iotvideodemo.netconfig.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencentcs.iotvideo.IoTVideoError;
import com.tencentcs.iotvideo.IoTVideoSdk;
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
import com.tencentcs.iotvideodemo.widget.BigImageDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class QRCodeNetConfigFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "QRCodeNetConfigFragment";

    private enum QRCodeNetConfigState {
        WaitingDeviceOnline, Binding, BindError, End
    }

    private TextView mTvNetConfigInfo;
    private TextView mTvDeviceInfo;
    private ImageView mQRCodeImage;
    private NetConfigViewModel mNetConfigInfoViewModel;
    private BigImageDialog mBigImageDialog;
    private QRCodeNetConfigState mNetConfigState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode_net_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvNetConfigInfo = view.findViewById(R.id.net_config_info);
        mQRCodeImage = view.findViewById(R.id.qrcode_image);
        mTvDeviceInfo = view.findViewById(R.id.device_info);
        mTvDeviceInfo.setOnClickListener(this);
        mNetConfigInfoViewModel = ViewModelProviders.of(getActivity(), new NetConfigViewModelFactory())
                .get(NetConfigViewModel.class);
        mTvNetConfigInfo.setText(R.string.getting_netcofing_token);
        mNetConfigInfoViewModel.getNetConfigInfoLiveData().observe(getActivity(), new Observer<NetConfigInfo>() {
            @Override
            public void onChanged(NetConfigInfo netConfigInfo) {
                if (!TextUtils.isEmpty(netConfigInfo.getNetMatchId())) {
                    createQRCodeAndDisplay(netConfigInfo);
                }
            }
        });
        mNetConfigInfoViewModel.getDeviceOnlineData().observe(getActivity(), new Observer<NetConfigResult>() {
            @Override
            public void onChanged(NetConfigResult result) {
                if (result != null && result.getData() != null) {
                    int errorCode = result.getData().getErrorcode();
                    if (errorCode != 0 && errorCode != IoTVideoError.ASrv_binderror_dev_has_bind_other) {
                        updateNetConfigState(QRCodeNetConfigState.End);
                        mTvDeviceInfo.setText(String.format("设备已联网，但无法绑定 : %s", Utils.getErrorDescription(errorCode)));
                    }
                }
            }
        });
        mNetConfigInfoViewModel.getBindStateData().observe(getActivity(), new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState httpRequestState) {
                switch (httpRequestState.getStatus()) {
                    case START:
                        updateNetConfigState(QRCodeNetConfigState.Binding);
                        break;
                    case SUCCESS:
                        updateNetConfigState(QRCodeNetConfigState.End);
                        break;
                    case ERROR:
                        updateNetConfigState(QRCodeNetConfigState.BindError);
                        break;
                }
            }
        });
        updateNetConfigState(QRCodeNetConfigState.WaitingDeviceOnline);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.device_info) {
            if (mNetConfigState == QRCodeNetConfigState.BindError) {
                mNetConfigInfoViewModel.rebindDevice();
            }
        }
    }

    private void createQRCodeAndDisplay(NetConfigInfo netConfigInfo) {
        QRCode qrCode = IoTVideoSdk.getNetConfig().newQRCodeNetConfig().createQRCode(netConfigInfo);
        final Bitmap bitmap = qrCode.toBitmap(800);
        if (bitmap != null) {
            mQRCodeImage.setImageBitmap(bitmap);
            mQRCodeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBigQrCodeDialog(bitmap);
                }
            });
            String netConfigString = qrCode.toQRContentString();

            QRCode analyseQrCode = QRCodeHelper.analyse(netConfigString);

            mTvNetConfigInfo.setText(netConfigString);
            LogUtils.i(TAG, "createQRCodeAndDisplay " + String.format("%s\n%s", netConfigString, analyseQrCode.toString()));
        }
        mTvDeviceInfo.setVisibility(View.VISIBLE);
    }

    private void showBigQrCodeDialog(Bitmap bitmap) {
        if (mBigImageDialog == null) {
            mBigImageDialog = new BigImageDialog(getActivity(), bitmap);
        }
        if (!mBigImageDialog.isShowing()) {
            mBigImageDialog.show();
        } else {
            mBigImageDialog.dismiss();
        }
    }

    private void updateNetConfigState(QRCodeNetConfigState state) {
        if (mNetConfigState == QRCodeNetConfigState.End) {
            LogUtils.e(TAG, "net config is ending");
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
            case WaitingDeviceOnline:
                mTvDeviceInfo.setText("等待设备联网...");
                break;
            case Binding:
                mTvDeviceInfo.setText("正在绑定设备...");
                break;
            case BindError:
                mTvDeviceInfo.setText("绑定失败，点击重新绑定");
                break;
            case End:
                mTvDeviceInfo.setText("设备已绑定，流程结束");
                break;
        }
    }
}
