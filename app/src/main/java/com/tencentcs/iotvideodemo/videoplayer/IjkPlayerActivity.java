package com.tencentcs.iotvideodemo.videoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.videoplayer.media.IRenderView;
import com.tencentcs.iotvideodemo.videoplayer.media.IjkVideoView;
import com.tencentcs.iotvideodemo.videoplayer.media.PlayerManager;

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
