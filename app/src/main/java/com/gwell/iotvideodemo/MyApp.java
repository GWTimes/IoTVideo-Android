package com.gwell.iotvideodemo;

import android.app.Application;

import com.gwell.iotvideo.IotVideoSdk;
import com.gwell.iotvideodemo.utils.CrashHandler;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        IotVideoSdk.init(getApplicationContext(), 102, "440234147841");
        CrashHandler.getInstance().init(getApplicationContext());
    }
}
