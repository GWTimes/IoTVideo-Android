package com.gwell.iotvideodemo.messagemgr

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.gwell.iotvideo.IoTVideoSdk
import com.gwell.iotvideo.messagemgr.DataMessage
import com.gwell.iotvideo.utils.rxjava.IResultListener
import com.gwell.iotvideo.utils.LogUtils
import com.gwell.iotvideodemo.R
import com.gwell.iotvideodemo.kt.base.BaseFragment
import com.gwell.iotvideodemo.kt.function.click
import kotlinx.android.synthetic.main.fragment_send_data_to_device.*

class SendDataToDeviceFragment : BaseFragment<DeviceMessagePresenter>() {

    override fun getResId(): Int {
        return R.layout.fragment_send_data_to_device
    }

    override fun init(savedInstanceState: Bundle?) {
        result_txt.movementMethod = ScrollingMovementMethod.getInstance()

        tv_run.click {
            IoTVideoSdk.getMessageMgr().sendDataToDeviceWithResponse(mBasePresenter.deviceId, null, object : IResultListener<DataMessage> {
                override fun onSuccess(p0: DataMessage?) {

                }

                override fun onError(p0: Int, p1: String?) {
                    LogUtils.d(TAG, "sendDataToDeviceWithResponse error code $p0,  $p1")
                    result_txt.text = "error code : $p0, content : $p1"
                }

                override fun onStart() {
                    result_txt.text = "请求中...."
                }

            })
        }
    }
}
