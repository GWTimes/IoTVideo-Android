package com.gwell.iotvideodemo.videoplayer

import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gwell.iotvideo.iotvideoplayer.player.PlaybackPlayer
import com.gwell.iotvideo.utils.rxjava.IResultListener
import com.gwell.iotvideo.messagemgr.PlaybackMessage
import com.gwell.iotvideo.utils.LogUtils
import com.gwell.iotvideodemo.R
import com.gwell.iotvideodemo.kt.base.BaseActivity
import com.gwell.iotvideodemo.kt.base.BasePresenter
import com.gwell.iotvideodemo.kt.function.click
import com.gwell.iotvideodemo.kt.ui.ListItemDecoration
import com.gwell.iotvideodemo.kt.ui.adapter.ItemHolder
import com.gwell.iotvideodemo.kt.ui.adapter.SimpleAdapter
import com.gwell.iotvideodemo.kt.ui.adapter.onClick
import com.gwell.iotvideodemo.kt.ui.adapter.setText
import com.gwell.iotvideodemo.kt.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_playback_player.*
import kotlinx.android.synthetic.main.item_playback_node.view.*
import java.util.ArrayList

class PlaybackPlayerActivity : BaseActivity<BasePresenter>() {

    private lateinit var mAdapter: SimpleAdapter<PlaybackMessage.PlaybackNode>

    var data: ArrayList<PlaybackMessage.PlaybackNode> = ArrayList()

    private var mDeviceId = 42949672974L

    private var mPlaybackPlayer = PlaybackPlayer()

    override fun getResId() = R.layout.activity_playback_player

    override fun init(savedInstanceState: Bundle?) {
        intent.getStringExtra("deviceID")?.apply {
            mDeviceId = this.toLong()
        }

        var recyclerView = rv_playback_list
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ListItemDecoration(ViewUtils.dip2px(1f), Color.parseColor("#eeeeee")))

        val itemHolder = ItemHolder<PlaybackMessage.PlaybackNode>(R.layout.item_playback_node, 0)
                .bindData { data, position ->
                    setText(itemView.tv_start, data.startTime.toString())
                    setText(itemView.tv_end, data.endTime.toString())
                    setText(itemView.tv_type, data.recordType)
                }
                .bindEvent { data, position ->
                    onClick(itemView) {
                        //设置播放器数据源
                        mPlaybackPlayer.setDataResource(mDeviceId, it.startTime, it)
                        mPlaybackPlayer.stop()
                        mPlaybackPlayer.play()
                    }
                }

        mAdapter = SimpleAdapter<PlaybackMessage.PlaybackNode>(data, itemHolder) { data, postion -> 0 }

        recyclerView.adapter = mAdapter

        tv_get_playback.click {
            PlaybackPlayer.getPlaybackList(mDeviceId, 0, System.currentTimeMillis() / 1000,
                    0, 50, object : IResultListener<PlaybackMessage> {
                override fun onStart() {
                    LogUtils.d(TAG, "请求中....");
                }

                override fun onSuccess(msg: PlaybackMessage?) {
                    LogUtils.d(TAG, "获取成功 : 当前页 ${msg!!.currentPage}, 总页数 ${msg!!.pageCount}")
                    data.clear()
                    msg!!.playbackList?.let {
                        data.addAll(it)
                        mAdapter.notifyDataSetChanged()
                    }
                }

                override fun onError(errorCode: Int, errorMsg: String?) {
                    LogUtils.d(TAG, "getPlaybackList error code $errorCode,  $errorMsg")
                }
            })

        }

        initPlaybackPlayer()

        //request permission
    }

    override fun onResume() {
        super.onResume()
        gwell_gl_surface_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        gwell_gl_surface_view.onPause()
    }

    override fun onStop() {
        super.onStop()
        mPlaybackPlayer.stop()
    }

    private fun initPlaybackPlayer() {
        mPlaybackPlayer.setVideoView(gwell_gl_surface_view)
        mPlaybackPlayer.setPreparedListener {

        }
        mPlaybackPlayer.setStatusListener {

        }
        mPlaybackPlayer.setTimeListener {

        }
        mPlaybackPlayer.setErrorListener {

        }
        mPlaybackPlayer.setUserDataListener {

        }

    }

}
