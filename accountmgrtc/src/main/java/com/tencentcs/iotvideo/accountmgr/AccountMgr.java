package com.tencentcs.iotvideo.accountmgr;

import com.tencentcs.iotvideo.http.TencentcsHttpServiceAdapter;
import com.tencentcs.iotvideo.utils.LogUtils;

public class AccountMgr {
    private static final String TAG = "AccountMgr";

    private static TencentcsHttpServiceAdapter mHttpService;
    private static String sProductId;
    private static String sSecretId;
    private static String sSecretKey;
    private static String sToken;
    private static String sAccessId;
    private static String sAccessToken;

    public static void init(String productId) {
        LogUtils.i(TAG, "init version is " + BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE +
            " productId = " + productId);
        sProductId = productId;
    }

    public static void setSecretInfo(String secretId, String secretKey, String token) {
        LogUtils.i(TAG, "setSecretInfo secretId = " + secretId + " secretKey = " + secretKey + " token = " + token);
        mHttpService = new TencentcsHttpServiceAdapter();
        mHttpService.init(secretId, secretKey, token);
        sSecretId = secretId;
        sSecretKey = secretKey;
        sToken = token;
    }

    public static void setAccessInfo(String accessId, String accessToken) {
        LogUtils.i(TAG, "setSecretInfo accessId = " + accessId + "accessToken" + accessToken);
        sAccessId = accessId;
        sAccessToken = accessToken;
        TencentcsHttpServiceAdapter.setAccessId(accessId);
    }

    public static HttpService getHttpService() {
        if (mHttpService == null) {
            throw new IllegalStateException("please init AccountMgr first!");
        }
        return mHttpService;
    }

    public static String getProductId() {
        return sProductId;
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

    public static String getAccessToken() {
        return sAccessToken;
    }
}