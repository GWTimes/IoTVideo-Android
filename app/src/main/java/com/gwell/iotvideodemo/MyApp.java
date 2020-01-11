package com.gwell.iotvideodemo;

import android.app.Application;

import android.os.Environment;import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.utils.CrashHandler;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import java.io.File;

public class MyApp extends Application {
    public static String APP_VIDEO_PATH;
    public static String APP_PIC_PATH;
    public static String APP_DOC_PATH;
    public final String TAG = "MyApp";
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
        APP_VIDEO_PATH = getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath();
        APP_PIC_PATH = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        APP_DOC_PATH = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath();
        IoTVideoSdk.init(getApplicationContext(), 103, "440234147841");
        IoTVideoSdk.setLogPath(MyApp.APP_DOC_PATH + File.separator + "xLog");

//        if(BuildConfig.DEBUG){
//            //悬浮框
//            ImageView imageView = new ImageView(getApplicationContext());
//            imageView.setImageResource(R.mipmap.ic_launcher);
//            FloatWindow
//                    .with(getApplicationContext())
//                    .setView(imageView)
//                    .setWidth(Screen.width, 0.1f) //设置悬浮控件宽高
//                    .setHeight(Screen.width, 0.1f)
//                    .setX(Screen.width, 0.1f)
//                    .setY(Screen.height, 0.2f)
//                    .setMoveType(MoveType.slide)
//                    .setMoveStyle(500, new BounceInterpolator())
//                    .setFilter(true, com.gwell.iotvideodemo.base.BaseActivity.class, com.gwell.iotvideodemo.kt.base.BaseActivity.class)
//                    .setViewStateListener(mViewStateListener)
//                    .setPermissionListener(mPermissionListener)
//                    .setDesktopShow(false)
//                    .build();
//            FloatWindow.get().show();
//            imageView.setOnClickListener(showListener);
//        }
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {
            LogUtils.d(TAG, "onSuccess");
        }

        @Override
        public void onFail() {
            LogUtils.d(TAG, "onFail");
        }
    };

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            //LogUtils.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            LogUtils.d(TAG, "onShow");
        }

        @Override
        public void onHide() {
            LogUtils.d(TAG, "onHide");
        }

        @Override
        public void onDismiss() {
            LogUtils.d(TAG, "onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
            LogUtils.d(TAG, "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            LogUtils.d(TAG, "onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
            LogUtils.d(TAG, "onBackToDesktop");
        }
    };

    private View.OnClickListener showListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FloatWindow.destroy();

            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_float_window, null);
            //初始化全局悬浮框
            FloatWindow
                    .with(getApplicationContext())
                    .setView(view)
                    .setWidth(Screen.width, 0.9f) //设置悬浮控件宽高
                    .setHeight(Screen.height, 0.6f)
                    .setX(Screen.width, 0.05f)
                    .setY(Screen.height, 0.3f)
                    .setMoveType(MoveType.slide)
                    .setMoveStyle(500, new BounceInterpolator())
                    .setFilter(true, com.gwell.iotvideodemo.base.BaseActivity.class, com.gwell.iotvideodemo.kt.base.BaseActivity.class)
                    .setViewStateListener(mViewStateListener)
                    .setPermissionListener(mPermissionListener)
                    .setDesktopShow(false)
                    .setTag("LogWindow")
                    .build();
            FloatWindow.get("LogWindow").show();
            view.findViewById(R.id.tv_hide).setOnClickListener(hideListener);
        }
    };

    private View.OnClickListener hideListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            FloatWindow.destroy("LogWindow");
            ImageView iconView = new ImageView(getApplicationContext());
            iconView.setImageResource(R.mipmap.ic_launcher);
            FloatWindow
                    .with(getApplicationContext())
                    .setView(iconView)
                    .setWidth(Screen.width, 0.1f) //设置悬浮控件宽高
                    .setHeight(Screen.width, 0.1f)
                    .setX(Screen.width, 0.1f)
                    .setY(Screen.height, 0.2f)
                    .setMoveType(MoveType.slide)
                    .setMoveStyle(500, new BounceInterpolator())
                    .setFilter(true, com.gwell.iotvideodemo.base.BaseActivity.class, com.gwell.iotvideodemo.kt.base.BaseActivity.class)
                    .setViewStateListener(mViewStateListener)
                    .setPermissionListener(mPermissionListener)
                    .setDesktopShow(false)
                    .build();
            iconView.setOnClickListener(showListener);
            FloatWindow.get().show();
        }
    };
}
