package com.tencentcs.iotvideodemo.test;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.videoplayer.CustomCaptureActivity;
import com.tencentcs.iotvideodemo.videoplayer.CustomCaptureActivity2;
import com.tencentcs.zxing.CaptureActivity;
import com.tencentcs.zxing.util.CodeUtils;

import androidx.annotation.Nullable;

public class TestQRCodeActivity extends BaseActivity {
    private static final String TAG = "TestQRCodeActivity";

    private static final int SCAN_QR_CODE_REQUEST = 1;

    private EditText mInputQRCodeText;
    private TextView mScanResultTextView;
    private ImageView mQRCodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_qr_code);
        mInputQRCodeText = findViewById(R.id.input_qrcode_text);
        mQRCodeImage = findViewById(R.id.qrcode_image);
        mScanResultTextView = findViewById(R.id.scan_result);
        findViewById(R.id.create_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createQRCodeAndDisplay(mInputQRCodeText.getText().toString());
            }
        });
        findViewById(R.id.scan_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCaptureActivity();
            }
        });
        findViewById(R.id.scan_qrcode2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCaptureActivity2();
            }
        });
    }

    private void createQRCodeAndDisplay(String text) {
        Bitmap bitmap = CodeUtils.createQRCode(text, 500);
        mQRCodeImage.setImageBitmap(bitmap);
    }

    private void startCaptureActivity() {
        requestPermissions(new OnPermissionsListener() {
            @Override
            public void OnPermissions(boolean granted) {
                if (granted) {
                    Intent intent = new Intent(TestQRCodeActivity.this, CustomCaptureActivity.class);
                    startActivity(intent);
                }
            }
        }, Manifest.permission.CAMERA);
    }

    private void startCaptureActivity2() {
        requestPermissions(new OnPermissionsListener() {
            @Override
            public void OnPermissions(boolean granted) {
                if (granted) {
                    Intent intent = new Intent(TestQRCodeActivity.this, CustomCaptureActivity2.class);
                    startActivityForResult(intent, SCAN_QR_CODE_REQUEST);
                }
            }
        }, Manifest.permission.CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_QR_CODE_REQUEST) {
            if (data != null) {
                String scanResult = data.getStringExtra(CaptureActivity.KEY_RESULT);
                LogUtils.i(TAG, "scan result = " + scanResult);
                mScanResultTextView.setText(scanResult);
            }
        }
    }
}
