package com.tencentcs.iotvideodemo.kt.base

import android.os.Bundle
import androidx.annotation.LayoutRes

interface IBaseView {

    @LayoutRes
    fun getResId(): Int

    fun init(savedInstanceState: Bundle?)

    fun showLoadingDialog(msg: String? = null, cancelCancel: Boolean = false)

    fun hideLoadingDialog()

}
