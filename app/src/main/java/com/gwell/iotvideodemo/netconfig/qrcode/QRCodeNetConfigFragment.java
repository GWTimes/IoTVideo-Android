package com.gwell.iotvideodemo.netconfig.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.netconfig.NetConfig;
import com.gwell.iotvideo.netconfig.NetConfigInfo;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseFragment;
import com.gwell.iotvideodemo.netconfig.NetConfigViewModel;
import com.gwell.iotvideodemo.netconfig.NetConfigViewModelFactory;
import com.gwell.iotvideodemo.widget.BigImageDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class QRCodeNetConfigFragment extends BaseFragment {

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
                createQRCodeAndDisplay();
            }
        });
        mNetConfigInfoViewModel = ViewModelProviders.of(getActivity(), new NetConfigViewModelFactory())
                .get(NetConfigViewModel.class);
        NetConfigInfo netConfigInfo = mNetConfigInfoViewModel.getNetConfigInfo();
        mTvNetConfigInfo.setText(netConfigInfo.toString());
    }

    private void createQRCodeAndDisplay() {
        NetConfigInfo netConfigInfo = mNetConfigInfoViewModel.getNetConfigInfo();
        final Bitmap bitmap = IoTVideoSdk.getNetConfig().newQRCodeNetConfig().createQRCode(netConfigInfo.getWifiName(), netConfigInfo.getWifiPassword(),
                netConfigInfo.getEncType(), 500);
        if (bitmap != null) {
            mQRCodeImage.setImageBitmap(bitmap);
            mQRCodeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showBigQrCodeDialog(bitmap);
                }
            });
            mTvNetConfigInfo.setText(netConfigInfo.toString());
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
}
