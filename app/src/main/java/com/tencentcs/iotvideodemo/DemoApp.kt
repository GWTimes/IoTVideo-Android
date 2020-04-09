package com.tencentcs.iotvideodemo

import android.app.Application
import com.tencentcs.iotvideo.IoTVideoSdk
import com.tencentcs.iotvideo.accountmgr.AccountMgr
import com.tencentcs.iotvideo.utils.UrlHelper
import com.tencentcs.iotvideo.vas.VasMgr
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceModelManager
import com.tencentcs.iotvideodemo.utils.AppSPUtils
import com.tencentcs.iotvideodemo.utils.StorageManager
import xcrash.XCrash
import java.io.File
import java.util.HashMap

class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        StorageManager.init(this)
        if (StorageManager.isDocPathAvailable()) {
            val xCrashParams = XCrash.InitParameters()
            xCrashParams.setLogDir(StorageManager.getDocPath() + File.separator + "errorLog")
            XCrash.init(this, xCrashParams)
        }

        val defaultServiceType = if (BuildConfig.DEBUG && "oem" == BuildConfig.FLAVOR) UrlHelper.SERVER_DEV else UrlHelper.SERVER_RELEASE
        UrlHelper.getInstance().serverType = AppSPUtils.getInstance().getInteger(this, AppSPUtils.SERVER_TYPE, defaultServiceType)

        IoTVideoSdk.init(applicationContext, null)
        if (StorageManager.isDocPathAvailable()) {
            IoTVideoSdk.setLogPath(StorageManager.getDocPath() + File.separator + "xLog")
            IoTVideoSdk.setDebugMode(IoTVideoSdk.LOG_LEVEL_DEBUG)
        }
        val productId = AppSPUtils.getInstance().getString(this, AppSPUtils.PRODUCT_ID, PRODUCT_ID)
        AccountMgr.init(productId)
        VasMgr.init()
        checkAndAutoLogin()
    }

    private fun checkAndAutoLogin() {
        if (AccountSPUtils.getInstance().isLogin(this)) {
            val secretId = AccountSPUtils.getInstance().getString(this, AccountSPUtils.SECRET_ID, "")
            val secretKey = AccountSPUtils.getInstance().getString(this, AccountSPUtils.SECRET_KEY, "")
            val token = AccountSPUtils.getInstance().getString(this, AccountSPUtils.TOKEN, "")
            val accessId = AccountSPUtils.getInstance().getString(this, AccountSPUtils.ACCESS_ID, "")
            val accessToken = AccountSPUtils.getInstance().getString(this, AccountSPUtils.ACCESS_TOKEN, "")
            //注册IoTVideo
            IoTVideoSdk.register(java.lang.Long.valueOf(accessId), accessToken)
            //注册帐号体系
            AccountMgr.setSecretInfo(secretId, secretKey, token)
            AccountMgr.setAccessInfo(accessId, accessToken)
            //监听物模型变化
            IoTVideoSdk.getMessageMgr().addModelListener(DeviceModelManager.getInstance())
            //设置vas模块公共参数
            val publicParams = HashMap<String, Any>()
            publicParams["accessId"] = AccountMgr.getAccessId()
            VasMgr.updatePublicParams(publicParams)
        }
    }

    companion object {
        const val PRODUCT_ID = "440234147841"
    }
}
