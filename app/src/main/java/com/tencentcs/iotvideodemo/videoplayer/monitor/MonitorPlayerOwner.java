package com.tencentcs.iotvideodemo.videoplayer.monitor;

import com.tencentcs.iotvideo.iotvideoplayer.IErrorListener;
import com.tencentcs.iotvideo.iotvideoplayer.IPreparedListener;
import com.tencentcs.iotvideo.iotvideoplayer.ISnapShotListener;
import com.tencentcs.iotvideo.iotvideoplayer.IStatusListener;
import com.tencentcs.iotvideo.iotvideoplayer.IUserDataListener;
import com.tencentcs.iotvideo.iotvideoplayer.PlayerStateEnum;
import com.tencentcs.iotvideo.messagemgr.AInnerUserDataLister;

public interface MonitorPlayerOwner {
    /**
     * 准备回调
     */
    void addPreparedListener(IPreparedListener listener);

    /**
     * 状态回调
     */
    void addStatusListener(IStatusListener listener);

    /**
     * 错误回调
     */
    void addErrorListener(IErrorListener listener);

    /**
     * 数据回调
     */
    void addUserDataListener(IUserDataListener listener);

    /**
     * 录像回调
     */
    void addRecordListener(RecordListener listener);

    /**
     * 截图回调
     */
    void addSnapShotListener(ISnapShotListener listener);

    /**
     * 速率回调
     */
    void addOnSpeedChangedListener(OnSpeedChangedListener listener);

    /**
     * 设置录像文件夹
     *
     * @param path 文件路径
     */
    void setRecordPath(String path);

    /**
     * 设置截图文件夹
     *
     * @param path 文件路径
     */
    void setSnapPath(String path);

    /**
     * 播放
     */
    void play();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 释放
     */
    void release();

    /**
     * 是否在静音状态
     */
    boolean isMute();

    /**
     * 静音
     *
     * @param on true==静音，false==非静音
     */
    void mute(boolean on);

    /**
     * 录像
     *
     * @return true==成功 false==失败
     */
    boolean startRecord();

    /**
     * 是否在录像
     *
     * @return true 是 false 否
     */
    boolean isRecording();

    /**
     * 停止录像
     */
    void stopRecord();

    /**
     * 截图
     */
    void snapShot();

    /**
     * 获取播放状态
     *
     * @return 状态枚举，参考:
     * {@link PlayerStateEnum#STATE_IDLE} 播放器初始状态
     */
    int getPlayState();

    /**
     * 获取视频宽
     *
     * @return 视频宽
     */
    int getVideoWidth();

    /**
     * 获取视频高
     *
     * @return 视频高
     */
    int getVideoHeight();

    /**
     * 是否在播放
     *
     * @return true 是 false 否
     */
    boolean isPlaying();

    /**
     * 切换分辨率
     *
     * @param definition 分辨率
     */
    void changeDefinition(IoTMonitorControl.Definition definition);

    /**
     * 转动摄像头
     *
     * @param isStart   是否是开始转动
     * @param direction 方法
     */
    void directionCtl(boolean isStart, IoTMonitorControl.Direction direction);

    /**
     * 开始对讲
     */
    void startTalk();

    /**
     * 正在对讲
     *
     * @return true 是 false 否
     */
    boolean isTalking();

    /**
     * 结束对讲
     */
    void stopTalk();

    /**
     * 获取流量
     *
     * @return 每秒钟收到的字节数
     */
    int getSpeed();

    void setInnerUserDataListener(AInnerUserDataLister listener);

    interface RecordListener {
        void onStart();

        void onEnd(int code, String path);
    }

    interface OnSpeedChangedListener {
        void onSpeedChanged(int speed);
    }
}
