package com.tencentcs.iotvideodemo.utils;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideodemo.BuildConfig;
import com.tencentcs.iotvideodemo.DemoApp;
import com.tencentcs.iotvideodemo.messagemgr.ModelBuildInEntity;

import java.util.ArrayList;
import java.util.List;
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

    public static boolean isOemVersion() {
        return "oem".equals(BuildConfig.FLAVOR);
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
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(msg).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    public static void showToast(String msg) {
        Toast.makeText(DemoApp.Companion.getDemoAppContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 判断是否连接上wifi
     *
     * @param context 上下文
     * @return true 是 false 否
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.isConnected();
    }

    /**
     * 获取正在连接的wifi的所有信息
     *
     * @param context 上下文
     * @return wifi信息
     */
    public static WifiInfo getConnectWifiInfo(Application context) {
        if (!isWifiConnected(context)) {
            return null;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return null;
        }
        WifiInfo wifiInfo;
        wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip 整型ip
     * @return   字符串ip
     */
    public static String intIPToStringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static List<String> getProUserBuildInInfo(String jsonData) {
        ModelBuildInEntity entity = JSONUtils.JsonToEntity(jsonData, ModelBuildInEntity.class);
        if (null == entity || null == entity.getVal()) {
            return new ArrayList<>();
        }
        return entity.getVal().getEditData();
    }


}
