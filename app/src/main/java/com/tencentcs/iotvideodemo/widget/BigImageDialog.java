package com.tencentcs.iotvideodemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.tencentcs.iotvideodemo.R;

public class BigImageDialog extends Dialog {
    private ImageView mImageView;

    public BigImageDialog(Context context) {
        super(context);
        initView(context);
    }

    public BigImageDialog(Context context, Bitmap bitmap) {
        super(context, R.style.DialogFullscreen);
        initView(context);
        loadBitmap(bitmap);
    }

    private void initView(Context context) {
        setContentView(R.layout.dialog_big_image);
        setCanceledOnTouchOutside(true);
        mImageView = findViewById(R.id.image_view);
    }

    public void loadBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        mImageView.setImageBitmap(bitmap);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}

