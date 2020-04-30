package com.tencentcs.iotvideodemo.videoplayer

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tencentcs.iotvideo.iotvideoplayer.IRecordListener
import com.tencentcs.iotvideo.iotvideoplayer.ISnapShotListener
import com.tencentcs.iotvideo.iotvideoplayer.PlayerStateEnum
import com.tencentcs.iotvideo.iotvideoplayer.player.PlaybackPlayer
import com.tencentcs.iotvideo.messagemgr.DataMessage
import com.tencentcs.iotvideo.messagemgr.MessageMgr
import com.tencentcs.iotvideo.utils.rxjava.IResultListener
import com.tencentcs.iotvideo.messagemgr.PlaybackMessage
import com.tencentcs.iotvideo.utils.LogUtils
import com.tencentcs.iotvideodemo.R
import com.tencentcs.iotvideodemo.kt.base.BaseActivity
import com.tencentcs.iotvideodemo.kt.function.click
import com.tencentcs.iotvideodemo.kt.ui.ListItemDecoration
import com.tencentcs.iotvideodemo.kt.ui.adapter.ItemHolder
import com.tencentcs.iotvideodemo.kt.ui.adapter.SimpleAdapter
import com.tencentcs.iotvideodemo.kt.ui.adapter.onClick
import com.tencentcs.iotvideodemo.kt.ui.adapter.setText
import com.tencentcs.iotvideodemo.kt.utils.ViewUtils
import com.tencentcs.iotvideodemo.utils.StorageManager
import kotlinx.android.synthetic.main.activity_playback_player.*
import kotlinx.android.synthetic.main.item_playback_node.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PlaybackPlayerActivity : BaseActivity() {
    private val mSimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")

    private lateinit var mAdapter: SimpleAdapter<PlaybackMessage.PlaybackNode>

    var data: ArrayList<PlaybackMessage.PlaybackNode> = ArrayList()

    private var mDeviceId = ""

    private var mPlaybackPlayer = PlaybackPlayer()

    private var mCurrentPageIndex = 0

    private var mPageCount = -1

    override fun getResId() = R.layout.activity_playback_player

    override fun init(savedInstanceState: Bundle?) {
        intent.getStringExtra("deviceID")?.apply {
            mDeviceId = this
        }

        val recyclerView = rv_playback_list
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ListItemDecoration(ViewUtils.dip2px(1f), Color.parseColor("#eeeeee")))

        val itemHolder = ItemHolder<PlaybackMessage.PlaybackNode>(R.layout.item_playback_node, 0)
                .bindData { data, position ->
                    setText(itemView.tv_start, data.startTimeDisplay)
                    setText(itemView.tv_end, data.endTimeDisplay)
                    setText(itemView.tv_type, data.recordType)
                }
                .bindEvent { data, position ->
                    onClick(itemView) {
                        if (mPlaybackPlayer.isPlaying) {
                            mPlaybackPlayer.seek(it.startTime, it)
                        } else {
                            //设置播放器数据源
                            mPlaybackPlayer.setDataResource(mDeviceId, it.startTime, it)
                            mPlaybackPlayer.play()
                        }
                    }
                }

        mAdapter = SimpleAdapter<PlaybackMessage.PlaybackNode>(data, itemHolder) { data, postion -> 0 }

        recyclerView.adapter = mAdapter

        tv_get_playback_previous.click {
            getPlaybackListPrevious()
        }

        tv_get_playback_next.click {
            getPlaybackListNext()
        }

        tv_start_record.click {
            deviceRecord(true)
        }

        tv_stop_record.click {
            deviceRecord(false)
        }

        record_btn.click { recordVideoFromDevice() }

        snap_btn.click { snap() }

        mute_btn.click { mPlaybackPlayer.mute(!mPlaybackPlayer.isMute) }

        stop_btn.click { mPlaybackPlayer.stop() }

        initPlaybackPlayer()

        getPlaybackList(tv_get_playback_previous, 0)
    }

    override fun onResume() {
        super.onResume()
        tencentcs_gl_surface_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        tencentcs_gl_surface_view.onPause()
    }

    override fun onStop() {
        super.onStop()
        mPlaybackPlayer.stop()
    }

    private fun initPlaybackPlayer() {
        mPlaybackPlayer.setVideoView(tencentcs_gl_surface_view)
        mPlaybackPlayer.setPreparedListener {
            LogUtils.i(TAG, "onPrepared")
//            playback_status.text = "开始准备"
        }
        mPlaybackPlayer.setStatusListener {
            LogUtils.i(TAG, "onStatus changed ${getPlayStatus(it)}")
            playback_status.text = getPlayStatus(it)
        }
        mPlaybackPlayer.setTimeListener {

        }
        mPlaybackPlayer.setErrorListener {
            playback_status.text = "播放错误：$it"
        }
        mPlaybackPlayer.setUserDataListener {
//            playback_status.text = "收到数据：$data"
            LogUtils.i(TAG, "收到数据：$data")
        }

    }

    private fun getPlayStatus(status: Int): String {
        var playStatus = ""
        when (status) {
            PlayerStateEnum.STATE_IDLE -> playStatus = "未初始化"
            PlayerStateEnum.STATE_INITIALIZED -> playStatus = "已初始化"
            PlayerStateEnum.STATE_PREPARING -> playStatus = "准备中..."
            PlayerStateEnum.STATE_READY -> playStatus = "准备完成"
            PlayerStateEnum.STATE_LOADING -> playStatus = "加载中"
            PlayerStateEnum.STATE_PLAY -> playStatus = "播放中"
            PlayerStateEnum.STATE_PAUSE -> {
                playStatus = "暂停"
            }
            PlayerStateEnum.STATE_STOP -> playStatus = "停止播放"
        }
        return playStatus
    }

    private fun getPlaybackList(view: View, pageIndex: Int) {
        PlaybackPlayer.getPlaybackList(mDeviceId, 0, System.currentTimeMillis(),
                pageIndex, 50, object : IResultListener<PlaybackMessage> {
            override fun onStart() {
                LogUtils.d(TAG, "请求中...")
                playback_status.text = "正在获取回放列表..."
            }

            override fun onSuccess(msg: PlaybackMessage?) {
                if (msg?.type == -1 && msg?.error == -1 && msg?.id.toInt() == -1) {
                    playback_status.text = "回放列表为空"
                    LogUtils.i(TAG, "回放列表为空")
                    return
                }
                mPageCount = msg?.pageCount!!
                val logStr = "获取成功 : 当前页 ${msg.currentPage}, 总页数 $mPageCount"
                LogUtils.d(TAG, logStr)
                LogUtils.d(TAG, "获取成功 ${msg.toString()}")
                runOnUiThread {
                    playback_status.text = "获取回放列表成功"
                    data.clear()
                    msg.playbackList?.let {
                        data.addAll(it)
                        mAdapter.notifyDataSetChanged()
                    }
                    Snackbar.make(view, logStr, Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onError(errorCode: Int, errorMsg: String?) {
                val logStr = "getPlaybackList error code $errorCode,  $errorMsg"
                runOnUiThread {
                    playback_status.text = "获取回放列表失败 $errorCode, $errorMsg"
                    LogUtils.d(TAG, logStr)
                    Snackbar.make(view, logStr, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun getPlaybackListPrevious() {
        if (mPageCount == -1) {
            //未成功获取到回放列表，默认从第一页获取
            getPlaybackList(tv_get_playback_previous, 0)
            return
        } else if (mCurrentPageIndex == 0) {
            Snackbar.make(tv_get_playback_previous, "已是第一页", Snackbar.LENGTH_SHORT).show()
            return
        }
        getPlaybackList(tv_get_playback_previous, mCurrentPageIndex--)
    }

    private fun getPlaybackListNext() {
        if (mPageCount == -1) {
            //未成功获取到回放列表，默认从第一页获取
            getPlaybackList(tv_get_playback_next, 0)
            return
        } else if (mCurrentPageIndex == mPageCount - 1) {
            Snackbar.make(tv_get_playback_next, "已是最后页", Snackbar.LENGTH_SHORT).show()
            return
        }
        getPlaybackList(tv_get_playback_next, mCurrentPageIndex++)
    }

    private fun deviceRecord(on: Boolean) {
        val charset = Charsets.UTF_8
        val byteArray = (if (on) "record_start" else "record_stop").toByteArray(charset)
        MessageMgr.getInstance().sendDataToDeviceWithoutResponse(mDeviceId, byteArray, object : IResultListener<DataMessage> {
            override fun onStart() {
            }

            override fun onSuccess(msg: DataMessage?) {
                LogUtils.d(TAG, "deviceRecord $on")
                Snackbar.make(tv_start_record, (if (on) "打开录像成功" else "关闭录像成功"), Snackbar.LENGTH_LONG).show()
            }

            override fun onError(errorCode: Int, errorMsg: String?) {
                LogUtils.d(TAG, "deviceRecord $on error code $errorCode,  $errorMsg")
                Snackbar.make(tv_start_record, "deviceRecord $on error code $errorCode,  $errorMsg", Snackbar.LENGTH_LONG).show()
            }

        })
    }

    private fun recordVideoFromDevice() {
        if (!StorageManager.isVideoPathAvailable()) {
            Toast.makeText(this, "storage is not available", Toast.LENGTH_LONG).show()
            return
        }
        if (mPlaybackPlayer.isRecording) {
            record_btn.text = "录像"
            mPlaybackPlayer.stopRecord()
        } else {
            record_btn.text = "停止录像"
            val recordFile = File(StorageManager.getVideoPath() + File.separator + mDeviceId)
            if (!recordFile.exists() && !recordFile.mkdirs()) {
                LogUtils.e(TAG, "can not create file")
                return
            }
            mPlaybackPlayer.startRecord(recordFile.absolutePath, mSimpleDateFormat.format(Date()) + ".mp4",
                    IRecordListener { code, path ->
                        Toast.makeText(this, "code:$code path:$path", Toast.LENGTH_LONG).show()
                        if (code != 0) {
                            record_btn.text = "录像"
                        }
                    })
        }
    }

    private fun snap() {
        if (!StorageManager.isPicPathAvailable()) {
            Toast.makeText(this, "storage is not available", Toast.LENGTH_LONG).show()
            return
        }
        val snapFile = File(StorageManager.getPicPath() + File.separator + mDeviceId)
        if (!snapFile.exists() && !snapFile.mkdirs()) {
            LogUtils.e(TAG, "can not create file")
            return
        }
        mPlaybackPlayer.snapShot(snapFile.absolutePath + File.separator + mSimpleDateFormat.format(Date()) + ".jpeg",
                ISnapShotListener { code, path ->
                    Toast.makeText(this, "code:$code path:$path", Toast.LENGTH_LONG).show()
                })
    }

}
