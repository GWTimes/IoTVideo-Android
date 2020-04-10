package com.tencentcs.iotvideodemo.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Utils {
    public static String getPhoneUuid(Context context) {
        String phoneDeviceUuid = AppSPUtils.getInstance().getString(context, AppSPUtils.UNIQUE_ID, null);
        if (TextUtils.isEmpty(phoneDeviceUuid)) {
            phoneDeviceUuid = getPhoneDeviceUuid();
            AppSPUtils.getInstance().putString(context, AppSPUtils.UNIQUE_ID, phoneDeviceUuid);
        }
        return phoneDeviceUuid;
    }

    /**
     * 生成设备唯一标识符
     *
     * @return 设备唯一标识符
     */
    public static String getPhoneDeviceUuid() {
        return UUID.randomUUID().toString();
    }

    public static void setClipboard(Context context, String content) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        if (cm != null) {
            cm.setPrimaryClip(mClipData);
        }
    }

    public static String printJson(String msg) {
        StringBuilder stringBuilder = new StringBuilder();

        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        String lineSeparator = System.getProperty("line.separator");
        String[] lines = message.split(lineSeparator);
        stringBuilder.append("\n");
        for (String line : lines) {
            stringBuilder.append(line).append("\n");
        }

        return stringBuilder.toString();
    }
}
