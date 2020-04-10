package com.tencentcs.iotvideodemo.kt.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import androidx.appcompat.app.AppCompatActivity
import com.tencentcs.iotvideo.utils.LogUtils
import com.tencentcs.iotvideo.utils.UrlHelper
import com.tencentcs.iotvideodemo.R
import com.tencentcs.iotvideodemo.utils.StatusBarUtils
import com.tencentcs.iotvideodemo.kt.utils.ViewUtils
import com.tencentcs.iotvideodemo.kt.widget.dialog.LoadingDialog
import com.tencentcs.iotvideodemo.rxbus2.RxBus
import com.tbruyelle.rxpermissions2.RxPermissions

abstract class BaseActivity : AppCompatActivity(), IBaseView {
    protected val TAG = javaClass.simpleName

    private val activityConfig = ActivityConfig()

    var mRxPermission: RxPermissions? = null

    val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.i(TAG, "OnCreate")

        ViewUtils.application = application

        initActivityConfig(activityConfig)

        if (activityConfig.isHideNavigation) {
            hideNavigationBar()
        }

        setContentView(getResId())

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        if (!UrlHelper.getInstance().isRelease) {
            title = title
        }

        if (activityConfig.isUseStatusBar) {
            StatusBarUtils.setColor(this, getStatusBarColor(), 0)
        }

        //保持屏幕亮
        if (activityConfig.keepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        if (activityConfig.isApplyRxBus) {
            RxBus.getDefault().register(this)
        }

        if (activityConfig.isPortraitOrientation) {
            val lp = window.attributes
            //修复BUG：  Only fullscreen activities can request orientation
            if (lp.flags == FLAG_FULLSCREEN) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        //状态栏颜色在透明时设置为深色
        if (activityConfig.isStatusBarTransparent) {
            setStatusBarDarkMode()
        }

        // 布局延伸到状态栏
        if (activityConfig.isSteep) {
            StatusBarUtils.setTransparentForImageView(this, null)
            setStatusBarDarkMode()
        }

        try {
            //关闭自动旋转
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
        } catch (e: Exception) {
            e.printStackTrace()
        }

        init(savedInstanceState)
    }

    /**
     * 配置选项
     */
    protected open fun initActivityConfig(activityConfig: ActivityConfig) {

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun setTitle(title: CharSequence?) {
        if (!UrlHelper.getInstance().isRelease) {
            super.setTitle("${title}(测试环境)")
        } else {
            super.setTitle(title)
        }
    }

    override fun hideLoadingDialog() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    override fun showLoadingDialog(msg: String?, cancelCancel: Boolean) {
        if (isFinishing) {
            return
        }
        loadingDialog.setCancelable(cancelCancel)
        if (msg.isNullOrEmpty()) {
            loadingDialog.show()
        } else {
            loadingDialog.show(msg)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus and activityConfig.isHideNavigation) hideNavigationBar()
    }


    override fun onDestroy() {
        super.onDestroy()
        LogUtils.i(TAG, "onDestroy")
        hideLoadingDialog()
        if (activityConfig.isApplyRxBus) RxBus.getDefault().unregister(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 页面名称
     */
    open val pageName: String
        get() = javaClass.simpleName

    /**
     * 获取状态栏颜色
     */
    open fun getStatusBarColor(): Int = ViewUtils.getColor(R.color.colorPrimary)

    /**
     * 设置状态栏Dark模式
     */
    fun setStatusBarDarkMode() {
        StatusBarUtils.setStatusBarDark(this, true)
    }

    /**
     * 设置状态栏Light模式
     */
    fun setStatusBarLightMode() {
        StatusBarUtils.setStatusBarDark(this, false)
    }

    /**
     * 是否是刘海屏
     */
    fun hasNotchInScreen(): Boolean {
        var res = false
        try {
            res = hasNotchInHW() || hasNotchInOppo() || hasNotchInVivo()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }

    /**
     * 隐藏导航栏
     */
    fun hideNavigationBar() {
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
        decorView.systemUiVisibility = uiOptions
    }

    /**
     * 显示导航栏
     */
    fun showNavigationBar() {
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                )
        decorView.systemUiVisibility = uiOptions
    }

    /**
     * 隐藏状态栏
     */
    fun hideStatusBar() {
        val decorView = window.decorView
        val systemUiVisibility = decorView.systemUiVisibility
        val uiOptions = (systemUiVisibility
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
        decorView.systemUiVisibility = uiOptions
    }

    /**
     * 显示状态栏
     */
    fun showStatusBar() {
        val decorView = window.decorView
        val systemUiVisibility = decorView.systemUiVisibility
        val uiOptions = (systemUiVisibility and (View.SYSTEM_UI_FLAG_FULLSCREEN.inv()))
        decorView.systemUiVisibility = uiOptions
    }

    @Volatile
    private var mHasCheckFullScreen: Boolean = false
    @Volatile
    private var mIsFullScreenDevice: Boolean = false

    /**
     * 判断是否是全面屏
     */
    fun isFullScreenDevice(context: Context): Boolean {
        if (mHasCheckFullScreen) {
            return mIsFullScreenDevice
        }
        mHasCheckFullScreen = true
        mIsFullScreenDevice = false
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        val width: Float
        val height: Float
        if (point.x < point.y) {
            width = point.x.toFloat()
            height = point.y.toFloat()
        } else {
            width = point.y.toFloat()
            height = point.x.toFloat()
        }
        if (height / width >= 1.97f) {
            mIsFullScreenDevice = true
        }

        return mIsFullScreenDevice
    }

    private fun hasNotchInHW(): Boolean {
        var ret = false
        try {
            val cl = classLoader
            val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = HwNotchSizeUtil.getMethod("hasNotchInScreen")
            ret = get.invoke(HwNotchSizeUtil) as Boolean
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return ret
        }
    }

    private fun hasNotchInOppo(): Boolean {
        return packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
    }

    @SuppressLint("PrivateApi")
    private fun hasNotchInVivo(): Boolean {
        var hasNotch = false
        try {
            val cl = classLoader
            val ftFeature = cl.loadClass("android.util.FtFeature")
            val methods = ftFeature.declaredMethods
            for (i in methods.indices) {
                val method = methods[i]
                if (method.name.equals("isFeatureSupport", true)) {
                    hasNotch = method.invoke(ftFeature, 0x00000020) as Boolean
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            hasNotch = false
        }
        return hasNotch
    }


    protected class ActivityConfig : AppConfig() {
        /**
         * 状态栏透明
         */
        var isStatusBarTransparent = false

        /**
         * 竖屏
         */
        var isPortraitOrientation = true

        /**
         * 隐藏导航栏
         */
        var isHideNavigation = false

        /**
         * 使用状态栏
         */
        var isUseStatusBar = false

        /**
         * 屏幕常亮
         */
        var keepScreenOn = false

        /**
         * 布局延伸到状态栏
         */
        var isSteep = false
    }
}
