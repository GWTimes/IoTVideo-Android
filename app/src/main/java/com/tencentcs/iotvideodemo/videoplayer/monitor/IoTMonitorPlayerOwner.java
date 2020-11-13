package com.tencentcs.iotvideodemo.videoplayer.monitor;

import android.Manifest;
import android.text.TextUtils;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencentcs.iotvideo.iotvideoplayer.CallTypeEnum;
import com.tencentcs.iotvideo.iotvideoplayer.IErrorListener;
import com.tencentcs.iotvideo.iotvideoplayer.IPreparedListener;
import com.tencentcs.iotvideo.iotvideoplayer.IRecordListener;
import com.tencentcs.iotvideo.iotvideoplayer.ISnapShotListener;
import com.tencentcs.iotvideo.iotvideoplayer.IStatusListener;
import com.tencentcs.iotvideo.iotvideoplayer.IUserDataListener;
import com.tencentcs.iotvideo.iotvideoplayer.IoTVideoView;
import com.tencentcs.iotvideo.iotvideoplayer.PlayerStateEnum;
import com.tencentcs.iotvideo.iotvideoplayer.mediacodec.MediaCodecAudioDecoder;
import com.tencentcs.iotvideo.iotvideoplayer.mediacodec.MediaCodecAudioEncoder;
import com.tencentcs.iotvideo.iotvideoplayer.mediacodec.MediaCodecVideoDecoder;
import com.tencentcs.iotvideo.iotvideoplayer.player.LivePlayer;
import com.tencentcs.iotvideo.messagemgr.AInnerUserDataLister;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.videoplayer.MonitorConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class IoTMonitorPlayerOwner implements IPreparedListener, IStatusListener,
        IErrorListener, IUserDataListener, MonitorPlayerOwner {
    private static final String TAG = "IoTMonitorPlayerOwner";

    private static final SimpleDateFormat mSimpleDateFormat =
            new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);

    private Fragment mViewOwner;
    private LivePlayer mMonitorPlayer;
    private List<IPreparedListener> mPreparedListeners = new ArrayList<>();
    private List<IStatusListener> mStatusListeners = new ArrayList<>();
    private List<IErrorListener> mErrorListeners = new ArrayList<>();
    private List<IUserDataListener> mUserDataListeners = new ArrayList<>();
    private List<RecordListener> mRecordListeners = new ArrayList<>();
    private List<ISnapShotListener> mSnapShotListeners = new ArrayList<>();
    private List<OnSpeedChangedListener> mOnSpeedChangedListener = new ArrayList<>();
    private String mRecordPath;
    private String mSnapPath;
    private Disposable mRequestAudioDis;
    private Disposable mGetSpeedDisposable;
    private int mCurrentSpeed;

    public IoTMonitorPlayerOwner(Fragment fragment, IoTVideoView videoView, String deviceId, MonitorConfig config) {
        mViewOwner = fragment;
        mMonitorPlayer = new LivePlayer();
        if (config != null) {
            mMonitorPlayer.setDataResource(deviceId, config.definition, config.sourceId);
            if (config.useMediaCodecAudioDecode) {
                mMonitorPlayer.setAudioDecoder(new MediaCodecAudioDecoder());
            }
            if (config.useMediaCodecVideoDecode) {
                mMonitorPlayer.setVideoDecoder(new MediaCodecVideoDecoder());
            }
            if (config.useMediaCodecAudioEncode) {
                mMonitorPlayer.setAudioEncoder(new MediaCodecAudioEncoder());
            }
        } else {
            mMonitorPlayer.setDataResource(deviceId, CallTypeEnum.VIDEO_DEFINITION_HD, (short) 0);
        }
        mMonitorPlayer.setPreparedListener(this);
        mMonitorPlayer.setStatusListener(this);
        mMonitorPlayer.setErrorListener(this);
        mMonitorPlayer.setUserDataListener(this);
        mMonitorPlayer.setVideoView(videoView);
    }

    @Override
    public void onPrepared() {
        LogUtils.i(TAG, "onPrepared");
        for (IPreparedListener listener : mPreparedListeners) {
            listener.onPrepared();
        }
    }

    @Override
    public void onStatus(int status) {
        LogUtils.i(TAG, "onStatus status " + status);
        for (IStatusListener listener : mStatusListeners) {
            listener.onStatus(status);
        }
        if (status == PlayerStateEnum.STATE_PLAY) {
            startGetSpeed();
        } else if (status == PlayerStateEnum.STATE_STOP || status == PlayerStateEnum.STATE_PAUSE) {
            stopGetSpeed();
        }
    }

    @Override
    public void onError(int error) {
        LogUtils.i(TAG, "onError " + error);
        for (IErrorListener listener : mErrorListeners) {
            listener.onError(error);
        }
    }

    @Override
    public void onReceive(byte[] data) {
        LogUtils.d(TAG, "onReceive");
        for (IUserDataListener listener : mUserDataListeners) {
            listener.onReceive(data);
        }
    }

    @Override
    public void addPreparedListener(IPreparedListener listener) {
        mPreparedListeners.add(listener);
    }

    @Override
    public void addStatusListener(IStatusListener listener) {
        mStatusListeners.add(listener);
    }

    @Override
    public void addErrorListener(IErrorListener listener) {
        mErrorListeners.add(listener);
    }

    @Override
    public void addUserDataListener(IUserDataListener listener) {
        mUserDataListeners.add(listener);
    }

    @Override
    public void addRecordListener(RecordListener listener) {
        mRecordListeners.add(listener);
    }

    @Override
    public void addSnapShotListener(ISnapShotListener listener) {
        mSnapShotListeners.add(listener);
    }

    @Override
    public void addOnSpeedChangedListener(OnSpeedChangedListener listener) {
        mOnSpeedChangedListener.add(listener);
    }

    @Override
    public void setRecordPath(String path) {
        mRecordPath = path;
    }

    @Override
    public void setSnapPath(String path) {
        mSnapPath = path;
    }

    @Override
    public void play() {
        mMonitorPlayer.play();
    }

    @Override
    public void stop() {
        mMonitorPlayer.stop();
    }

    @Override
    public void release() {
        mMonitorPlayer.release();
        if (mRequestAudioDis != null && !mRequestAudioDis.isDisposed()) {
            mRequestAudioDis.dispose();
        }
        stopGetSpeed();
    }

    @Override
    public boolean isMute() {
        return mMonitorPlayer.isMute();
    }

    @Override
    public void mute(boolean on) {
        mMonitorPlayer.mute(on);
    }

    @Override
    public boolean startRecord() {
        if (TextUtils.isEmpty(mRecordPath)) {
            return false;
        }
        boolean success = mMonitorPlayer.startRecord(mRecordPath, mSimpleDateFormat.format(new Date()) + ".mp4",
                new IRecordListener() {
                    @Override
                    public void onResult(int code, String path) {
                        for (RecordListener listener : mRecordListeners) {
                            listener.onEnd(code, path);
                        }
                    }
                });
        if (success) {
            for (RecordListener listener : mRecordListeners) {
                listener.onStart();
            }
        }

        return success;
    }

    @Override
    public boolean isRecording() {
        return mMonitorPlayer.isRecording();
    }

    @Override
    public void stopRecord() {
        mMonitorPlayer.stopRecord();
    }

    @Override
    public void snapShot() {
        if (TextUtils.isEmpty(mSnapPath)) {
            return;
        }
        mMonitorPlayer.snapShot(mSnapPath + File.separator + mSimpleDateFormat.format(new Date()) + ".jpeg",
                new ISnapShotListener() {
                    @Override
                    public void onResult(int code, String path) {
                        for (ISnapShotListener listener : mSnapShotListeners) {
                            listener.onResult(code, path);
                        }
                    }
                });
    }

    @Override
    public int getPlayState() {
        return mMonitorPlayer.getPlayState();
    }

    @Override
    public int getVideoWidth() {
        return mMonitorPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mMonitorPlayer.getVideoHeight();
    }

    @Override
    public boolean isPlaying() {
        return mMonitorPlayer.isPlaying();
    }

    @Override
    public void changeDefinition(IoTMonitorControl.Definition definition) {
        switch (definition) {
            case LD:
                mMonitorPlayer.changeDefinition((byte) CallTypeEnum.VIDEO_DEFINITION_FL);
                break;
            case SD:
                mMonitorPlayer.changeDefinition((byte) CallTypeEnum.VIDEO_DEFINITION_SD);
                break;
            case HD:
                mMonitorPlayer.changeDefinition((byte) CallTypeEnum.VIDEO_DEFINITION_HD);
                break;
        }
    }

    @Override
    public void directionCtl(boolean isStart, IoTMonitorControl.Direction direction) {

    }

    @Override
    public void startTalk() {
        RxPermissions rxPermissions = new RxPermissions(mViewOwner);
        boolean isGrant = rxPermissions.isGranted(Manifest.permission.RECORD_AUDIO);
        if (isGrant) {
            //开启后APP开始发送音频数据，若开启前后需发送其他通信数据，如唤醒设备扬声器等，请使用`发送自定义数据`方法，参见`MonitorPlayer.sendUserData`
            mMonitorPlayer.startTalk();
        } else {
            mRequestAudioDis = rxPermissions.request(Manifest.permission.RECORD_AUDIO)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                mMonitorPlayer.startTalk();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean isTalking() {
        return mMonitorPlayer.isTalking();
    }

    @Override
    public void stopTalk() {
        //关闭后APP停止发送音频数据，若关闭前后需发送其他通信数据，如关闭设备扬声器等，请使用`发送自定义数据`方法，参见`MonitorPlayer.sendUserData`
        mMonitorPlayer.stopTalk();
    }

    @Override
    public int getSpeed() {
        return mMonitorPlayer.getAvBytesPerSec();
    }

    @Override
    public void setInnerUserDataListener(AInnerUserDataLister listener) {
        mMonitorPlayer.setInnerUserDataListener(listener);
    }

    private void startGetSpeed() {
        if (mGetSpeedDisposable != null) {
            return;
        }
        mGetSpeedDisposable = Observable.interval(3, TimeUnit.SECONDS)
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(Long aLong) throws Exception {
                        return getSpeed();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer speed) throws Exception {
                        if (speed != mCurrentSpeed) {
                            mCurrentSpeed = speed;
                            for (OnSpeedChangedListener listener : mOnSpeedChangedListener) {
                                listener.onSpeedChanged(speed);
                            }
                        }
                    }
                });
    }

    private void stopGetSpeed() {
        if (mGetSpeedDisposable != null) {
            if (!mGetSpeedDisposable.isDisposed()) {
                mGetSpeedDisposable.dispose();
            }
            mGetSpeedDisposable = null;
        }
    }
}
