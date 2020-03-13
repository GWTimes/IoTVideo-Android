package com.tencentcs.iotvideodemo.kt.utils

import android.app.Activity
import android.app.Application
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

object ViewUtils {

    private val MIN_WVGA_HEIGHT = 700
    private val WVGA_HEIGHT = 800
    private val MIN_HD_HEIGHT = 1180
    private val HD_HEIGHT = 1280

    lateinit var application: Application

    val resources: Resources
        get() = application.resources

    val phonePixels: IntArray
        get() {
            val metrics = resources.displayMetrics
            val curWidth = metrics.widthPixels
            var curHeight = metrics.heightPixels
            if (curHeight in MIN_WVGA_HEIGHT..WVGA_HEIGHT) {
                curHeight = WVGA_HEIGHT
            }
            if (curHeight in MIN_HD_HEIGHT..HD_HEIGHT) {
                curHeight = HD_HEIGHT
            }
            return intArrayOf(curWidth, curHeight)
        }

    val screenWidth: Int
        get() = phonePixels[0]

    val screenHeight: Int
        get() = phonePixels[1] + navigationBarHeight

    /**
     * 获取导航栏的高度
     *
     * @return 如果没有导航栏，返回0
     */
    val navigationBarHeight: Int
        get() {
            if (!checkDeviceHasNavigationBar()) {
                return 0
            }
            val resources = application.resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

    /**
     * 状态栏高度
     */
    val statusBarHeight: Int
        get() {
            var statusBarHeight = 0
            try {
                val c: Class<*>? = Class.forName("com.android.internal.R\$dimen")
                val x: Int
                val obj = c!!.newInstance()
                val cFiled = c.getField("status_bar_height")
                x = Integer.parseInt(cFiled!!.get(obj).toString())
                statusBarHeight = application.resources.getDimensionPixelSize(x)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }

            return statusBarHeight
        }

    val density: Float
        get() = Resources.getSystem().displayMetrics.density

    val densityDpi: Int
        get() = Resources.getSystem().displayMetrics.densityDpi

    fun inflateView(activity: Activity, layoutId: Int): View {
        return LayoutInflater.from(activity).inflate(layoutId, null)
    }

    fun inflateView(activity: Activity, layoutId: Int, root: ViewGroup): View {
        return LayoutInflater.from(activity).inflate(layoutId, root, false)
    }

    fun getDimenPx(resId: Int): Int {
        return resources.getDimensionPixelSize(resId)
    }

    fun getDrawable(resId: Int): Drawable {
        return ContextCompat.getDrawable(application, resId)!!
    }

    fun getResourceArray(arrayId: Int): IntArray {

        val array = resources.obtainTypedArray(arrayId)
        val len = array.length()
        val resIds = IntArray(len)
        for (i in 0 until len) {
            resIds[i] = array.getResourceId(i, 0)
        }
        array.recycle()
        return resIds
    }

    fun getString(@StringRes stringId: Int): String {
        return try {
            application.getString(stringId)
        } catch (e: Exception) {
            ""
        }
    }

    fun getString(resId: Int, vararg formatArgs: Any): String {
        return resources.getString(resId, *formatArgs)
    }

    fun getColor(colorId: Int): Int {
        return ContextCompat.getColor(application, colorId)
    }

    fun dip2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun dip2pxReal(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    fun px2dip(pxValue: Float): Int {
        val scale = resources.displayMetrics.density
        return ((pxValue - 0.5f) / scale).toInt()
    }

    fun setPaddingTop(view: View?, paddingTop: Int) {
        view?.setPadding(view.paddingLeft, paddingTop, view.paddingRight, view.paddingBottom)
    }

    fun setPaddingLeft(view: View?, paddingLeft: Int) {
        view?.setPadding(paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)
    }

    fun setPaddingRight(view: View?, paddingRight: Int) {
        view?.setPadding(view.paddingLeft, view.paddingTop, paddingRight, view.paddingBottom)
    }

    fun setpaddingBottom(view: View?, paddingBottom: Int) {
        view?.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, paddingBottom)
    }

    fun setMarginTop(view: View, marginTop: Int) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(params.leftMargin, marginTop, params.rightMargin, params.bottomMargin)
        view.layoutParams = params
    }

    fun setMarginBottom(view: View, marginBottom: Int) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, marginBottom)
        view.layoutParams = params
    }

    fun setMarginLeft(view: View, marginLeft: Int) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = marginLeft
        view.layoutParams = params
    }

    fun setMarginRight(view: View, marginRight: Int) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        params.marginEnd = marginRight
        view.layoutParams = params
    }

    fun setViewBackground(background: Bitmap?, view: View) {
        background?.apply {
            val bitmapDrawable = BitmapDrawable(resources, this)
            setViewBackgroundDrawable(bitmapDrawable, view)
        }
    }

    fun setViewBackgroundDrawable(bitmapDrawable: Drawable, view: View?) {
        if (view == null) {
            return
        }
        view.background = bitmapDrawable
    }

    private fun checkDeviceHasNavigationBar(): Boolean {
        var hasNavigationBar = false
        val rs = application.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return hasNavigationBar
    }
}