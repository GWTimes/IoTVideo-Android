package com.gwell.iotvideodemo.vas;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.gwell.http.SubscriberListener;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideo.vas.vas;
import com.gwell.iotvideo.vas.VasService;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseActivity;

public class VasActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "VasActivity";

    private RadioGroup mRGChannel;

    private VasService mVasService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vas);
        mRGChannel = findViewById(R.id.rg_group);
        mRGChannel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                LogUtils.i(TAG, "onCheckedChanged i = " + i);
                if (i == R.id.rb_p2p) {
                    mVasService = vas.getVasService(vas.VIA_P2P);
                } else if (i == R.id.rb_http) {
                    mVasService = vas.getVasService(vas.VIA_HTTP);
                }
            }
        });
        findViewById(R.id.test).setOnClickListener(this);
        mVasService = vas.getVasService();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.test) {
            mVasService.cloudStoragePlayback("devId", 28800, System.currentTimeMillis(), new SubscriberListener() {
                @Override
                public void onStart() {
                    LogUtils.i(TAG, "cloudStoragePlayback start");
                }

                @Override
                public void onSuccess(JsonObject response) {
                    LogUtils.i(TAG, "cloudStoragePlayback onSuccess " + response.toString());
                    Snackbar.make(mRGChannel, response.toString(), Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onFail(Throwable e) {
                    LogUtils.i(TAG, "cloudStoragePlaybackMain onFail " + e.getMessage());
                    Snackbar.make(mRGChannel, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
