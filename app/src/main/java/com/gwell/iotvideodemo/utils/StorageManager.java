package com.gwell.iotvideodemo.utils;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StorageManager {
    private static String APP_VIDEO_PATH;
    private static String APP_PIC_PATH;
    private static String APP_DOC_PATH;

    public static void init(Application app) {
        File videoDirectory = app.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File picDirectory = app.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File docDirectory = app.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (videoDirectory != null && videoDirectory.exists()) {
            APP_VIDEO_PATH = videoDirectory.getPath();
        }
        if (picDirectory != null && picDirectory.exists()) {
            APP_PIC_PATH = picDirectory.getPath();
        }
        if (docDirectory != null && docDirectory.exists()) {
            APP_DOC_PATH = docDirectory.getPath();
        }
    }

    public static boolean isVideoPathAvailable() {
        return !TextUtils.isEmpty(APP_VIDEO_PATH);
    }

    public static boolean isPicPathAvailable() {
        return !TextUtils.isEmpty(APP_PIC_PATH);
    }

    public static boolean isDocPathAvailable() {
        return !TextUtils.isEmpty(APP_DOC_PATH);
    }

    public static String getVideoPath() {
        return APP_VIDEO_PATH;
    }

    public static String getPicPath() {
        return APP_PIC_PATH;
    }

    public static String getDocPath() {
        return APP_DOC_PATH;
    }

    public static boolean isFileExists(String strFile) {
        try {
            File file = new File(strFile);
            if (!file.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }


    public static boolean isSDCardVailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    public static void copyToSDCard(Context context, String assertFile, String outFile) throws IOException {
        OutputStream outputStream = new FileOutputStream(outFile);
        InputStream inputStream = context.getAssets().open(assertFile);
        byte[] buffer = new byte[1024];
        int length = inputStream.read(buffer);
        while (length > 0) {
            outputStream.write(buffer, 0, length);
            length = inputStream.read(buffer);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
    }

}
