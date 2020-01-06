package com.gwell.iotvideodemo.messagemgr

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.gwell.iotvideo.IoTVideoSdk
import com.gwell.iotvideo.messagemgr.IResultListener
import com.gwell.iotvideo.messagemgr.Message
import com.gwell.iotvideo.utils.LogUtils
import com.gwell.iotvideodemo.R
import com.gwell.iotvideodemo.kt.base.BaseFragment
import com.gwell.iotvideodemo.kt.base.IBasePresenter
import com.gwell.iotvideodemo.kt.function.click
import kotlinx.android.synthetic.main.fragment_send_data_to_server.*

class SendDataToServerFragment : BaseFragment<DeviceMessagePresenter>() {

    override fun getResId(): Int {
        return R.layout.fragment_send_data_to_server
    }

    override fun init(savedInstanceState: Bundle?) {
        result_txt.movementMethod = ScrollingMovementMethod.getInstance()

        tv_run.click {
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
