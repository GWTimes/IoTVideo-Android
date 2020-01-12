package com.gwell.iotvideodemo.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.BuildConfig;
import com.gwell.iotvideodemo.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FloatLogWindows {

    public final String TAG = "FloatLogWindows";

    private Context mApplicationContext;
    private int mActivityNum = 0;

    private static class FloatLogWindowsHolder {
        private static final FloatLogWindows INSTANCE = new FloatLogWindows();
    }

    public static FloatLogWindows getInstance() {
        return FloatLogWindowsHolder.INSTANCE;
    }

    private FloatLogWindows() {
    }


    public void init(Application application) {
        if (BuildConfig.DEBUG) {
            mApplicationContext = application.getApplicationContext();
            //悬浮框
            ImageView imageView = new ImageView(mApplicationContext);
            imageView.setImageResource(R.mipmap.ic_launcher);
            FloatWindow
                    .with(mApplicationContext)
                    .setView(imageView)
                    .setWidth(Screen.width, 0.1f) //设置悬浮控件宽高
                    .setHeight(Screen.width, 0.1f)
                    .setX(Screen.width, 0.1f)
                    .setY(Screen.height, 0.2f)
                    .setMoveType(MoveType.active)
                    .setMoveStyle(500, new BounceInterpolator())
                    .setFilter(true, androidx.appcompat.app.AppCompatActivity.class)
                    .setViewStateListener(mViewStateListener)
                    .setPermissionListener(mPermissionListener)
                    .setDesktopShow(false)
                    .build();
            FloatWindow.get().show();
            imageView.setOnClickListener(showListener);

            application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                    mActivityNum++;
                    if(FloatWindow.get() != null){
                        FloatWindow.get().show();
                    } else if(FloatWindow.get("LogWindow") != null){
                        FloatWindow.get("LogWindow").show();
                    }
                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {

                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {
                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {
                    mActivityNum--;
                    if (mActivityNum == 0) {
                        if(FloatWindow.get() != null){
                            FloatWindow.get().hide();
                        } else if(FloatWindow.get("LogWindow") != null){
                            FloatWindow.get("LogWindow").hide();
                        }
                    }
                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {

                }
            });

        }




    }

    public void destory() {

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
            //LogUtils.d(TAG, "onShow");
        }

        @Override
        public void onHide() {
            //LogUtils.d(TAG, "onHide");
        }

        @Override
        public void onDismiss() {
            //LogUtils.d(TAG, "onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
            //LogUtils.d(TAG, "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            //LogUtils.d(TAG, "onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
            //LogUtils.d(TAG, "onBackToDesktop");
        }
    };

    private LogCollectorThread mLogCollectorThread = null;
    private View.OnClickListener showListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FloatWindow.destroy();

            final View view = LayoutInflater.from(mApplicationContext).inflate(R.layout.layout_float_window, null);
            //初始化全局悬浮框
            FloatWindow
                    .with(mApplicationContext)
                    .setView(view)
                    .setWidth(Screen.width, 0.9f) //设置悬浮控件宽高
                    .setHeight(Screen.height, 0.5f)
                    .setX(Screen.width, 0.05f)
                    .setY(Screen.height, 0.3f)
                    .setMoveType(MoveType.active)
                    .setMoveStyle(500, new BounceInterpolator())
                    .setFilter(true, androidx.appcompat.app.AppCompatActivity.class)
                    .setViewStateListener(mViewStateListener)
                    .setPermissionListener(mPermissionListener)
                    .setDesktopShow(false)
                    .setTag("LogWindow")
                    .build();
            FloatWindow.get("LogWindow").show();
            ((TextView) (view.findViewById(R.id.result_txt))).setMovementMethod(ScrollingMovementMethod.getInstance());
            view.findViewById(R.id.tv_hide).setOnClickListener(hideListener);
            view.findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TextView) (view.findViewById(R.id.result_txt))).setText("");
                }
            });

            mLogCollectorThread = new LogCollectorThread();
            mLogCollectorThread.start();
        }
    };

    private View.OnClickListener hideListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FloatWindow.destroy("LogWindow");
            ImageView iconView = new ImageView(mApplicationContext);
            iconView.setImageResource(R.mipmap.ic_launcher);
            FloatWindow
                    .with(mApplicationContext)
                    .setView(iconView)
                    .setWidth(Screen.width, 0.1f) //设置悬浮控件宽高
                    .setHeight(Screen.width, 0.1f)
                    .setX(Screen.width, 0.1f)
                    .setY(Screen.height, 0.2f)
                    .setMoveType(MoveType.active)
                    .setMoveStyle(500, new BounceInterpolator())
                    .setFilter(true, androidx.appcompat.app.AppCompatActivity.class)
                    .setViewStateListener(mViewStateListener)
                    .setPermissionListener(mPermissionListener)
                    .setDesktopShow(false)
                    .build();
            iconView.setOnClickListener(showListener);
            FloatWindow.get().show();

            if(mLogCollectorThread != null){
                mLogCollectorThread.stopLogCollector();
                mLogCollectorThread = null;
            }
        }
    };


    class LogCollectorThread extends Thread {
        public LogCollectorThread() {
            super("LogCollectorThread");
            Log.d(TAG, "LogCollectorThread is create");
        }

        @Override
        public void run() {
            //清楚日志缓存
            clearLogCache();

            //获取进程信息 杀掉logcat进程
            killLogcatProcess();

            //收集日志
            startLogCollector();
        }

        /**
         * 每次记录日志之前先清除日志的缓存, 不然会在两个日志文件中记录重复的日志
         */
        private void clearLogCache() {
            Process proc = null;
            List<String> commandList = new ArrayList<String>();
            commandList.add("logcat");
            commandList.add("-c");
            try {
                proc = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
                StreamConsumer errorGobbler = new StreamConsumer(proc.getErrorStream());
                StreamConsumer outputGobbler = new StreamConsumer(proc.getInputStream());
                errorGobbler.start();
                outputGobbler.start();
                if (proc.waitFor() != 0) {
                    Log.e(TAG, " clearLogCache proc.waitFor() != 0");
                }
            } catch (Exception e) {
                Log.e(TAG, "clearLogCache failed", e);
            } finally {
                try {
                    proc.destroy();
                } catch (Exception e) {
                    Log.e(TAG, "clearLogCache failed", e);
                }
            }
        }

        private Process mProcess;

        /**
         * 关闭由本程序开启的logcat进程： 根据用户名称杀死进程(如果是本程序进程开启的Logcat收集进程那么两者的USER一致)
         * 如果不关闭会有多个进程读取logcat日志缓存信息写入日志文件
         *
         * @return
         */
        private void killLogcatProcess() {
            if (mProcess != null) {
                mProcess.destroy();
            }

            List<String> orgProcessList = getAllProcess();
            List<ProcessInfo> allProcessList = getProcessInfoList(orgProcessList);

            String packName = mApplicationContext.getPackageName();
            String myUser = getAppUser(packName, allProcessList);
            /*
             * recordLogServiceLog("app user is:"+myUser);
             * recordLogServiceLog("========================"); for (ProcessInfo
             * processInfo : allProcList) {
             * recordLogServiceLog(processInfo.toString()); }
             * recordLogServiceLog("========================");
             */
            for (ProcessInfo processInfo : allProcessList) {
                if (processInfo.name.toLowerCase(mApplicationContext.getResources().getConfiguration().locale).equals("logcat")
                        && processInfo.user.equals(myUser)) {
                    android.os.Process.killProcess(Integer.parseInt(processInfo.pid));
                    // recordLogServiceLog("kill another logcat mProcess success,the mProcess info is:"
                    // + processInfo);
                }
            }
        }

        /**
         * 开始收集日志信息
         */
        private void startLogCollector() {
            List<String> commandList = new ArrayList<String>();
            commandList.add("logcat");
            commandList.add("-v");
            commandList.add("time");
            commandList.add("*:V");
            // commandList.add("*:E");// 过滤所有的错误信息
            // 过滤指定TAG的信息
            // commandList.add("MyAPP:V");
            // commandList.add("*:S");
            try {
                mProcess = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
                StreamConsumer errorGobbler = new StreamConsumer(mProcess.getErrorStream(), true);
                StreamConsumer outputGobbler = new StreamConsumer(mProcess.getInputStream(), true);
                errorGobbler.start();
                outputGobbler.start();
            } catch (Exception e) {
                Log.e(TAG, "CollectorThread == >" + e.getMessage(), e);
            }
        }

        public void stopLogCollector(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    killLogcatProcess();
                }
            }).start();
        }

        /**
         * 获取本程序的用户名称
         *
         * @param packName
         * @param allProcList
         * @return
         */
        private String getAppUser(String packName, List<ProcessInfo> allProcList) {
            for (ProcessInfo processInfo : allProcList) {
                if (processInfo.name.equals(packName)) {
                    return processInfo.user;
                }
            }
            return null;
        }

        /**
         * 运行PS命令得到进程信息
         *
         * @return USER PID PPID VSIZE RSS WCHAN PC NAME root 1 0 416 300 c00d4b28
         * 0000cd5c S /init
         */
        private List<String> getAllProcess() {
            List<String> orgProcessList = new ArrayList<String>();
            Process proc = null;
            try {
                proc = Runtime.getRuntime().exec("ps");
                StreamConsumer errorConsumer = new StreamConsumer(proc.getErrorStream());
                StreamConsumer outputConsumer = new StreamConsumer(proc.getInputStream(), orgProcessList);
                errorConsumer.start();
                outputConsumer.start();
                if (proc.waitFor() != 0) {
                    Log.e(TAG, "getAllProcess proc.waitFor() != 0");
                }
            } catch (Exception e) {
                Log.e(TAG, "getAllProcess failed", e);
            } finally {
                try {
                    proc.destroy();
                } catch (Exception e) {
                    Log.e(TAG, "getAllProcess failed", e);
                }
            }
            return orgProcessList;
        }

        /**
         * 根据ps命令得到的内容获取PID，User，name等信息
         *
         * @param orgProcessList
         * @return
         */
        private List<ProcessInfo> getProcessInfoList(List<String> orgProcessList) {
            List<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();
            for (int i = 1; i < orgProcessList.size(); i++) {
                String processInfo = orgProcessList.get(i);
                String[] proStr = processInfo.split(" ");
                // USER PID PPID VSIZE RSS WCHAN PC NAME
                // root 1 0 416 300 c00d4b28 0000cd5c S /init
                List<String> orgInfo = new ArrayList<String>();
                for (String str : proStr) {
                    if (!"".equals(str)) {
                        orgInfo.add(str);
                    }
                }
                if (orgInfo.size() == 9) {
                    ProcessInfo pInfo = new ProcessInfo();
                    pInfo.user = orgInfo.get(0);
                    pInfo.pid = orgInfo.get(1);
                    pInfo.ppid = orgInfo.get(2);
                    pInfo.name = orgInfo.get(8);
                    procInfoList.add(pInfo);
                }
            }
            return procInfoList;
        }
    }

    class StreamConsumer extends Thread {
        InputStream is;
        List<String> list;
        boolean isShow = false;

        StreamConsumer(InputStream is) {
            this.is = is;
        }

        StreamConsumer(InputStream is, List<String> list) {
            this.is = is;
            this.list = list;
        }

        StreamConsumer(InputStream is, boolean isShow) {
            this.is = is;
            this.isShow = isShow;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr, 2048);
                String line;
                while ((line = br.readLine()) != null) {
                    if (isShow && FloatWindow.get("LogWindow") != null && FloatWindow.get("LogWindow").isShowing()) {
                        if (!((line.contains("IoTVideo-") || line.contains("P2PLIB")))) {
                            continue;
                        }
                        final String lineText = line;
                        final TextView output = (FloatWindow.get("LogWindow").getView().findViewById(R.id.result_txt));
                        output.post(new Runnable() {
                            @Override
                            public void run() {
                                output.append(lineText);
                                int offset = output.getLineCount() * output.getLineHeight();
                                if (offset > output.getHeight()) {
                                    output.scrollTo(0, offset - output.getHeight());
                                }
                            }
                        });
                    } else {
                        if (list != null) {
                            list.add(line);
                        }
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    class ProcessInfo {
        public String user;
        public String pid;
        public String ppid;
        public String name;

        @Override
        public String toString() {
            String str = "user=" + user + " pid=" + pid + " ppid=" + ppid
                    + " name=" + name;
            return str;
        }
    }

}
