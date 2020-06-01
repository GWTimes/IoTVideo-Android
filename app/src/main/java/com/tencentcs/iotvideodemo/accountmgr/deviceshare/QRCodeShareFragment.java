package com.tencentcs.iotvideodemo.accountmgr.deviceshare;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.qrcode.QRCode;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.zxing.util.CodeUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class QRCodeShareFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "QRCodeShareFragment";

    private ImageView mQRCodeImage;
    private TextView mQRCodeTextView;
    private DeviceShareViewModel mDeviceShareViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQRCodeImage = view.findViewById(R.id.qrcode_image);
        mQRCodeTextView = view.findViewById(R.id.qrcode_text);
        view.findViewById(R.id.fresh_qrcode).setOnClickListener(this);
        initViewModel();
    }

    private void initViewModel() {
        mDeviceShareViewModel = ViewModelProviders.of(getActivity()).get(DeviceShareViewModel.class);
        mDeviceShareViewModel.getGenShareQRCodeData().observe(getActivity(), new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState httpRequestState) {
                switch (httpRequestState.getStatus()) {
                    case ERROR:
                        Snackbar.make(mQRCodeImage, httpRequestState.getStatusTip(), Snackbar.LENGTH_LONG).show();
                        break;
                    case SUCCESS:
                        GenShareQRCodeResult result = JSONUtils.JsonToEntity(httpRequestState.getJsonObject().toString(), GenShareQRCodeResult.class);
                        QRCode qrCode = new QRCode();
                        qrCode.shareToken = String.valueOf(result.getData().getQrcodeToken());
                        createQRCodeAndDisplay(qrCode.toQRContentString());
                        LogUtils.i(TAG, "share device QRCode = " + qrCode.toString());
                        mQRCodeTextView.setText(String.format("%s\n%s", qrCode.toQRContentString(), qrCode.toString()));
                        break;
                }
            }
        });
        mDeviceShareViewModel.genShareQrcode();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fresh_qrcode) {
            mDeviceShareViewModel.genShareQrcode();
        }
    }

    private void createQRCodeAndDisplay(String text) {
        Bitmap bitmap = CodeUtils.createQRCode(text, 800);
        mQRCodeImage.setImageBitmap(bitmap);
    }
}
