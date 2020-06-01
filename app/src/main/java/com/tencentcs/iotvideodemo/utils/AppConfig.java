package com.tencentcs.iotvideodemo.utils;

import com.tencentcs.iotvideo.iotvideoplayer.CallTypeEnum;

public class AppConfig {
    public static boolean SUPPORT_TALK = true;
    public static boolean SUPPORT_CAMERA = false;
    public static boolean USE_MEDIACODEC_AUDIO_DECODE = false;
    public static boolean USE_MEDIACODEC_VIDEO_DECODE = false;
    public static boolean USE_MEDIACODEC_AUDIO_ENCODE = false;
    public static int MONITOR_DEFINITION = CallTypeEnum.VIDEO_DEFINITION_HD;
}
