package com.tencentcs.iotvideodemo.accountmgrtc.httpservice;

import com.tencentcs.iotvideo.utils.LogUtils;

public class TencentcsAccountMgr {
    private static final String TAG = "TencentcsAccountMgr";

    private static TencentcsHttpServiceAdapter mHttpService;
    private static String sSecretId;
    private static String sSecretKey;
    private static String sToken;
    private static String sAccessId;

    public static void init(String secretId, String secretKey, String token) {
        LogUtils.i(TAG, "init secretId = " + secretId + " secretKey = " + secretKey + " token = " + token);
        mHttpService = new TencentcsHttpServiceAdapter();
        mHttpService.init(secretId, secretKey, token);
        sSecretId = secretId;
        sSecretKey = secretKey;
        sToken = token;
    }

    public static TencentcsHttpService getHttpService() {
        if (mHttpService == null) {
            throw new IllegalStateException("please init AccountMgr first!");
        }
        return mHttpService;
    }

    public static String getSecretId() {
        return sSecretId;
    }

    public static String getSecretKey() {
        return sSecretKey;
    }

    public static String getToken() {
        return sToken;
    }

    public static String getAccessId() {
        return sAccessId;
    }

    public static void setAccessId(String accessId) {
        sAccessId = accessId;
    }
}