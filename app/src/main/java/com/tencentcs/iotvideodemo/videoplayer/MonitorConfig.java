package com.tencentcs.iotvideodemo.videoplayer;

import com.tencentcs.iotvideodemo.utils.AppConfig;

import java.io.Serializable;

public class MonitorConfig implements Serializable {
    public boolean supportTalk;
    public boolean supportCamera;
    public boolean useMediaCodecAudioDecode;
    public boolean useMediaCodecVideoDecode;
    public boolean useMediaCodecAudioEncode;

    public static MonitorConfig defaultConfig() {
        MonitorConfig config = new MonitorConfig();
        config.supportTalk = AppConfig.SUPPORT_TALK;
        config.supportCamera = AppConfig.SUPPORT_CAMERA;
        config.useMediaCodecAudioDecode = AppConfig.USE_MEDIACODEC_AUDIO_DECODE;
        config.useMediaCodecVideoDecode = AppConfig.USE_MEDIACODEC_VIDEO_DECODE;
        config.useMediaCodecAudioEncode = AppConfig.USE_MEDIACODEC_AUDIO_ENCODE;

        return config;
    }

    public static MonitorConfig simpleConfig() {
        MonitorConfig config = new MonitorConfig();
        config.supportTalk = false;
        config.supportCamera = false;
        config.useMediaCodecAudioDecode = false;
        config.useMediaCodecVideoDecode = false;
        config.useMediaCodecAudioEncode = false;

        return config;
    }

    @Override
    public String toString() {
        return "MonitorConfig{" +
                "supportTalk=" + supportTalk +
                ", supportCamera=" + supportCamera +
                ", useMediaCodecAudioDecode=" + useMediaCodecAudioDecode +
                ", useMediaCodecVideoDecode=" + useMediaCodecVideoDecode +
                ", useMediaCodecAudioEncode=" + useMediaCodecAudioEncode +
                '}';
    }
}
