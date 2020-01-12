package com.gwell.iotvideodemo;

import android.app.Application;
import android.os.Environment;
import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideodemo.utils.CrashHandler;
import com.gwell.iotvideodemo.utils.FloatLogWindows;

import java.io.File;

public class MyApp extends Application {
    public static String APP_VIDEO_PATH;
    public static String APP_PIC_PATH;
    public static String APP_DOC_PATH;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
        APP_VIDEO_PATH = getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath();
        APP_PIC_PATH = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        APP_DOC_PATH = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath();
        IoTVideoSdk.init(getApplicationContext(), 103, "440234147841");
        IoTVideoSdk.setLogPath(MyApp.APP_DOC_PATH + File.separator + "xLog");
        if(BuildConfig.DEBUG){
            FloatLogWindows.getInstance().init(this);
        }
    }
}
