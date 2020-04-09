package com.tencentcs.iotvideodemo.kt.widget.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tencentcs.iotvideodemo.R
import com.tencentcs.iotvideodemo.kt.base.BaseDialogFragment
import com.tencentcs.iotvideodemo.kt.function.round
import com.tencentcs.iotvideodemo.kt.utils.ViewUtils.dip2px
import kotlinx.android.synthetic.main.layout_common_dialog_fragment.*

/**
 * 通用对话框
 */
class CommonDialogFragment : BaseDialogFragment(), View.OnClickListener {
    private lateinit var cancelCallBack: (() -> Unit)
    private lateinit var okCallBack: (() -> Unit)
    private var strTitle: String? = null
    private var strTips: String? = null
    private var strOk: String? = null
    private var strCancel: String? = null
    private var upStyle: Boolean = false
    private var confirm: Boolean = false

    companion object {
        fun newDialog(): CommonDialogFragment {
            return CommonDialogFragment()
        }
    }


    override fun initFragmentConfig(fragmentConfig: DialogFragmentConfig) {
        animationStyle = R.style.Base_Animation_AppCompat_Dialog
        backgroundRes = R.color.translucent_dark
    }

    override fun getResId(): Int {
        return R.layout.layout_common_dialog_fragment
    }

    override fun init(savedInstanceState: Bundle?) {

        tv_content.movementMethod = ScrollingMovementMethod.getInstance()

        iv_dialog_bg.round(r = 2.0f, solidColor = Color.parseColor("#FFFFFF"))
        tv_cancel.round(r = 2.0f, solidColor = Color.parseColor("#00574B"))
        tv_ok.round(r = 2.0f, solidColor = Color.parseColor("#006EFF"))

        tv_cancel.setOnClickListener(this)
        tv_ok.setOnClickListener(this)

        strTips?.let {
            tv_content.text = it
        }

        strOk?.let {
            tv_ok.text = it
        }

        strCancel?.let {
            tv_cancel.text = it
        }

        strTitle?.let {
            tv_title.visibility = View.VISIBLE
            tv_title.text = it
        }

        if (upStyle) {
            var lp = iv_dialog_bg.layoutParams as ConstraintLayout.LayoutParams
            lp.topMargin = dip2px(100f)
            iv_dialog_bg.layoutParams = lp
        }

        if (confirm) {
            tv_cancel.visibility = View.GONE
            var lp = tv_ok.layoutParams as ConstraintLayout.LayoutParams
            lp.startToStart = R.id.iv_dialog_bg
            lp.marginStart = dip2px(15f)
            tv_ok.layoutParams = lp
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            val id = v.id
            when (id) {
                R.id.tv_cancel -> {
                    Handler().postDelayed({
                        if (::cancelCallBack.isInitialized) {
                            cancelCallBack.invoke()
                        }
                    }, 50)
                    dismissAllowingStateLoss()
                }
                R.id.tv_ok -> {
                    Handler().postDelayed({
                        if (::okCallBack.isInitialized) {
                            okCallBack.invoke()
                        }
                    }, 50)
                    dismissAllowingStateLoss()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                dismissAllowingStateLoss()
            }
        }
    }

    fun callback(cancel: () -> Unit = {}, ok: () -> Unit = {}): CommonDialogFragment {
        this.cancelCallBack = cancel
        this.okCallBack = ok
        return this
    }

    fun title(title: String): CommonDialogFragment {
        this.strTitle = title
        return this
    }

    fun tips(tips: String): CommonDialogFragment {
        this.strTips = tips
        return this
    }

    fun cancel(tips: String): CommonDialogFragment {
        this.strCancel = tips
        return this
    }

    fun ok(tips: String): CommonDialogFragment {
        this.strOk = tips
        return this
    }

    fun outSideFinish(outSideFinish: Boolean): CommonDialogFragment {
        builder.outSideFinish = outSideFinish
        return this
    }

    fun hideSystemUI(hide: Boolean): CommonDialogFragment {
        this.builder.isHideSystemUI = hide
        return this
    }

    //居上的样式
    fun setUpStyle(up: Boolean): CommonDialogFragment {
        this.upStyle = up
        return this
    }

    //确认样式
    fun setConfirmStyle(confirm: Boolean): CommonDialogFragment {
        this.confirm = confirm
        return this
    }
}