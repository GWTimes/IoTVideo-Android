package com.tencentcs.iotvideodemo.netconfig.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.DataMessage;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideo.netconfig.NetConfigInfo;
import com.tencentcs.iotvideo.netconfig.data.NetMatchTokenResult;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.qrcode.QRCode;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModel;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModelFactory;
import com.tencentcs.iotvideodemo.widget.BigImageDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class QRCodeNetConfigFragment extends BaseFragment {
    private static final String TAG = "QRCodeNetConfigFragment";

    private TextView mTvNetConfigInfo;
    private ImageView mQRCodeImage;
    private NetConfigViewModel mNetConfigInfoViewModel;
    private BigImageDialog mBigImageDialog;

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
        view.findViewById(R.id.create_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNetMatchIdAndCreateQRCode();
            }
        });
        mNetConfigInfoViewModel = ViewModelProviders.of(getActivity(), new NetConfigViewModelFactory())
                .get(NetConfigViewModel.class);
        NetConfigInfo netConfigInfo = mNetConfigInfoViewModel.getNetConfigInfo();
        mTvNetConfigInfo.setText(netConfigInfo.toString());
        getNetMatchIdAndCreateQRCode();
    }

    private void createQRCodeAndDisplay(String netConfigToken) {
        NetConfigInfo netConfigInfo = mNetConfigInfoViewModel.getNetConfigInfo();
        QRCode qrCode = IoTVideoSdk.getNetConfig().newQRCodeNetConfig().createQRCode(netConfigToken,
                netConfigInfo.getWifiName(), netConfigInfo.getWifiPassword(), netConfigInfo.getEncType());
        final Bitmap bitmap = qrCode.toBitmap(800);
        if (bitmap != null) {
            mQRCodeImage.setImageBitmap(bitmap);
            mQRCodeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBigQrCodeDialog(bitmap);
                }
            });
            mTvNetConfigInfo.setText(qrCode.toString());
        }
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

    private void getNetMatchIdAndCreateQRCode() {
        mNetConfigInfoViewModel.getNetConfigToken(new IResultListener<DataMessage>() {
            @Override
            public void onStart() {
                LogUtils.i(TAG, "getNetConfigToken start");
            }

            @Override
            public void onSuccess(DataMessage msg) {
                LogUtils.i(TAG, "getNetConfigToken onSuccess : " + msg);
                byte[] token = msg.data;
                if (token != null) {
                    String tokenStr = new String(token);
                    NetMatchTokenResult result = JSONUtils.JsonToEntity(tokenStr, NetMatchTokenResult.class);
                    createQRCodeAndDisplay(result.getToken());
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.i(TAG, "getNetConfigToken errorCode : " + errorCode + " " + errorMsg);
                Snackbar.make(mQRCodeImage, errorCode + " " + errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
