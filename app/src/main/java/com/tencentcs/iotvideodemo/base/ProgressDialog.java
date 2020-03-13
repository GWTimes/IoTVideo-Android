package com.tencentcs.iotvideodemo.base;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.tencentcs.iotvideodemo.R;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

public class ProgressDialog extends Dialog {
    private TextView mProgressTip;
    private TimerTask mTimerTask;
    private Timer mTimeOutTimer;
    private int mTimeOut = 8 * 1000;
    private OnTimeOutListener mOnTimeOutListener;

    public ProgressDialog(@NonNull Context context) {
        this(context, R.style.DialogProgress);
        initView();
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_progress);
        setCanceledOnTouchOutside(false);
        mProgressTip = findViewById(R.id.progress_tip);
        mTimeOutTimer = new Timer();
    }

    @Override
    public void show() {
        super.show();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (null != mOnTimeOutListener) {
                    mOnTimeOutListener.onTimeOut(ProgressDialog.this);
                }
                dismiss();
            }
        };
        if (null == mTimeOutTimer) {
            mTimeOutTimer = new Timer();
        }
        mTimeOutTimer.schedule(mTimerTask, mTimeOut);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (null != mTimerTask) {
            mTimerTask.cancel();
        }
        mTimeOutTimer.purge();
    }

    public void setProgressTip(String msg) {
        mProgressTip.setText(msg);
    }

    public void setOnTimeOutListener(OnTimeOutListener listener, int timeOut) {
        mOnTimeOutListener = listener;
        mTimeOut = timeOut;
    }

    public interface OnTimeOutListener {
        void onTimeOut(ProgressDialog dialog);
    }
}
