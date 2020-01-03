package com.gwell.iotvideodemo.messagemgr

import com.gwell.iotvideodemo.kt.base.BaseActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import com.gwell.iotvideo.IoTVideoSdk
import com.gwell.iotvideo.messagemgr.IResultListener
import com.gwell.iotvideo.messagemgr.Message
import com.gwell.iotvideo.messagemgr.ModelMessage
import com.gwell.iotvideo.utils.LogUtils
import com.gwell.iotvideodemo.R
import com.gwell.iotvideodemo.kt.base.IBasePresenter
import com.gwell.iotvideodemo.kt.function.click
import kotlinx.android.synthetic.main.activity_device_message.*

class DeviceMessageActivity : BaseActivity<IBasePresenter>() {
    var device = ""

    var checkId = R.id.rb_set_data

    override fun getResId(): Int = R.layout.activity_device_message

    override fun init(savedInstanceState: Bundle?) {
        if (!TextUtils.isEmpty(intent.extras!!.getString("deviceID"))) {
            LogUtils.d(TAG, intent.extras!!.getString("deviceID"))
            device = intent.extras!!.getString("deviceID")!!
        }

        result_txt.movementMethod = ScrollingMovementMethod.getInstance()

        //设置监听
        rg_group.setOnCheckedChangeListener { group, checkedId ->
            checkId = checkedId
            when (checkedId) {
                R.id.rb_set_data -> {
                    tv_title1.text = "路径"
                    tv_title1.visibility = View.VISIBLE
                    et_path.visibility = View.VISIBLE
                    et_path.text.clear()
                    tv_title2.visibility = View.VISIBLE
                    et_data.visibility = View.VISIBLE
                    et_data.text.clear()
                }
                R.id.rb_get_data -> {
                    tv_title1.text = "路径"
                    tv_title1.visibility = View.VISIBLE
                    et_path.visibility = View.VISIBLE
                    et_path.text.clear()
                    tv_title2.visibility = View.GONE
                    et_data.visibility = View.GONE
                    et_data.text.clear()
                }
                R.id.rb_send_data_device -> {
                    tv_title1.text = "设备ID"
                    tv_title1.visibility = View.GONE
                    et_path.visibility = View.GONE
                    tv_title2.visibility = View.VISIBLE
                    et_data.visibility = View.VISIBLE
                    et_data.text.clear()
                }
                R.id.rb_send_data_server -> {
                    tv_title1.text = "路径"
                    tv_title1.visibility = View.VISIBLE
                    et_path.visibility = View.VISIBLE
                    et_path.text.clear()
                    tv_title2.visibility = View.VISIBLE
                    et_data.visibility = View.VISIBLE
                    et_data.text.clear()
                }
            }
        }

        tv_run.click {
            when (checkId) {
                R.id.rb_set_data -> {
                    IoTVideoSdk.getMessageMgr().setData(device.toLong(), et_path.text.toString(), et_data.text.toString(), object : IResultListener {
                        override fun onSuccess(p0: Message?) {
                            LogUtils.d(TAG, "setData" + (p0 as ModelMessage).data)
                            result_txt.text = (p0 as ModelMessage).data
                        }

                        override fun onError(p0: Int, p1: String?) {
                            LogUtils.d(TAG, "setData error code $p0, content $p1")
                            result_txt.text = "error code : $p0, content : $p1"
                        }

                        override fun onStart() {
                            result_txt.text = "请求中..."
                        }

                    })
                }
                R.id.rb_get_data -> {
                    IoTVideoSdk.getMessageMgr().getData(device.toLong(), et_path.text.toString(), object : IResultListener {
                        override fun onSuccess(p0: Message?) {
                            LogUtils.d(TAG, "getData" + (p0 as ModelMessage).data)
                            result_txt.text = (p0 as ModelMessage).data
                        }

                        override fun onError(p0: Int, p1: String?) {
                            LogUtils.d(TAG, "getData error code $p0, content $p1")
                            result_txt.text = "error code : $p0, content : $p1"
                        }

                        override fun onStart() {
                            result_txt.text = "请求中..."
                        }

                    })
                }
                R.id.rb_send_data_device -> {
                    IoTVideoSdk.getMessageMgr().sendDataToDevice(device.toLong(), null, object : IResultListener {
                        override fun onSuccess(p0: Message?) {

                        }

                        override fun onError(p0: Int, p1: String?) {
                            LogUtils.d(TAG, "sendDataToDevice error code $p0,  $p1")
                            result_txt.text = "error code : $p0, content : $p1"
                        }

                        override fun onStart() {
                            result_txt.text = "请求中...."
                        }

                    })
                }
                R.id.rb_send_data_server -> {
                    IoTVideoSdk.getMessageMgr().sendDataToServer(et_path.text.toString(), null, object : IResultListener {
                        override fun onSuccess(p0: Message?) {
                        }

                        override fun onError(p0: Int, p1: String?) {
                            LogUtils.d(TAG, "sendDataToServer error code $p0,  $p1")
                            result_txt.text = "error code : $p0, content : $p1"
                        }

                        override fun onStart() {
                            result_txt.text = "请求中..."
                        }

                    })
                }
            }
        }
    }

}
