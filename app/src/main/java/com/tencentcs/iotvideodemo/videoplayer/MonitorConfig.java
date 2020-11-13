package com.tencentcs.iotvideodemo.videoplayer;

import android.content.Context;

import com.tencentcs.iotvideodemo.settings.DeviceSettingsSPUtils;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class MonitorConfig implements Serializable {
    public boolean supportTalk;
    public boolean supportCamera;
    public boolean useMediaCodecAudioDecode;
    public boolean useMediaCodecVideoDecode;
    public boolean useMediaCodecAudioEncode;
    public int definition;
    public short sourceId;

    public static MonitorConfig defaultConfig(Context context) {
        MonitorConfig config = new MonitorConfig();
        config.supportTalk = DeviceSettingsSPUtils.getInstance().supportAudioTalk(context);
        config.supportCamera = DeviceSettingsSPUtils.getInstance().supportVideoTalk(context);
        config.useMediaCodecAudioDecode = DeviceSettingsSPUtils.getInstance().media_decode_audio(context);
        config.useMediaCodecVideoDecode = DeviceSettingsSPUtils.getInstance().media_decode_video(context);
        config.useMediaCodecAudioEncode = DeviceSettingsSPUtils.getInstance().media_encode_audio(context);
        config.definition = DeviceSettingsSPUtils.getInstance().default_definition(context);
        config.sourceId = DeviceSettingsSPUtils.getInstance().default_sourceId(context);

        return config;
    }

    public static MonitorConfig simpleConfig(Context context) {
        MonitorConfig config = MonitorConfig.defaultConfig(context);
        config.supportCamera = false;
        config.sourceId = 0;

        return config;
    }

    public static MonitorConfig simpleConfig(Context context, short defaultSourceId) {
        MonitorConfig config = MonitorConfig.defaultConfig(context);
        config.supportCamera = false;
        config.sourceId = defaultSourceId;

        return config;
    }

    public static boolean compare(MonitorConfig config1, MonitorConfig config2) {
        if (config1 == null || config2 == null) {
            return false;
        }
        return config1.supportTalk == config2.supportTalk
                && config1.supportCamera == config2.supportCamera
                && config1.useMediaCodecAudioDecode == config2.useMediaCodecAudioDecode
                && config1.useMediaCodecVideoDecode == config2.useMediaCodecVideoDecode
                && config1.useMediaCodecAudioEncode == config2.useMediaCodecAudioEncode
                && config1.definition == config2.definition
                && config1.sourceId == config2.sourceId;
    }

    @NonNull
    @Override
    public String toString() {
        return "MonitorConfig{" +
                "supportTalk=" + supportTalk +
                ", supportCamera=" + supportCamera +
                ", useMediaCodecAudioDecode=" + useMediaCodecAudioDecode +
                ", useMediaCodecVideoDecode=" + useMediaCodecVideoDecode +
                ", useMediaCodecAudioEncode=" + useMediaCodecAudioEncode +
                ", definition=" + definition +
                ", sourceId=" + sourceId +
                '}';
    }
}
