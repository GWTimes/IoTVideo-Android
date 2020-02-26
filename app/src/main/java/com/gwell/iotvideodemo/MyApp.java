package com.gwell.iotvideodemo;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import com.gwell.iotvideo.IoTVideoSdk;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.utils.UrlHelper;
import com.gwell.iotvideo.vas.VasMgr;
import com.gwell.iotvideodemo.accountmgr.AccountSPUtils;
import com.gwell.iotvideodemo.utils.AppSPUtils;
import com.gwell.iotvideodemo.utils.CrashHandler;
import com.gwell.iotvideodemo.utils.FloatLogWindows;

import java.io.File;

public class MyApp extends Application {
    public static String APP_VIDEO_PATH;
    public static String APP_PIC_PATH;
    public static String APP_DOC_PATH;

    public final static int CID = 103;
    public final static String PRODUCT_ID = "440234147841";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
        APP_VIDEO_PATH = getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath();
        APP_PIC_PATH = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
        APP_DOC_PATH = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath();

        if (!AppSPUtils.getInstance().getBoolean(this, AppSPUtils.NEED_SWITCH_SERVER_TYPE, false)) {
            UrlHelper.getInstance().setServerType(BuildConfig.DEBUG ? UrlHelper.SERVER_DEV : UrlHelper.SERVER_RELEASE);
        } else {
            UrlHelper.getInstance().setServerType(
                    AppSPUtils.getInstance().getInteger(this, AppSPUtils.SERVER_TYPE, UrlHelper.SERVER_RELEASE));
        }

        IoTVideoSdk.init(getApplicationContext(), null);
        IoTVideoSdk.setLogPath(MyApp.APP_DOC_PATH + File.separator + "xLog");
        AccountMgr.init(CID, PRODUCT_ID);
        checkAndAutoLogin();
        VasMgr.init();
        if(BuildConfig.DEBUG){
            FloatLogWindows.getInstance().init(this);
        }
    }

    private void checkAndAutoLogin() {
        int validityTime = AccountSPUtils.getInstance().getInteger(this, AccountSPUtils.VALIDITY_TIMESTAMP, 0);
        boolean isLogin = validityTime > (System.currentTimeMillis() / 1000);
        if (isLogin) {
            String realToken = AccountSPUtils.getInstance().getString(this, AccountSPUtils.IV_TOKEN, "");
            String secretKey = AccountSPUtils.getInstance().getString(this, AccountSPUtils.SECRET_KEY, "");
            String accessId = AccountSPUtils.getInstance().getString(this, AccountSPUtils.ACCESS_ID, "");
            if (!TextUtils.isEmpty(realToken) && !TextUtils.isEmpty(secretKey) && !TextUtils.isEmpty(accessId)) {
                IoTVideoSdk.register(Long.valueOf(accessId), realToken + secretKey);
                AccountMgr.setSecretInfo(accessId, secretKey, realToken);
            }
        }
    }
}
