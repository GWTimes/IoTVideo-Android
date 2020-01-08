package com.gwell.iotvideodemo.messagemgr

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.gwell.iotvideo.IoTVideoSdk
import com.gwell.iotvideo.iotvideoplayer.player.PlaybackPlayer
import com.gwell.iotvideo.messagemgr.DataMessage
import com.gwell.iotvideo.messagemgr.IResultListener
import com.gwell.iotvideo.messagemgr.Message
import com.gwell.iotvideo.utils.LogUtils
import com.gwell.iotvideodemo.R
import com.gwell.iotvideodemo.kt.base.BaseFragment
import com.gwell.iotvideodemo.kt.function.click
import kotlinx.android.synthetic.main.fragment_send_data_to_device.*

class GetPlaybackFragment : BaseFragment<DeviceMessagePresenter>() {

    override fun getResId(): Int {
        return R.layout.fragment_get_playback
    }

    override fun init(savedInstanceState: Bundle?) {
        result_txt.movementMethod = ScrollingMovementMethod.getInstance()

        tv_run.click {
            PlaybackPlayer.getPlaybackList(mBasePresenter.deviceId, 0, System.currentTimeMillis() / 1000,
                    0, 50, object : IResultListener<DataMessage>{
                override fun onStart() {
                    result_txt.text = "请求中...."
                }

                override fun onSuccess(msg: DataMessage?) {
                    result_txt.text = "获取成功 ${msg!!.data}"
                }

                override fun onError(errorCode: Int, errorMsg: String?) {
                    LogUtils.d(TAG, "sendDataToDevice error code $errorCode,  $errorMsg")
                    result_txt.text = "error code : $errorCode, content : $errorMsg"
                }

            })
        }
    }
}
