package com.tencentcs.iotvideodemo.videoplayer;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencentcs.iotvideo.iotvideoplayer.IErrorListener;
import com.tencentcs.iotvideo.iotvideoplayer.IPreparedListener;
import com.tencentcs.iotvideo.iotvideoplayer.IRecordListener;
import com.tencentcs.iotvideo.iotvideoplayer.ISnapShotListener;
import com.tencentcs.iotvideo.iotvideoplayer.IStatusListener;
import com.tencentcs.iotvideo.iotvideoplayer.ITimeListener;
import com.tencentcs.iotvideo.iotvideoplayer.IUserDataListener;
import com.tencentcs.iotvideo.iotvideoplayer.IoTVideoView;
import com.tencentcs.iotvideo.iotvideoplayer.PlayerStateEnum;
import com.tencentcs.iotvideo.iotvideoplayer.player.MonitorPlayer;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.utils.StorageManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MonitorPlayerFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "MonitorPlayerFragment";

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

    private IoTVideoView mVideoView;
    private MonitorPlayer mMonitorPlayer;
    private String mDeviceId = "";

    private Button mPlayBtn;
    private Button mStopBtn;
    private Button mSnapBtn;
    private Button mRecordBtn;
    private Button mMuteBtn;
    private Button mTalk;
    private Button mCamera;
    private Button mChooseCamera;
    private TextView mTvMonitorState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monitor_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideoView = view.findViewById(R.id.surface_view);
        mPlayBtn = view.findViewById(R.id.play_btn);
        mPlayBtn.setOnClickListener(this);
        mStopBtn = view.findViewById(R.id.stop_btn);
        mStopBtn.setOnClickListener(this);
        mSnapBtn = view.findViewById(R.id.snap_btn);
        mSnapBtn.setOnClickListener(this);
        mRecordBtn = view.findViewById(R.id.record_btn);
        mRecordBtn.setOnClickListener(this);
        mMuteBtn = view.findViewById(R.id.mute_btn);
        mMuteBtn.setOnClickListener(this);
        mTalk = view.findViewById(R.id.start_talk_btn);
        mTalk.setOnClickListener(this);
        mCamera = view.findViewById(R.id.open_camera_btn);
        mCamera.setOnClickListener(this);
        mChooseCamera = view.findViewById(R.id.choose_camera_btn);
        mChooseCamera.setOnClickListener(this);
        mTvMonitorState = view.findViewById(R.id.monitor_status);

        mMonitorPlayer = new MonitorPlayer();
        mMonitorPlayer.setVideoView(mVideoView);
        mMonitorPlayer.setPreparedListener(mPreparedListener);
        mMonitorPlayer.setStatusListener(mStatusListener);
        mMonitorPlayer.setTimeListener(mTimeListener);
        mMonitorPlayer.setErrorListener(mErrorListener);
        mMonitorPlayer.setUserDataListener(mUserDataListener);
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
        mMonitorPlayer.setDataResource(mDeviceId);
        mTvMonitorState.setText(mDeviceId);
    }

    private IPreparedListener mPreparedListener = new IPreparedListener() {
        @Override
        public void onPrepared() {
            log("onPrepared");
            showToast("onPrepared");
        }
    };

    private IStatusListener mStatusListener = new IStatusListener() {
        @Override
        public void onStatus(int status) {
            log("onStatus status " + getPlayStatus(status));
            showToast(getPlayStatus(status));
        }
    };

    private ITimeListener mTimeListener = new ITimeListener() {
        @Override
        public void onTime(long currentTime) {
            //log("onTime currentTime " + currentTime);
        }
    };

    private IErrorListener mErrorListener = new IErrorListener() {
        @Override
        public void onError(int error) {
            log("onError error " + error);
            showToast("onError " + error);
        }
    };

    private IUserDataListener mUserDataListener = new IUserDataListener() {
        @Override
        public void onReceive(byte[] data) {
            log("onReceive ----");
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
                if (!StorageManager.isPicPathAvailable()) {
                    showToast("storage is not available");
                    break;
                }
                Date date = new Date();
                String dateStringParse = mSimpleDateFormat.format(date);
                mMonitorPlayer.snapShot(new File(StorageManager.getPicPath(), dateStringParse + ".jpeg").getAbsolutePath(),
                        new ISnapShotListener() {
                            @Override
                            public void onResult(int code, String path) {
                                showToast("code:" + code + " path:" + path);
                            }
                        });
                break;
            case R.id.mute_btn:
                mMonitorPlayer.mute(!mMonitorPlayer.isMute());
                break;
            case R.id.record_btn:
                if (!StorageManager.isVideoPathAvailable()) {
                    showToast("storage is not available");
                    return;
                }
                if (mMonitorPlayer.isRecording()) {
                    mRecordBtn.setText("开始录像");
                    mMonitorPlayer.stopRecord();
                } else {
                    mRecordBtn.setText("停止录像");
                    Date tdate = new Date();
                    String tdateStringParse = mSimpleDateFormat.format(tdate);
                    mMonitorPlayer.startRecord(new File(StorageManager.getVideoPath(), tdateStringParse + ".mp4").getAbsolutePath(),
                            new IRecordListener() {
                                @Override
                                public void onResult(int code, String path) {
                                    showToast("code:" + code + " path:" + path);
                                    if (code != 0) {
                                        mRecordBtn.setText("开始录像");
                                    }
                                }
                            });
                }
                break;
            case R.id.start_talk_btn:
                if (mMonitorPlayer.isTalking()) {
                    mMonitorPlayer.stopTalk();
                } else {
                    requestPermissions(new BaseFragment.OnPermissionsListener() {
                        @Override
                        public void OnPermissions(boolean granted) {
                            if (granted) {
                                mMonitorPlayer.startTalk();
                            }
                        }
                    }, Manifest.permission.RECORD_AUDIO);
                }
                break;
            case R.id.open_camera_btn:
//                mVideoView.openCamera();
                break;
            case R.id.choose_camera_btn:
//                mVideoView.switchCamera(1);
                break;
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), mDeviceId + " : " + msg, Toast.LENGTH_SHORT).show();
        mTvMonitorState.setText(msg);
    }

    private String getPlayStatus(int status) {
        String playStatus = "";
        switch (status) {
            case PlayerStateEnum.STATE_IDLE:
                playStatus = "未初始化";
                break;
            case PlayerStateEnum.STATE_INITIALIZED:
                playStatus = "已初始化";
                break;
            case PlayerStateEnum.STATE_PREPARING:
                playStatus = "准备中...";
                break;
            case PlayerStateEnum.STATE_READY:
                playStatus = "准备完成";
                break;
            case PlayerStateEnum.STATE_LOADING:
                playStatus = "加载中";
                break;
            case PlayerStateEnum.STATE_PLAY:
                playStatus = "播放中";
                break;
            case PlayerStateEnum.STATE_PAUSE: {
                playStatus = "暂停";
            }
            break;
            case PlayerStateEnum.STATE_STOP:
                playStatus = "停止播放";
                break;
        }
        return playStatus;
    }

    @Override
    public void onResume() {
        log("onResume");
        super.onResume();
        if (mVideoView != null) {
            mVideoView.onResume();
        }
    }

    @Override
    public void onPause() {
        log("onPause");
        super.onPause();
        if (mVideoView != null) {
            mVideoView.onPause();
        }
    }

    @Override
    public void onStop() {
        log("onStop");
        super.onStop();
        if (mMonitorPlayer != null) {
            mMonitorPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        super.onDestroy();
        if (mMonitorPlayer != null) {
            mMonitorPlayer.release();
            mMonitorPlayer = null;
        }
    }

    private void log(String msg) {
        LogUtils.i(TAG, mDeviceId + ":" + msg);
    }
}
