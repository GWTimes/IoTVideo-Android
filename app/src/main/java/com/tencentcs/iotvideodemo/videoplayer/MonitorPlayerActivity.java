package com.tencentcs.iotvideodemo.videoplayer;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.TextureView;
import android.view.View;
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
import com.tencentcs.iotvideo.iotvideoplayer.player.LivePlayer;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.Utils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.utils.StorageManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

public class MonitorPlayerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MonitorPlayerActivity";

    private IoTVideoView mVideoView;
    private TextureView mPreviewSurface;
    private LivePlayer mMonitorPlayer;

    private ConstraintLayout mRootView;
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

    private Button mClearOutput;
    private TextView mResultTxt;

    private String mDeviceId = "";

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_player);
        mRootView = findViewById(R.id.root_view);
        mPreviewSurface = findViewById(R.id.preview_surface);
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

        mClearOutput = findViewById(R.id.tv_clear);
        mClearOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultTxt.setText("");
            }
        });
        mResultTxt = findViewById(R.id.output_txt);
        mResultTxt.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.tv_browse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StorageManager.isPicPathAvailable()) {
                    Toast.makeText(MonitorPlayerActivity.this, "storage is not available", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //判断是否是AndroidN以及更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        Uri contentUri = FileProvider.getUriForFile(MonitorPlayerActivity.this, "com.tencentcs.iotvideodemo.fileProvider",
                                new File(StorageManager.getPicPath()));
                        LogUtils.i(TAG, contentUri.toString());
                        intent.setData(contentUri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                    } catch (Exception e) {
                        LogUtils.e(TAG, e.getMessage());
                    }
                } else {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(MonitorPlayerActivity.this.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getParentFile()), "*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                MonitorPlayerActivity.this.startActivity(intent);
            }
        });

        mDeviceId = getIntent().getStringExtra("deviceID");
        boolean useMediaCodec = getIntent().getBooleanExtra("useMediaCodec", false);
        boolean renderDirectly = getIntent().getBooleanExtra("renderDirectly", false);
        int renderDirectlyType = getIntent().getIntExtra("renderDirectlyType", 0);
        LogUtils.i(TAG, "mDeviceId = " + mDeviceId + " useMediaCodec = " + useMediaCodec
                + " renderDirectly = " + renderDirectly + " renderDirectlyType = " + renderDirectlyType);

        mVideoView = new IoTVideoView(this);
        mRootView.addView(mVideoView);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mVideoView.getLayoutParams();
        layoutParams.width = 0;
        layoutParams.height = 0;
        layoutParams.dimensionRatio = "H,16:9";
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;

        mMonitorPlayer = new LivePlayer();
        mMonitorPlayer.setDataResource(mDeviceId);
        mMonitorPlayer.setPreparedListener(mPreparedListener);
        mMonitorPlayer.setStatusListener(mStatusListener);
        mMonitorPlayer.setTimeListener(mTimeListener);
        mMonitorPlayer.setErrorListener(mErrorListener);
        mMonitorPlayer.setUserDataListener(mUserDataListener);
        mMonitorPlayer.setVideoView(mVideoView);
        appendToOutput("设备ID：" + mDeviceId + " useMediaCodec = " + useMediaCodec + " " + mVideoView.getClass().getSimpleName());
    }

    private IPreparedListener mPreparedListener = new IPreparedListener() {
        @Override
        public void onPrepared() {
            LogUtils.d(TAG, "onPrepared");
            appendToOutput("开始准备");
        }
    };

    private IStatusListener mStatusListener = new IStatusListener() {
        @Override
        public void onStatus(int status) {
            LogUtils.d(TAG, "onStatus status " + status);
            appendToOutput("播放状态：" + getPlayStatus(status));
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
            appendToOutput("播放错误：" + Utils.getErrorDescription(error));
        }
    };

    private IUserDataListener mUserDataListener = new IUserDataListener() {
        @Override
        public void onReceive(byte[] data) {
            LogUtils.d(TAG, "onReceive ----");
            appendToOutput("收到数据：" + data);
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
                    Toast.makeText(this, "storage is not available", Toast.LENGTH_LONG).show();
                    break;
                }
                File snapFile = new File(StorageManager.getPicPath() + File.separator + mDeviceId);
                if (!snapFile.exists() && !snapFile.mkdirs()) {
                    LogUtils.e(TAG, "can not create file");
                    break;
                }
                mMonitorPlayer.snapShot(snapFile.getAbsolutePath() + File.separator + mSimpleDateFormat.format(new Date()) + ".jpeg",
                        new ISnapShotListener() {
                            @Override
                            public void onResult(int code, String path) {
                                Toast.makeText(MonitorPlayerActivity.this, "code:" + code + " path:" + path, Toast.LENGTH_LONG).show();
                                appendToOutput("截图结果：  返回码 " + code + " 路径 " + path);
                            }
                        });
                break;
            case R.id.record_btn:
                if (!StorageManager.isVideoPathAvailable()) {
                    Toast.makeText(this, "storage is not available", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mMonitorPlayer.isRecording()) {
                    mRecordBtn.setText("开始录像");
                    appendToOutput("停止录像");
                    mMonitorPlayer.stopRecord();
                } else {
                    mRecordBtn.setText("停止录像");
                    appendToOutput("开始录像");
                    File recordFile = new File(StorageManager.getVideoPath() + File.separator + mDeviceId);
                    if (!recordFile.exists() && !recordFile.mkdirs()) {
                        LogUtils.e(TAG, "can not create file");
                        break;
                    }
                    mMonitorPlayer.startRecord(recordFile.getAbsolutePath() + File.separator + mSimpleDateFormat.format(new Date()) + ".mp4",
                            new IRecordListener() {
                                @Override
                                public void onResult(int code, String path) {
                                    Toast.makeText(MonitorPlayerActivity.this, "code:" + code + " path:" + path, Toast.LENGTH_LONG).show();
                                    if (code != 0) {
                                        mRecordBtn.setText("开始录像");
                                    }
                                    appendToOutput("录像结果：  返回码 " + code + " 路径 " + path);
                                }
                            });
                }
                break;
            case R.id.start_talk_btn:
                requestPermissions(new OnPermissionsListener() {
                    @Override
                    public void OnPermissions(boolean granted) {
                        if (granted) {
                            mMonitorPlayer.startTalk();
                            appendToOutput("开始对讲");
                        }
                    }
                }, Manifest.permission.RECORD_AUDIO);
                break;
            case R.id.stop_talk_btn:
                if (mMonitorPlayer.isTalking()) {
                    mMonitorPlayer.stopTalk();
                    appendToOutput("结束对讲");
                }
                break;
            case R.id.open_camera_btn:
                requestPermissions(new OnPermissionsListener() {
                    @Override
                    public void OnPermissions(boolean granted) {
                        if (granted) {
                            mMonitorPlayer.openCameraAndPreview(MonitorPlayerActivity.this,
                                    mPreviewSurface, StorageManager.getVideoPath() + File.separator + "preview.mp4");
                            appendToOutput("打开摄像头");
                        }
                    }
                }, Manifest.permission.CAMERA);
                break;
            case R.id.choose_camera_btn:
                mMonitorPlayer.switchCamera(this);
                appendToOutput("切换摄像头");
                break;
            case R.id.close_camera_btn:
                if (mMonitorPlayer.isCameraOpen()) {
                    mMonitorPlayer.closeCamera();
                    appendToOutput("关闭摄像头");
                }
                break;
            case R.id.mute_btn:
                mMonitorPlayer.mute(!mMonitorPlayer.isMute());
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMonitorPlayer != null) {
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

    private void appendToOutput(String text) {
        mResultTxt.append("\n" + text);
        int offset = mResultTxt.getLineCount() * mResultTxt.getLineHeight();
        if (offset > mResultTxt.getHeight()) {
            mResultTxt.scrollTo(0, offset - mResultTxt.getHeight());
        }
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
}
