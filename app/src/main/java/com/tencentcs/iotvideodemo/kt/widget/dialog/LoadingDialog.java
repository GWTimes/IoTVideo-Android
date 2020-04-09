package com.tencentcs.iotvideodemo.kt.widget.dialog;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.tencentcs.iotvideodemo.R;


public class LoadingDialog extends Dialog {
    private Context mContext;
    private View mProgressView;

    public LoadingDialog(Context context) {
        super(context);
        initDialog(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        initDialog(context);
    }

    private void initDialog(Context ctx) {
        mContext = ctx;
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_loading, null);
        mProgressView = findViewById(R.id.progress_login);
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
        showProgress(true);
    }

    public void show(String msgText) {
        show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        showProgress(false);
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = mContext.getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}
