package com.gwell.iotvideodemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.gwell.iotvideodemo.R;

public class BigImageDialog extends Dialog {
    private ImageView mIvQrCode;

    public BigImageDialog(Context context, Bitmap qrCode) {
        super(context, R.style.DialogFullscreen);
        initView(context, qrCode);
    }

    private void initView(Context context, Bitmap qrCode) {
        setContentView(R.layout.dialog_big_qr_code);
        setCanceledOnTouchOutside(true);
        mIvQrCode = findViewById(R.id.iv_qr_code);
        loadQrCodeBitmap(context, qrCode);
    }

    private void loadQrCodeBitmap(Context context, Bitmap qrCodeBitmap) {
        if (qrCodeBitmap == null) {
            return;
        }
        mIvQrCode.setImageBitmap(qrCodeBitmap);
        mIvQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}

