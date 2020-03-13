package com.tencentcs.iotvideodemo.kt.widget.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.tencentcs.iotvideodemo.R;


public class LoadingDialog extends Dialog {

    private LottieAnimationView mLottie;

    public LoadingDialog(Context context) {
        super(context);
        initDialog(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        initDialog(context);
    }

    private void initDialog(Context ctx) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_loading, null);
        //清理背景变暗
        mLottie = view.findViewById(R.id.lottie);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        }
//        final float scale = ctx.getResources().getDisplayMetrics().density;
//        int width = (int) (100 * scale + 0.5f);
//        int height = (int) (100 * scale + 0.5f);
        int width = FrameLayout.LayoutParams.MATCH_PARENT;
        int height = FrameLayout.LayoutParams.MATCH_PARENT;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        lp.gravity = Gravity.CENTER;
        this.setContentView(view, lp);
        setCancelable(false);
    }


    @Override
    public void show() {
        super.show();
        mLottie.playAnimation();
    }

    public void show(String msgText) {
        show();
    }

}
