package com.gwell.iotvideodemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public void show() {
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        super.show();
    }
}

