package com.tencentcs.iotvideodemo

import android.app.Application
import android.text.TextUtils
import android.widget.Toast

import com.tencentcs.iotvideo.IoTVideoSdk
import com.tencentcs.iotvideo.accountmgr.AccountMgr
import com.tencentcs.iotvideo.utils.UrlHelper
import com.tencentcs.iotvideo.vas.VasMgr
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceModelManager
import com.tencentcs.iotvideodemo.accountmgrtc.httpservice.TencentcsAccountMgr
import com.tencentcs.iotvideodemo.accountmgrtc.httpservice.TencentcsAccountSPUtils
import com.tencentcs.iotvideodemo.utils.AppSPUtils
import com.tencentcs.iotvideodemo.utils.CrashHandler
import com.tencentcs.iotvideodemo.utils.FloatLogWindows
import com.tencentcs.iotvideodemo.utils.StorageManager

import java.io.File

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        StorageManager.init(this)
        if (StorageManager.isDocPathAvailable()) {
            CrashHandler.getInstance().init(applicationContext,
                    StorageManager.getDocPath() + File.separator + "errorLog")
        }

        val defaultServiceType = UrlHelper.SERVER_DEV
        UrlHelper.getInstance().serverType = AppSPUtils.getInstance().getInteger(this, AppSPUtils.SERVER_TYPE, defaultServiceType)

        IoTVideoSdk.init(applicationContext, null)
        if (StorageManager.isDocPathAvailable()) {
            IoTVideoSdk.setLogPath(StorageManager.getDocPath() + File.separator + "xLog")
            IoTVideoSdk.setDebugMode(true, 1)
        }
        if (ENABLE_TENCENTCS) {
            if (TencentcsAccountSPUtils.getInstance().isLogin(this)) {
                val accessId = TencentcsAccountSPUtils.getInstance().getString(this, TencentcsAccountSPUtils.ACCESS_ID, "0")
                val secretId = TencentcsAccountSPUtils.getInstance().getString(this, TencentcsAccountSPUtils.SECRET_ID, "")
                val secretKey = TencentcsAccountSPUtils.getInstance().getString(this, TencentcsAccountSPUtils.SECRET_KEY, "")
                val token = TencentcsAccountSPUtils.getInstance().getString(this, TencentcsAccountSPUtils.TOKEN, "")
                val accessToken = TencentcsAccountSPUtils.getInstance().getString(this, TencentcsAccountSPUtils.ACCESS_TOKEN, "")
                TencentcsAccountMgr.init(secretId, secretKey, token)
                TencentcsAccountMgr.setAccessId(accessId)
                //注册IoTVideo
                IoTVideoSdk.register(java.lang.Long.valueOf(accessId), accessToken)
                //监听物模型变化
                IoTVideoSdk.getMessageMgr().addModelListener(DeviceModelManager.getInstance())
            }
        } else {
            val productId = AppSPUtils.getInstance().getString(this, AppSPUtils.PRODUCT_ID, PRODUCT_ID)
            Toast.makeText(applicationContext, "productId : $productId", Toast.LENGTH_LONG).show()
            AccountMgr.init(productId)
            checkAndAutoLogin()
            VasMgr.init()
        }
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
        const val PRODUCT_ID = "440234147841"
        const val ENABLE_TENCENTCS:Boolean = true
    }
}
