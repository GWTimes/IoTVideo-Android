package com.tencentcs.iotvideodemo.kt.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.tencentcs.iotvideodemo.kt.widget.dialog.LoadingDialog
import com.tencentcs.iotvideodemo.rxbus2.RxBus

abstract class BaseFragment : Fragment(), IBaseView {

    protected var TAG = this.javaClass.name

    private val fragmentConfig = FragmentConfig()

    private var isOnCreateView = false

    lateinit var mRootView: View

    val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(context)
    }

    var isVisibleToUsers = false
        private set

    var isSetUserVisibleHint: Boolean? = null
        private set

    var isHiddenChanged: Boolean? = null
        private set

    /**
     * 页面名称
     */
    open val pageName: String
        get() = javaClass.simpleName


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isOnCreateView = true
        mRootView = LayoutInflater.from(activity).inflate(getResId(), null, false)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)
        initFragmentConfig(fragmentConfig)
        if (fragmentConfig.isApplyRxBus) {
            RxBus.getDefault().register(this)
        }
        init(savedInstanceState)
    }

    protected open fun initFragmentConfig(fragmentConfig: FragmentConfig) {

    }


    override fun onResume() {
        super.onResume()
        //当前界面处于视觉可见态，生命周期才要触发可见回调
        if (parentFragment != null && parentFragment is BaseFragment) {
            if ((parentFragment as BaseFragment).isVisibleToUsers) {
                if (isPassivityVisible()) {
                    setVisibleToUser(true)
                }
            }
        } else {
            if (isPassivityVisible()) {
                setVisibleToUser(true)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //当前界面处于视觉可见态，生命周期才要触发可见回调
        if (isPassivityVisible()) {
            setVisibleToUser(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (fragmentConfig.isApplyRxBus) {
            RxBus.getDefault().unregister(this)
        }
        hideLoadingDialog()
    }

    override fun showLoadingDialog(msg: String?, cancelCancel: Boolean) {
        loadingDialog.setCancelable(cancelCancel)
        if (msg.isNullOrEmpty()) {
            loadingDialog.show()
        } else {
            loadingDialog.show(msg)
        }
    }

    override fun hideLoadingDialog() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isSetUserVisibleHint = isVisibleToUser
        setVisibleToUser(isVisibleToUser)
        val fragments = getValidChildFragmentManager()?.fragments
        if (fragments != null && fragments.isNotEmpty()) {
            fragments.forEach {
                if (it is BaseFragment) {
                    //当子Fragment处于视觉可见态，才随父Fragment触发可见回调
                    if (it.isPassivityVisible()) {
                        it.setVisibleToUser(isVisibleToUser)
                    }
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isHiddenChanged = hidden
        setVisibleToUser(!hidden)
        val fragments = getValidChildFragmentManager()?.fragments
        if (fragments != null && fragments.isNotEmpty()) {
            fragments.forEach {
                if (it is BaseFragment) {
                    //当子Fragment处于可见态，才随父Fragment触发可见回调
                    if (it.isPassivityVisible()) {
                        it.setVisibleToUser(!hidden)
                    }
                }
            }
        }
    }

    /**
     * fragment 可见变化
     */
    protected open fun onVisibleToUserChanged(isVisibleToUser: Boolean) {
        //Log.d(TAG, "##onVisibleToUserChanged() isVisibleToUser = $isVisibleToUser")
    }


    //视觉可见态，被setUserVisibleHint或者onHiddenChanged触发的可见态，跟生命周期无关。当Fragment被hidden或者处于ViewPager时会被触发
    private fun isPassivityVisible(): Boolean {
        if (isHiddenChanged != null && isSetUserVisibleHint != null) {
            if (!isHiddenChanged!!) {
                return isSetUserVisibleHint!!
            }
            return false
        } else {
            isHiddenChanged?.apply {
                return !this
            }

            isSetUserVisibleHint?.apply {
                return this
            }

            return true
        }
    }

    private fun getValidChildFragmentManager(): FragmentManager? {
        return try {
            childFragmentManager
        } catch (e: Exception) {
            null
        }
    }

    private fun setVisibleToUser(isVisibleToUser: Boolean) {
        if (!isOnCreateView) {
            return
        }
        if (isVisibleToUser == isVisibleToUsers) {
            return
        }
        isVisibleToUsers = isVisibleToUser
        onVisibleToUserChanged(isVisibleToUsers)
    }

    open class FragmentConfig : AppConfig()

}
