package com.tencentcs.iotvideodemo.messagemgr

import com.tencentcs.iotvideodemo.kt.base.BaseActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.tencentcs.iotvideo.utils.LogUtils
import com.tencentcs.iotvideodemo.R
import kotlinx.android.synthetic.main.activity_device_message.*

class DeviceMessageActivity : BaseActivity<DeviceMessagePresenter>(), IDeviceModelView {

    private var device = "0"

    private var checkId = R.id.rb_get_data

    private lateinit var mGetDataFragment: GetDataFragment
    private lateinit var mSetDataFragment: SetDataFragment
    private lateinit var mSendDataToDeviceFragment: SendDataToDeviceFragment
    private lateinit var mSendDataToServerFragment: SendDataToServerFragment
    private lateinit var mGetPlaybackFragment: GetPlaybackFragment

    override fun getResId(): Int = R.layout.activity_device_message

    override fun init(savedInstanceState: Bundle?) {
        if (!TextUtils.isEmpty(intent.extras!!.getString("deviceID"))) {
            LogUtils.d(TAG, intent.extras!!.getString("deviceID"))
            device = intent.extras!!.getString("deviceID")!!
        }

        mGetDataFragment = GetDataFragment()
        mSetDataFragment = SetDataFragment()
        mSendDataToDeviceFragment = SendDataToDeviceFragment()
        mSendDataToServerFragment = SendDataToServerFragment()
        mGetPlaybackFragment = GetPlaybackFragment()
        addFragment(mGetDataFragment, "GetDataFragment")
        addFragment(mSetDataFragment, "SetDataFragment")
        addFragment(mSendDataToDeviceFragment, "SendDataToDeviceFragment")
        addFragment(mSendDataToServerFragment, "SendDataToServerFragment")
        addFragment(mGetPlaybackFragment, "GetPlaybackFragment")

        //获取物模型
        mBasePresenter = DeviceMessagePresenter(this)
        mBasePresenter.initModelData(applicationContext, device)
        mBasePresenter.deviceId = device

        mGetDataFragment.mBasePresenter = mBasePresenter
        mSetDataFragment.mBasePresenter = mBasePresenter
        mSendDataToDeviceFragment.mBasePresenter = mBasePresenter
        mSendDataToServerFragment.mBasePresenter = mBasePresenter
        mGetPlaybackFragment.mBasePresenter = mBasePresenter

        when (checkId) {
            R.id.rb_get_data -> switchFragment(mGetDataFragment)
            R.id.rb_set_data -> switchFragment(mSetDataFragment)
            R.id.rb_send_data_device -> switchFragment(mSendDataToDeviceFragment)
            R.id.rb_send_data_server -> switchFragment(mSendDataToServerFragment)
            R.id.rb_get_playback -> switchFragment(mGetPlaybackFragment)
        }

        //设置监听
        rg_group.setOnCheckedChangeListener { group, checkedId ->
            if (checkId == checkedId) {
                return@setOnCheckedChangeListener
            }
            checkId = checkedId
            when (checkId) {
                R.id.rb_get_data -> switchFragment(mGetDataFragment)
                R.id.rb_set_data -> switchFragment(mSetDataFragment)
                R.id.rb_send_data_device -> switchFragment(mSendDataToDeviceFragment)
                R.id.rb_send_data_server -> switchFragment(mSendDataToServerFragment)
                R.id.rb_get_playback -> switchFragment(mGetPlaybackFragment)
            }
        }
    }

    override fun updateModelData(list: MutableList<DeviceModelItemData>) {
        if (::mGetDataFragment.isInitialized) {
            mGetDataFragment.updateModelData(list)
        }
        if (::mSetDataFragment.isInitialized) {
            mSetDataFragment.updateModelData(list)
        }
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        if (!fragment.isAdded) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fl_content, fragment, tag)
                    .commitAllowingStateLoss()
        }
    }

    private fun switchFragment(targetFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        hideAllSelected(transaction, targetFragment)
        transaction.show(targetFragment)
        transaction.commitNowAllowingStateLoss()
    }

    private fun hideAllSelected(transaction: FragmentTransaction, targetFragment: Fragment) {
        val listFragment = arrayListOf(mGetDataFragment, mSetDataFragment, mSendDataToDeviceFragment, mSendDataToServerFragment, mGetPlaybackFragment)
        listFragment.remove(targetFragment)
        listFragment.forEach {
            transaction.hide(it)
        }
    }

}
