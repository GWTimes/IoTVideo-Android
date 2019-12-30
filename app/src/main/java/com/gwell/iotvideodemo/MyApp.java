package com.gwell.iotvideodemo;

import android.app.Application;

import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideodemo.utils.CrashHandler;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IoTVideoSdk.init(getApplicationContext(), 102, "440234147841");
        CrashHandler.getInstance().init(getApplicationContext());
    }
}
