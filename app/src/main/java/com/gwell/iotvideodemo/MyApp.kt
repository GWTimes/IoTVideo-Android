package com.gwell.iotvideodemo

import android.app.Application
import android.text.TextUtils

import com.gwell.iotvideo.IoTVideoSdk
import com.gwell.iotvideo.accountmgr.AccountMgr
import com.gwell.iotvideo.utils.UrlHelper
import com.gwell.iotvideo.vas.VasMgr
import com.gwell.iotvideodemo.accountmgr.AccountSPUtils
import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceModelManager
import com.gwell.iotvideodemo.utils.AppSPUtils
import com.gwell.iotvideodemo.utils.CrashHandler
import com.gwell.iotvideodemo.utils.FloatLogWindows
import com.gwell.iotvideodemo.utils.StorageManager

import java.io.File

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        StorageManager.init(this)
        if (StorageManager.isDocPathAvailable()) {
            CrashHandler.getInstance().init(applicationContext,
                    StorageManager.getDocPath() + File.separator + "IoTVideo" + File.separator + "errorLog")
        }

        if (!AppSPUtils.getInstance().getBoolean(this, AppSPUtils.NEED_SWITCH_SERVER_TYPE, false)) {
            UrlHelper.getInstance().serverType = UrlHelper.SERVER_RELEASE
        } else {
            UrlHelper.getInstance().serverType = AppSPUtils.getInstance().getInteger(this, AppSPUtils.SERVER_TYPE, UrlHelper.SERVER_RELEASE)
        }

        IoTVideoSdk.init(applicationContext, null)
        if (StorageManager.isDocPathAvailable()) {
            IoTVideoSdk.setLogPath(StorageManager.getDocPath() + File.separator + "xLog")
        }
        AccountMgr.init(CID, PRODUCT_ID)
        checkAndAutoLogin()
        VasMgr.init()
        if (BuildConfig.DEBUG) {
            FloatLogWindows.getInstance().init(this)
        }
    }

    private fun checkAndAutoLogin() {
        if (AccountSPUtils.getInstance().isLogin(this)) {
            val realToken = AccountSPUtils.getInstance().getString(this, AccountSPUtils.TOKEN, "")
            val secretKey = AccountSPUtils.getInstance().getString(this, AccountSPUtils.SECRET_KEY, "")
            val accessId = AccountSPUtils.getInstance().getString(this, AccountSPUtils.ACCESS_ID, "")
            if (!TextUtils.isEmpty(realToken) && !TextUtils.isEmpty(secretKey) && !TextUtils.isEmpty(accessId)) {
                initIoTVideo(realToken, secretKey, accessId)
            }
        }
    }

    private fun initIoTVideo(realToken: String, secretKey: String, accessId: String) {
        //注册IoTVideo
        IoTVideoSdk.register(java.lang.Long.valueOf(accessId), realToken + secretKey)
        //注册帐号体系
        AccountMgr.setSecretInfo(accessId, secretKey, realToken)
        //监听物模型变化
        IoTVideoSdk.getMessageMgr().addModelListener(DeviceModelManager.getInstance())
    }

    companion object {

        const val CID = 103
        const val PRODUCT_ID = "440234147841"
    }
}
