package com.tencentcs.iotvideodemo.videoplayer.ijkplayer;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.videoplayer.ijkplayer.media.IRenderView;
import com.tencentcs.iotvideodemo.videoplayer.ijkplayer.media.IjkVideoView;
import com.tencentcs.iotvideodemo.videoplayer.ijkplayer.media.PlayerManager;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class IjkPlayerActivity extends BaseActivity {
    private static final String TAG = "IjkPlayerActivity";

    private IjkVideoView mVideoView;
    private TextView mTvUrl, mTvPlayStatus;
    private PlayerManager mPlayer;

    private String mFileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk_player);

        mVideoView = (IjkVideoView) findViewById(R.id.video_view);

        mFileUrl = getIntent().getStringExtra("URI");
        if (!TextUtils.isEmpty(mFileUrl)) {
            mFileUrl = mFileUrl.replace("https://", "http://");
        }
        LogUtils.i(TAG, "mFileUrl = " + mFileUrl);
        mTvUrl = findViewById(R.id.tv_url);
        mTvUrl.setText(mFileUrl);
        mTvPlayStatus = findViewById(R.id.tv_play_status);
        mTvPlayStatus.setText("加载中...");

        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mTvPlayStatus.setText("播放中");
            }
        });

        /** 普通播放 start **/
        mVideoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        mVideoView.setVideoURI(Uri.parse(mFileUrl));
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
