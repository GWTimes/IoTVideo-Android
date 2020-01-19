package com.gwell.iotvideodemo.messagemgr

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.gwell.iotvideo.iotvideoplayer.player.PlaybackPlayer
import com.gwell.iotvideo.utils.rxjava.IResultListener
import com.gwell.iotvideo.messagemgr.PlaybackMessage
import com.gwell.iotvideo.utils.LogUtils
import com.gwell.iotvideodemo.R
import com.gwell.iotvideodemo.kt.base.BaseFragment
import com.gwell.iotvideodemo.kt.function.click
import kotlinx.android.synthetic.main.fragment_get_playback.*
import kotlinx.android.synthetic.main.fragment_send_data_to_device.result_txt
import kotlinx.android.synthetic.main.fragment_send_data_to_device.tv_run

class GetPlaybackFragment : BaseFragment<DeviceMessagePresenter>() {

    override fun getResId(): Int {
        return R.layout.fragment_get_playback
    }

    override fun init(savedInstanceState: Bundle?) {
        result_txt.movementMethod = ScrollingMovementMethod.getInstance()

        tv_run.click {

            var pageIndex = if(et_page_index.text.toString().isEmpty()) 0 else et_page_index.text.toString().toInt()
            var pagePerCount = if(et_page_per_count.text.toString().isEmpty()) 50 else et_page_per_count.text.toString().toInt()

            PlaybackPlayer.getPlaybackList(mBasePresenter.deviceId, 0, System.currentTimeMillis() / 1000,
                    pageIndex , pagePerCount, object : IResultListener<PlaybackMessage> {
                override fun onStart() {
                    result_txt.text = "请求中...."
                }

                override fun onSuccess(msg: PlaybackMessage?) {
                    result_txt.text = "获取成功 : 当前页 ${msg!!.currentPage}, 总页数 ${msg!!.pageCount}"
                    for(item in msg!!.playbackList){
                        appendToOutput(item.toString())
                    }
                }

                override fun onError(errorCode: Int, errorMsg: String?) {
                    LogUtils.d(TAG, "getPlaybackList error code $errorCode,  $errorMsg")
                    result_txt.text = "error code : $errorCode, content : $errorMsg"
                }

            })
        }
    }

    private fun appendToOutput(text: String) {
        result_txt.append("\n" + text)
        val offset = result_txt.getLineCount() * result_txt.getLineHeight()
        if (offset > result_txt.getHeight()) {
            result_txt.scrollTo(0, offset - result_txt.getHeight())
        }
    }
}
