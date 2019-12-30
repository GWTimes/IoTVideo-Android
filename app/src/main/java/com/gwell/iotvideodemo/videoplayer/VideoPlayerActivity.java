package com.gwell.iotvideodemo.videoplayer;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gwell.iotvideo.iotvideoplayer.IErrorListener;
import com.gwell.iotvideo.iotvideoplayer.IPreparedListener;
import com.gwell.iotvideo.iotvideoplayer.IResultListener;
import com.gwell.iotvideo.iotvideoplayer.IStatusListener;
import com.gwell.iotvideo.iotvideoplayer.ITimeListener;
import com.gwell.iotvideo.iotvideoplayer.IUserDataListener;
import com.gwell.iotvideo.iotvideoplayer.IoTVideoView;
import com.gwell.iotvideo.iotvideoplayer.player.MonitorPlayer;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseActivity;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Map;

public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "VideoPlayerActivity";

    private IoTVideoView mVideoView;
    private MonitorPlayer mMonitorPlayer;

    private Button mPlayBtn;
    private Button mStopBtn;
    private Button mSnapBtn;
    private Button mRecordBtn;

    private Button mMuteBtn;

    private Button mStartTalk;
    private Button mStopTalk;
    private Button mOpenCamera;
    private Button mChooseCamera;
    private Button mCloseCamera;

    private long mDeviceId = 42949672974L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_video_player);
        mVideoView = findViewById(R.id.gwell_gl_surface_view);
        mPlayBtn = findViewById(R.id.play_btn);
        mPlayBtn.setOnClickListener(this);
        mStopBtn = findViewById(R.id.stop_btn);
        mStopBtn.setOnClickListener(this);
        mSnapBtn = findViewById(R.id.snap_btn);
        mSnapBtn.setOnClickListener(this);
        mRecordBtn = findViewById(R.id.record_btn);
        mRecordBtn.setOnClickListener(this);

        mMuteBtn = findViewById(R.id.mute_btn);
        mMuteBtn.setOnClickListener(this);

        mStartTalk = findViewById(R.id.start_talk_btn);
        mStartTalk.setOnClickListener(this);
        mStopTalk = findViewById(R.id.stop_talk_btn);
        mStopTalk.setOnClickListener(this);
        mOpenCamera = findViewById(R.id.open_camera_btn);
        mOpenCamera.setOnClickListener(this);
        mChooseCamera = findViewById(R.id.choose_camera_btn);
        mChooseCamera.setOnClickListener(this);
        mCloseCamera = findViewById(R.id.close_camera_btn);
        mCloseCamera.setOnClickListener(this);

        applyForStoragePerMission();

        if (getIntent() != null) {
            String did = getIntent().getStringExtra("did");
            if (!TextUtils.isEmpty(did)) {
                mDeviceId = Long.valueOf(did);
                LogUtils.i(TAG, "mDeviceId = " + mDeviceId);
            }
        }

        mMonitorPlayer = new MonitorPlayer();
        mMonitorPlayer.setDataResource(mDeviceId);
        mMonitorPlayer.setVideoView(mVideoView);
        mMonitorPlayer.setPreparedListener(mPreparedListener);
        mMonitorPlayer.setStatusListener(mStatusListener);
        mMonitorPlayer.setTimeListener(mTimeListener);
        mMonitorPlayer.setErrorListener(mErrorListener);
        mMonitorPlayer.setUserDataListener(mUserDataListener);

        //mVideoView.prepare();
    }

    private IPreparedListener mPreparedListener = new IPreparedListener() {
        @Override
        public void onPrepared() {
            LogUtils.d(TAG, "onPrepared");
        }
    };

    private IStatusListener mStatusListener = new IStatusListener() {
        @Override
        public void onStatus(int status) {
            LogUtils.d(TAG, "onStatus status " + status);
        }
    };

    private ITimeListener mTimeListener = new ITimeListener() {
        @Override
        public void onTime(long currentTime) {
            //LogUtils.d(TAG, "onTime currentTime " + currentTime);
        }
    };

    private IErrorListener mErrorListener = new IErrorListener() {
        @Override
        public void onError(int error) {
            LogUtils.d(TAG, "onError error " + error);
        }
    };

    private IUserDataListener mUserDataListener = new IUserDataListener() {
        @Override
        public void onReceive(ByteBuffer data) {
            LogUtils.d(TAG, "onReceive ----");
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_btn:
                mMonitorPlayer.play();
                break;
            case R.id.stop_btn:
                mMonitorPlayer.stop();
                break;
            case R.id.snap_btn:
                mMonitorPlayer.snapShot(new File(Environment.getExternalStorageDirectory(), "xxx.jpeg").getAbsolutePath(),
                        new IResultListener() {
                            @Override
                            public void onResult(int code, String path) {
                                Toast.makeText(VideoPlayerActivity.this, "code:" + code + " path:" + path, Toast.LENGTH_LONG).show();
                            }
                        });
                break;
            case R.id.record_btn:
                if (mMonitorPlayer.isRecording()) {
                    mMonitorPlayer.stopRecord();
                    mRecordBtn.setText("开始录像");
                } else {
                    mMonitorPlayer.startRecord(new File(Environment.getExternalStorageDirectory(), "xxx.mp4").getAbsolutePath(),
                            new IResultListener() {
                                @Override
                                public void onResult(int code, String path) {
                                    Toast.makeText(VideoPlayerActivity.this, "code:" + code + " path:" + path, Toast.LENGTH_LONG).show();
                                }
                            });
                    mRecordBtn.setText("停止录像");
                }
                break;
            case R.id.start_talk_btn:
                mMonitorPlayer.startTalk();
                break;
            case R.id.stop_talk_btn:
                if (mMonitorPlayer.isTalking()) {
                    mMonitorPlayer.stopTalk();
                }
                break;
            case R.id.open_camera_btn:
//                mVideoView.openCamera();
                break;
            case R.id.choose_camera_btn:
//                mVideoView.switchCamera(1);
                break;
            case R.id.close_camera_btn:
//                if (mVideoView.isCameraOpen()) {
//                    mVideoView.closeCamera();
//                }
                break;
            case R.id.mute_btn:
                mMonitorPlayer.mute(!mMonitorPlayer.isMute());
                break;
        }
    }

    @Override
    protected void applyForPermissionResult(int mark, Map<String, Boolean> permissionResult, boolean applyResult) {
        super.applyForPermissionResult(mark, permissionResult, applyResult);
        if (applyResult) {
            Boolean readStorage = permissionResult.get(Manifest.permission.READ_EXTERNAL_STORAGE);
            Boolean writeStorage = permissionResult.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (readStorage != null && writeStorage != null && readStorage && writeStorage) {
//                boolean isFileExist = FileUtils.isFileExists(new File(Environment.getExternalStorageDirectory(), "iotvideo.mp4").getAbsolutePath());
//                if (!isFileExist) {
//                    Single.create(new SingleOnSubscribe<String>() {
//
//                        @Override
//                        public void subscribe(SingleEmitter<String> emitter) throws Exception {
//                            FileUtils.copyToSDCard(VideoPlayerActivity.this.getApplicationContext(), "iotvideo.mp4",
//                                    new File(Environment.getExternalStorageDirectory(), "iotvideo.mp4").getAbsolutePath());
//                        }
//                    }).subscribeOn(Schedulers.io())
//                            .subscribe();
//                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.onPause();
        }
        if(mMonitorPlayer != null){
            mMonitorPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMonitorPlayer != null) {
            mMonitorPlayer.release();
            mMonitorPlayer = null;
        }
    }
}
