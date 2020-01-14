package com.gwell.iotvideodemo.videoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseActivity;
import com.gwell.iotvideodemo.videoplayer.media.IRenderView;
import com.gwell.iotvideodemo.videoplayer.media.IjkVideoView;
import com.gwell.iotvideodemo.videoplayer.media.PlayerManager;

public class IjkPlayerActivity extends BaseActivity {
    private static final String TAG = "IjkPlayerActivity";

    private IjkVideoView mVideoView;
    private PlayerManager mPlayer;

    private String mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_player);

        mVideoView = (IjkVideoView) findViewById(R.id.video_view);

        mUri = getIntent().getStringExtra("URI");
        if (!TextUtils.isEmpty(mUri)) {
            mUri = mUri.replace("https://", "http://");
        }
        LogUtils.i(TAG, "mUri = " + mUri);

        /** 普通播放 start **/
        mVideoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        mVideoView.setVideoURI(Uri.parse(mUri));
        mVideoView.start();
        /** 普通播放 end **/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mVideoView != null){
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoView != null){
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mVideoView != null){
            mVideoView.releaseWithoutStop();
        }
    }
}
