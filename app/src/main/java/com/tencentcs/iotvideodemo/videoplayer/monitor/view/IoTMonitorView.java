package com.tencentcs.iotvideodemo.videoplayer.monitor.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencentcs.iotvideo.iotvideoplayer.IErrorListener;
import com.tencentcs.iotvideo.iotvideoplayer.ISnapShotListener;
import com.tencentcs.iotvideo.iotvideoplayer.IStatusListener;
import com.tencentcs.iotvideo.iotvideoplayer.IoTVideoView;
import com.tencentcs.iotvideo.iotvideoplayer.PlayerStateEnum;
import com.tencentcs.iotvideo.iotvideoplayer.render.GestureGLSurfaceView;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.videoplayer.monitor.IoTMonitorControl;
import com.tencentcs.iotvideodemo.videoplayer.monitor.MonitorPlayerOwner;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class IoTMonitorView extends ConstraintLayout implements View.OnClickListener,
        IoTMonitorControl {
    private static final String TAG = "IoTMonitorView";

    private IoTVideoView mIoTVideoView;
    private ImageView mIvSnap;
    private ImageView mIvFullScreen;
    private ImageView mIvMuteOn;
    private ImageView mIvMuteOff;
    private ImageView mIvRecord;
    private ImageView mIvTalk;
    private ImageView mIvShotPic;
    private TextView mTvDefinition;
    private TextView mTvMonitorState;
    private LinearLayout mLlRightCtl;

    private boolean mIsViewVisible;
    private boolean mKeepViewVisible;
    private Timer mTimer;

    private MonitorPlayerOwner mOwner;

    public IoTMonitorView(Context context) {
        super(context);
        initView(context);
    }

    public IoTMonitorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public IoTMonitorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_iot_monitor, this);
        mIoTVideoView = findViewById(R.id.gl_surface_view);
        mIoTVideoView.setSingleTapUpListener(new GestureGLSurfaceView.OnSingleTapUp() {
            @Override
            public void onSingleTapUp(MotionEvent e) {
                if (!mKeepViewVisible) {
                    setViewState(mIsViewVisible ? GONE : VISIBLE);
                    resetTimer();
                }
            }
        });
        findViewById(R.id.iot_fullscreen_btn).setOnClickListener(this);
        mIvSnap = findViewById(R.id.iot_snap_btn);
        mIvSnap.setOnClickListener(this);
        mIvRecord = findViewById(R.id.iot_record_btn);
        mIvRecord.setOnClickListener(this);
        mIvTalk = findViewById(R.id.iot_talk_btn);
        mIvTalk.setOnClickListener(this);
        mIvMuteOn = findViewById(R.id.iot_mute_on_btn);
        mIvMuteOn.setOnClickListener(this);
        mIvMuteOff = findViewById(R.id.iot_mute_off_btn);
        mIvMuteOff.setOnClickListener(this);
        mTvDefinition = findViewById(R.id.tv_iot_definition);
        mTvDefinition.setOnClickListener(this);
        mLlRightCtl = findViewById(R.id.iot_right_ctl);
        mIvFullScreen = findViewById(R.id.iot_fullscreen_btn);
        mTvMonitorState = findViewById(R.id.tv_iot_monitor_state);
        mTvMonitorState.setOnClickListener(this);
        mIvShotPic = findViewById(R.id.iv_shot_pic);
    }

    public IoTVideoView getIoTVideoView() {
        return mIoTVideoView;
    }

    private void setMonitorState(int state) {
        LogUtils.i(TAG, "setMonitorState " + state);
        String playStatus = "";
        int playIconRes = -1;
        switch (state) {
            case PlayerStateEnum.STATE_INITIALIZED:
            case PlayerStateEnum.STATE_PREPARING:
            case PlayerStateEnum.STATE_READY:
            case PlayerStateEnum.STATE_LOADING:
                playStatus = "正在连接...";
                setEnabled(false);
                break;
            case PlayerStateEnum.STATE_PLAY:
                playStatus = "";
                setEnabled(true);
                scaleVideo();
                break;
            case PlayerStateEnum.STATE_PAUSE:
                playStatus = "暂停";
                setEnabled(false);
                break;
            case PlayerStateEnum.STATE_STOP:
            case PlayerStateEnum.STATE_IDLE:
                playStatus = "连接失败";
                playIconRes = R.drawable.ic_iot_refresh;
                setEnabled(false);
                break;
        }
        mTvMonitorState.setText(playStatus);
        if (playIconRes != -1) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), playIconRes);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mTvMonitorState.setCompoundDrawables(null, drawable, null, null);
            }
        } else {
            mTvMonitorState.setCompoundDrawables(null, null, null, null);
        }
    }

    private void scaleVideo() {
        if (mOwner == null || mOwner.getVideoWidth() <= 0 || mOwner.getVideoHeight() <= 0) {
            return;
        }
        int videoWidth = mOwner.getVideoWidth();
        int videoHeight = mOwner.getVideoHeight();
        int viewWidth = mIoTVideoView.getWidth();
        int viewHeight = mIoTVideoView.getHeight();
        float scale1 = videoWidth * viewHeight * 1.0f / videoHeight / viewWidth;
        float scale2 = videoHeight * viewWidth * 1.0f / videoWidth / viewHeight;
        float currentScale = mIoTVideoView.getCurrentScale();

        mIoTVideoView.scaleVideo(viewWidth >> 1, viewHeight >> 1, Math.max(scale1, scale2) / currentScale);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mIvFullScreen.setEnabled(enabled);
        mIvMuteOn.setEnabled(enabled);
        mIvMuteOff.setEnabled(enabled);
        mIvRecord.setEnabled(enabled);
        mIvTalk.setEnabled(enabled);
        mTvDefinition.setEnabled(enabled);
        mLlRightCtl.setEnabled(enabled);
        mIvSnap.setEnabled(enabled);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isLand = newConfig.orientation != Configuration.ORIENTATION_PORTRAIT;
        mIvFullScreen.setSelected(isLand);
        mIvShotPic.setVisibility(GONE);
    }

    private void setViewState(int visible) {
        if (getHandler() != null) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    setLandViewStateSafely(visible);
                    mIsViewVisible = visible == VISIBLE;
                }
            });
        }
    }

    private void setLandViewStateSafely(int visible) {
        mLlRightCtl.setVisibility(visible);
        mIvShotPic.setVisibility(GONE);
    }

    private void setPublicViewStateSafely(int visible) {
        mTvDefinition.setVisibility(visible);
        mIvFullScreen.setVisibility(visible);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST && heightSize > widthSize * 0.75f) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * 0.75), MeasureSpec.EXACTLY);
        } else {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onAttachedToWindow() {
        LogUtils.i(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
        schedule();
    }

    @Override
    protected void onDetachedFromWindow() {
        LogUtils.i(TAG, "onDetachedFromWindow");
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void finalize() throws Throwable {
        LogUtils.i(TAG, "finalize");
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.finalize();
    }

    private void resetTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            schedule();
        }
    }

    private void schedule() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
//                LogUtils.i(TAG, "schedule time");
                if (!mKeepViewVisible) {
                    setViewState(GONE);
                }
            }
        }, 5000, 5000);
    }

    @Override
    public void onClick(View view) {
        resetTimer();
        switch (view.getId()) {
            case R.id.iot_fullscreen_btn:
                backClicked();
                break;
            case R.id.iot_snap_btn:
                snapClicked();
                break;
            case R.id.iot_record_btn:
                if (mIvRecord.isSelected()) {
                    recordClicked(false);
                } else {
                    recordClicked(true);
                }
                break;
            case R.id.iot_talk_btn:
                if (mIvTalk.isSelected()) {
                    talkClicked(false);
                    mIvTalk.setSelected(false);
                } else {
                    talkClicked(true);
                    mIvTalk.setSelected(true);
                }
                break;
            case R.id.iot_mute_on_btn:
                muteClicked(true);
                mIvMuteOn.setVisibility(GONE);
                mIvMuteOff.setVisibility(VISIBLE);
                break;
            case R.id.iot_mute_off_btn:
                muteClicked(false);
                mIvMuteOn.setVisibility(VISIBLE);
                mIvMuteOff.setVisibility(GONE);
                break;
            case R.id.tv_iot_monitor_state:
                if (mOwner == null) {
                    break;
                }
                if (mOwner.getPlayState() == PlayerStateEnum.STATE_STOP ||
                        mOwner.getPlayState() == PlayerStateEnum.STATE_IDLE) {
                    mOwner.play();
                }
                break;
        }
    }

    @Override
    public void setMonitorOwner(MonitorPlayerOwner owner) {
        mOwner = owner;
        mOwner.addStatusListener(new IStatusListener() {
            @Override
            public void onStatus(int status) {
                setMonitorState(status);
            }
        });
        mOwner.addErrorListener(new IErrorListener() {
            @Override
            public void onError(int error) {
                setMonitorState(PlayerStateEnum.STATE_STOP);
            }
        });
        mOwner.addRecordListener(new MonitorPlayerOwner.RecordListener() {
            @Override
            public void onStart() {
                mIvRecord.setSelected(true);
            }

            @Override
            public void onEnd(int code, String path) {
                LogUtils.i(TAG, "on record result " + code + " " + path);
                mIvRecord.setSelected(false);
                if (code == 0) {
                    mIvShotPic.setVisibility(VISIBLE);
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(path);
                    mIvShotPic.setImageBitmap(mmr.getFrameAtTime());
                    mmr.release();//释放资源
                    shotPicAnim(mIvShotPic);
                } else {
                    Toast.makeText(getContext(), "录像失败 " + code, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mOwner.addSnapShotListener(new ISnapShotListener() {
            @Override
            public void onResult(int code, String path) {
                LogUtils.i(TAG, "on snap result " + code + " " + path);
                if (code == 0) {
                    mIvShotPic.setVisibility(VISIBLE);
                    mIvShotPic.setImageURI(Uri.parse(path));
                    shotPicAnim(mIvShotPic);
                } else {
                    Toast.makeText(getContext(), "截图失败 " + code, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void shotPicAnim(View view) {
        if (mOwner == null || mOwner.getVideoWidth() <= 0 || mOwner.getVideoHeight() <= 0) {
            return;
        }
        mIvShotPic.setTranslationX(0);
        mIvShotPic.setTranslationY(0);
        mIvShotPic.setScaleX(1.0f);
        mIvShotPic.setScaleY(1.0f);

        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float videoWidth = mOwner.getVideoWidth();
        float videoHeight = mOwner.getVideoHeight();
        float yScale = 0.3f;
        float targetWidth = yScale * viewHeight * (videoWidth / videoHeight);
        float xScale = targetWidth / viewWidth;
        float yOffset = viewHeight / 2 - viewHeight * 0.3f / 2 - dip2px(16);
        float xOffset = viewWidth / 2 - targetWidth / 2 - dip2px(16);
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", -xOffset);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", yOffset);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, xScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, yScale);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationX, translationY, scaleX, scaleY);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    private int dip2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void backClicked() {

    }

    @Override
    public void snapClicked() {
        if (mOwner != null) {
            mOwner.snapShot();
        }
    }

    @Override
    public void recordClicked(boolean on) {
        if (mOwner != null) {
            if (on) {
                mOwner.startRecord();
            } else {
                mOwner.stopRecord();
            }
        }
    }

    @Override
    public void talkClicked(boolean on) {
        if (mOwner != null) {
            if (on) {
                mOwner.startTalk();
            } else {
                mOwner.stopTalk();
            }
        }
    }

    @Override
    public void muteClicked(boolean on) {
        if (mOwner != null) {
            mOwner.mute(on);
        }
    }

    @Override
    public void definitionClicked(Definition definition) {
        if (mOwner != null) {
            mOwner.changeDefinition(definition);
        }
    }

    @Override
    public void directionClicked(boolean isStart, Direction direction) {
        if (mOwner != null) {
            mOwner.directionCtl(isStart, direction);
        }
    }

    @Override
    public void modeClicked(Mode mode) {

    }
}
