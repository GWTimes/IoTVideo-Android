package com.tencentcs.iotvideodemo.messagemgr

import com.tencentcs.iotvideodemo.kt.base.BaseActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.tencentcs.iotvideo.utils.LogUtils
import com.tencentcs.iotvideodemo.R

class DeviceMessageActivity : BaseActivity() {

    private var device = "0"

    private lateinit var mModelDataFragment: ModelDataFragment

    override fun getResId(): Int = R.layout.activity_device_message

    override fun init(savedInstanceState: Bundle?) {
        if (!TextUtils.isEmpty(intent.extras!!.getString("deviceID"))) {
            LogUtils.d(TAG, intent.extras!!.getString("deviceID"))
            device = intent.extras!!.getString("deviceID")!!
        }

        mModelDataFragment = ModelDataFragment()
        showFragment(mModelDataFragment, "ModelDataFragment")
        ViewModelProviders.of(this).get(DeviceMessageMgrViewModel::class.java).initModelData(this, device)
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_content, fragment, tag)
                .commitAllowingStateLoss()
    }
}
