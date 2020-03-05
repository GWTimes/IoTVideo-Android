package com.gwell.iotvideodemo.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideo.utils.rxjava.SubscriberListener;
import com.gwell.iotvideo.vas.VasMgr;
import com.gwell.iotvideo.vas.VasService;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.accountmgr.AccountSPUtils;
import com.gwell.iotvideodemo.base.BaseActivity;
import com.gwell.iotvideodemo.test.preview.CameraActivity;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.start_preview).setOnClickListener(this);
        findViewById(R.id.start_record).setOnClickListener(this);
        findViewById(R.id.start_web_api_activity).setOnClickListener(this);
        findViewById(R.id.start_test_qrcode_activity).setOnClickListener(this);
        findViewById(R.id.test_http_via_p2p).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_preview:
                startPreview();
                break;
            case R.id.start_record:
                startRecord();
                break;
            case R.id.start_web_api_activity:
                startWebApiActivity();
                break;
            case R.id.start_test_qrcode_activity:
                startQRCodeActivity();
                break;
            case R.id.test_http_via_p2p:
                VasService vasService = VasMgr.getVasService();
                Map<String, Object> publicParams = new HashMap<>();
                publicParams.put("tencentCid", "20aea48e985f519539679487802ba59f");
                VasMgr.updatePublicParams(publicParams);
                String userId = AccountSPUtils.getInstance().getString(this, AccountSPUtils.ACCESS_ID, "");
                vasService.register(userId, new SubscriberListener() {
                    @Override
                    public void onStart() {
                        LogUtils.i(TAG, "cloudStorageCreate start");
                        Snackbar.make(v, "发起请求", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(@NonNull JsonObject response) {
                        LogUtils.i(TAG, "cloudStorageCreate onSuccess " + response.toString());
                        Snackbar.make(v, response.toString(), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail(@NonNull Throwable e) {
                        LogUtils.i(TAG, "cloudStorageCreate onFail " + e.getMessage());
                        Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
                break;
        }
    }

    private void startPreview() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.OPERATE_TYPE, CameraActivity.OPERATE_PREVIEW);
        startActivity(intent);
    }

    private void startRecord() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.OPERATE_TYPE, CameraActivity.OPERATE_RECORD);
        startActivity(intent);
    }

    private void startWebApiActivity() {
        Intent intent = new Intent(this, TestWebApiActivity.class);
        startActivity(intent);
    }

    private void startQRCodeActivity() {
        Intent intent = new Intent(this, TestQRCodeActivity.class);
        startActivity(intent);
    }
}
