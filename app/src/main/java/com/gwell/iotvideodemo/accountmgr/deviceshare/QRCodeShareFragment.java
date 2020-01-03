package com.gwell.iotvideodemo.accountmgr.deviceshare;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.gwell.http.SubscriberListener;
import com.gwell.http.utils.HttpUtils;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.utils.qrcode.QRCode;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceList;
import com.gwell.iotvideodemo.base.BaseFragment;
import com.gwell.zxing.util.CodeUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class QRCodeShareFragment extends BaseFragment implements View.OnClickListener {
    private ImageView mQRCodeImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQRCodeImage = view.findViewById(R.id.qrcode_image);
        view.findViewById(R.id.fresh_qrcode).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fresh_qrcode:
                genShareQrcode();
                break;
        }
    }

    private void genShareQrcode() {
        DeviceViewModel deviceViewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);
        DeviceList.Device device = deviceViewModel.getDevice();
        AccountMgr.getInstance().genShareQrcode(device.getDevId(), device.getDeviceName(), device.getDeviceName(), new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(JsonObject response) {
                GenShareQRCodeResult result = HttpUtils.JsonToEntity(response.toString(), GenShareQRCodeResult.class);
                QRCode qrCode = new QRCode(QRCode.FUNCTION_SHARE_DEVICE);
                qrCode.setShareToken(result.getData().getQrcodeToken());
                createQRCodeAndDisplay(qrCode.toQRContent());
            }

            @Override
            public void onFail(Throwable e) {
                Snackbar.make(mQRCodeImage, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void createQRCodeAndDisplay(String text) {
        Bitmap bitmap = CodeUtils.createQRCode(text, 500);
        mQRCodeImage.setImageBitmap(bitmap);
    }
}
