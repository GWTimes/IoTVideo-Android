package com.tencentcs.iotvideodemo.kt.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.tencentcs.iotvideodemo.R
import com.tencentcs.iotvideodemo.kt.utils.ViewUtils
import com.tencentcs.iotvideodemo.rxbus2.RxBus

abstract class BaseDialogFragment : DialogFragment(), IBaseView, View.OnTouchListener, GestureDetector.OnGestureListener, View.OnSystemUiVisibilityChangeListener {

    protected var TAG = this.javaClass.name

    private val mFragmentConfig = DialogFragmentConfig()
    private val width: Int = WindowManager.LayoutParams.MATCH_PARENT
    private val height: Int = WindowManager.LayoutParams.MATCH_PARENT

    private var mIsVisibleToUser = false
    private var mIsSetUserVisibleHint = false
    private var mDetector: GestureDetectorCompat? = null
    protected var mRootView: View? = null


    /**
     * @return 默认淡入淡出
     */
    var animationStyle: Int = R.style.bottom_enter

    /**
     * 获取背景颜色值
     *
     * @return 资源ID
     */
    var backgroundRes: Int = R.color.translucent

    val ctx: Context?
        get() = activity

    protected val pageName: String
        get() = ""

    var builder: Builder = Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragmentTheme)
        mDetector = GestureDetectorCompat(context, this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                onBack()
            }
        }
        dialog.let {
            it.window!!.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mRootView != null) return mRootView

        mRootView = inflater.inflate(getResId(), container, false)

        initFragmentConfig(mFragmentConfig)

        if (mFragmentConfig.isApplyRxBus) {
            RxBus.getDefault().register(this)
        }

        if (dialog!!.window != null) {
            val window = dialog!!.window
            window!!.addFlags(activity!!.window.attributes.flags)
            window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            if (builder.isUseStatusBar) {
                setStatusColor(window)
            }

            //继承宿主的systemUI 属性
            window.decorView.systemUiVisibility = activity!!.window.decorView.systemUiVisibility
            if (builder.isHideSystemUI) {
                hideSystemUI()
            }

            //设置背景
            window.setBackgroundDrawableResource(backgroundRes)

            if (builder.isIgnoreFocus) {
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            }

            if (builder.outSideFinish) {
                window.decorView.setOnTouchListener(this)
            }

            if (builder.gravity != 0) {
                val wlp = window.attributes
                wlp.gravity = builder.gravity
                window.attributes = wlp
            }
        }

        return mRootView
    }

    protected abstract fun initFragmentConfig(fragmentConfig: DialogFragmentConfig)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!mIsSetUserVisibleHint) {
            setVisibleToUser(true)
        }
        if (builder.isHideSystemUI) {
            hideSystemUI()
        }
    }

    override fun onPause() {
        super.onPause()
        setVisibleToUser(false)
    }

    override fun showLoadingDialog(msg: String?, cancelCancel: Boolean) {

    }

    override fun hideLoadingDialog() {

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mIsSetUserVisibleHint = true
        Log.d(TAG, "setUserVisibleHint : $isVisibleToUser")
        setVisibleToUser(isVisibleToUser)
    }

    protected open fun onVisibleToUserChanged(isVisibleToUser: Boolean) {
        //Log.d(TAG, "##onVisibleToUserChanged() isVisibleToUser = $isVisibleToUser")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mFragmentConfig.isApplyRxBus) {
            RxBus.getDefault().unregister(this)
        }
    }

    open fun onBack() {
        dismissAllowingStateLoss()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return mDetector!!.onTouchEvent(event)
    }


    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val rawX = e.rawX
        val rawY = e.rawY
        if (builder.gravity == Gravity.RIGHT) {
            val width = width
            val screenWidth = ViewUtils.screenWidth
            val dx = screenWidth - width
            if (rawX < dx) {
                dismiss()
                return true
            }
        } else if (builder.gravity == Gravity.BOTTOM) {
            val height = height
            val screenHeight = ViewUtils.screenHeight
            val dy = screenHeight - height
            if (rawY < dy) {
                dismiss()
                return true
            }
        }
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {

    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog!!.window
        window?.decorView?.setOnSystemUiVisibilityChangeListener(this)

        //修复内存泄漏
        dialog?.setOnCancelListener(null)
        dialog?.setOnDismissListener(null)
    }

    override fun onSystemUiVisibilityChange(visibility: Int) {
        if (builder.isHideSystemUI) {
            hideSystemUI()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!manager.isDestroyed) {
            val transaction = manager.beginTransaction()
            transaction.add(this, tag)
            transaction.commitAllowingStateLoss()
        }

        fragmentManager?.let {
            it.executePendingTransactions()
        }
    }

    private fun setVisibleToUser(isVisibleToUser: Boolean) {
        if (isVisibleToUser == mIsVisibleToUser) {
            return
        }
        mIsVisibleToUser = isVisibleToUser
        onVisibleToUserChanged(mIsVisibleToUser)
    }

    private fun hideSystemUI() {
        if (dialog != null && dialog!!.window != null) {
            val uiPot = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    )
            dialog!!.window!!.decorView.systemUiVisibility = uiPot
        }
    }

    private fun setStatusColor(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
    }

    protected class DialogFragmentConfig : AppConfig()

    open class Builder {
        var gravity: Int = Gravity.BOTTOM


        var isUseStatusBar = true

        /**
         * 是否隐藏systemui
         *
         * @return true表示隐藏，false反之
         */
        var isHideSystemUI: Boolean = false

        /**
         * 是否忽略焦点
         * @return true表示忽略焦点，false反之
         */
        var isIgnoreFocus: Boolean = false

        /**
         * 点击对话框外退出
         * @return true表示退出，false反之
         */
        var outSideFinish = false
    }
}

