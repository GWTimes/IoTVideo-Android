package com.gwell.iotvideodemo.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

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
