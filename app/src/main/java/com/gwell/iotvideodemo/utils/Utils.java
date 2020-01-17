package com.gwell.iotvideodemo.utils;

import android.content.Context;
import android.text.TextUtils;

import com.gwell.iotvideodemo.accountmgr.AccountSPUtils;

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
}
