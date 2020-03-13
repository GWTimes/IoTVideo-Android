package com.tencentcs.iotvideodemo.videoplayer;

import android.view.View;
import android.widget.Button;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.zxing.CaptureActivity;

public class CustomCaptureActivity2 extends CaptureActivity implements View.OnClickListener {
    private static final String TAG = "CustomCaptureActivity2";
    private Button mPreviewControl;
    private Button mTorchControl;
    private boolean mIsPreviewing = true;
    private boolean mIsTorching = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_custom_capture;
    }

    @Override
    public boolean isContentView(int layoutId) {
        return true;
    }

    @Override
    public void initUI() {
        super.initUI();
        mPreviewControl = findViewById(R.id.preview_control);
        mTorchControl = findViewById(R.id.torch_control);
        mPreviewControl.setOnClickListener(this);
        mTorchControl.setOnClickListener(this);
    }

    @Override
    public int getSurfaceViewId() {
        return R.id.surfaceView;
    }

    @Override
    public int getIvTorchId() {
        return 0;
    }

    @Override
    public int getViewfinderViewId() {
        return R.id.viewfinderView;
    }

    @Override
    public boolean onResultCallback(String result) {
        LogUtils.i(TAG, "onResultCallback = " + result);
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preview_control:
                if (mIsPreviewing) {
                    getCameraManager().stopPreview();
                    mPreviewControl.setText(R.string.start_preview);
                    mIsPreviewing = false;
                } else {
                    getCameraManager().startPreview();
                    mPreviewControl.setText(R.string.stop_preview);
                    mIsPreviewing = true;
                }
                break;
            case R.id.torch_control:
                if (mIsTorching) {
                    getCameraManager().setTorch(false);
                    mTorchControl.setText(R.string.start_torch);
                    mIsTorching = false;
                } else {
                    getCameraManager().setTorch(true);
                    mTorchControl.setText(R.string.stop_torch);
                    mIsTorching = true;
                }
                break;
        }
    }
}
