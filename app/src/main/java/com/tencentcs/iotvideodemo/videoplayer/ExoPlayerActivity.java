package com.tencentcs.iotvideodemo.videoplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;


import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;

public class ExoPlayerActivity extends BaseActivity {

    private ExoUserPlayer mExoPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);

        String uri = getIntent().getStringExtra("URI");
        //支持List列表
//        String [] videoSourceList={"http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
//                "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4",
//                "http://120.25.246.21/vrMobile/travelVideo/zhejiang_xuanchuanpian.mp4"};
//        String[] videoNameList={"超清","高清","标清"};
        mExoPlayerManager =  new VideoPlayerManager.Builder(this, VideoPlayerManager.TYPE_PLAY_GESTURE, R.id.exo_play_context_id)
                .setDataSource(new DataSource(this))
                //加载rtmp 协议视频
                //.setPlayUri("rtmp://live.hkstv.hk.lxdns.com/live/hks")
                //加载m3u8
                //.setPlayUri("http://cctvalih5ca.v.myalicdn.com/live/cctv1_2/index.m3u8")
                //加载ts.文件
                //.setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts")
                //示例本地路径 或者 /storage/emulated/0/DCIM/Camera/xxx.mp4
                //.setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath() + "/video.mp4")
                //.setPlayUri("https://mp4.vjshi.com/2018-09-20/bc8b2ae8678e93a8b5ff87a83378b920.mp4")
                .setPlayUri(uri)
                //播放列表视频
                //.setPlayUri(lists)
                //设置开始播放进度
                //.setPosition(1000)
                //开启线路设置
                //.setShowVideoSwitch(true)
                //.setPlaySwitchUri(0, videoSourceList, videoNameList)
                //开启手势
                .setPlayerGestureOnTouch(true)
                //视频进度回调
                .addOnWindowListener(new VideoWindowListener() {
                    @Override
                    public void onCurrentIndex(int currentIndex, int windowCount) {
                        Toast.makeText(getApplication(), currentIndex + "windowCount:" + windowCount, Toast.LENGTH_SHORT).show();
                    }
                })
                .addVideoInfoListener(new VideoInfoListener() {

                    @Override
                    public void onPlayStart(long currPosition) {

                    }

                    @Override
                    public void onLoadingChanged() {

                    }

                    @Override
                    public void onPlayerError(@Nullable ExoPlaybackException e) {

                    }

                    @Override
                    public void onPlayEnd() {

                    }

                    @Override
                    public void isPlaying(boolean playWhenReady) {

                    }
                }).create().startPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        mExoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mExoPlayerManager.onPause();
    }


    @Override
    protected void onDestroy() {
        mExoPlayerManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mExoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        //使用播放返回键监听
        if (mExoPlayerManager.onBackPressed()) {
            finish();
        }
    }

    public class DataSource implements DataSourceListener {
        public static final String TAG = "OfficeDataSource";

        private Context context;

        public DataSource(Context context) {
            this.context = context;
        }

        @Override
        public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
            //默认数据源工厂
            return new DefaultHttpDataSourceFactory(context.getPackageName(),null , DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);

        }
    }

    class UriDataBean implements ItemVideo{
        String mUri;
        UriDataBean(String uri){
            mUri  = uri;
        }

        @Override
        public String getVideoUri() {
            return mUri;
        }
    }
}
