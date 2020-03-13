package com.tencentcs.iotvideodemo.videoplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.utils.StatusBarUtils;
import com.tencentcs.zxing.CaptureHelper;
import com.tencentcs.zxing.Intents;
import com.tencentcs.zxing.OnCaptureCallback;
import com.tencentcs.zxing.ViewfinderView;

import androidx.annotation.Nullable;

public class CustomCaptureActivity extends BaseActivity implements OnCaptureCallback, View.OnClickListener {
    private static final String TAG = "CustomCaptureActivity";

    private SurfaceView mSurfaceView;
    private ViewfinderView mViewfinderView;
    private Button mPreviewControl;
    private Button mTorchControl;
    private CaptureHelper mCaptureHelper;
    private boolean mIsPreviewing = true;
    private boolean mIsTorching = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_capture);
        StatusBarUtils.setColor(this, android.R.color.transparent, 1);
        mSurfaceView = findViewById(R.id.surfaceView);
        mViewfinderView = findViewById(R.id.viewfinderView);
        mPreviewControl = findViewById(R.id.preview_control);
        mTorchControl = findViewById(R.id.torch_control);
        mCaptureHelper = new CaptureHelper(this, mSurfaceView, mViewfinderView);
        mCaptureHelper.setOnCaptureCallback(this);
        mCaptureHelper.onCreate();
//        mCaptureHelper.continuousScan(true);//支持连扫

        mPreviewControl.setOnClickListener(this);
        mTorchControl.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        LogUtils.i(TAG, "onResume");
        super.onResume();
        if (mCaptureHelper == null) {
            mCaptureHelper = new CaptureHelper(this, mSurfaceView, mViewfinderView);
            mCaptureHelper.setOnCaptureCallback(this);
            mCaptureHelper.onCreate();
        }
        mCaptureHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCaptureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onResultCallback(String result) {
        Intent intent = new Intent();
        intent.putExtra(Intents.Scan.RESULT, result);
        setResult(Activity.RESULT_OK, intent);
        finish();

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preview_control:
                if (mIsPreviewing) {
                    mCaptureHelper.getCameraManager().stopPreview();
                    mPreviewControl.setText(R.string.start_preview);
                    mIsPreviewing = false;
                } else {
                    mCaptureHelper.getCameraManager().startPreview();
                    mPreviewControl.setText(R.string.stop_preview);
                    mIsPreviewing = true;
                }
                break;
            case R.id.torch_control:
                if (mIsTorching) {
                    mCaptureHelper.getCameraManager().setTorch(false);
                    mTorchControl.setText(R.string.start_torch);
                    mIsTorching = false;
                } else {
                    mCaptureHelper.getCameraManager().setTorch(true);
                    mTorchControl.setText(R.string.stop_torch);
                    mIsTorching = true;
                }
                break;
        }
    }
}
