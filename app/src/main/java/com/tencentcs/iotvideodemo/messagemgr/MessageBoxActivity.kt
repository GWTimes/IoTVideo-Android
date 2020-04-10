package com.tencentcs.iotvideodemo.messagemgr

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.Builder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencentcs.iotvideo.messagemgr.EventMessage
import com.tencentcs.iotvideo.messagemgr.Message
import com.tencentcs.iotvideo.messagemgr.ModelMessage
import com.tencentcs.iotvideodemo.R
import com.tencentcs.iotvideodemo.kt.base.BaseActivity
import com.tencentcs.iotvideodemo.utils.Utils
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider
import com.tencentcs.iotvideodemo.widget.SimpleRecyclerViewAdapter
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_message_box.*

class MessageBoxActivity: BaseActivity() {
    private val messageList: ArrayList<Message> = ArrayList()
    private var adapter: SimpleRecyclerViewAdapter<Message>? = null
    private var alertDialog: AlertDialog? = null

    override fun getResId(): Int = R.layout.activity_message_box

    override fun init(savedInstanceState: Bundle?) {
        adapter = SimpleRecyclerViewAdapter(this, messageList)
        message_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        message_list.addItemDecoration(RecycleViewDivider(this, RecycleViewDivider.VERTICAL))
        message_list.adapter = adapter

        swipe_refresh_layout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark)
        swipe_refresh_layout.setOnRefreshListener {
            freshMessageList()
            swipe_refresh_layout.isRefreshing = false
        }

        adapter!!.setOnItemClickListener {
            val message = messageList[it]
            var title = "unknow"
            var tip = "unknow"

            if (message is EventMessage) {
                title = message.topic
                tip = Utils.printJson(message.data)
            } else if (message is ModelMessage) {
                title = message.device + ":" + message.path
                tip = Utils.printJson(message.data)
            }

            showJsonDetail(title, tip)
        }

        freshMessageList()
    }

    private fun freshMessageList() {
        messageList.clear()
        MessageBox.eventMessageList.forEach {
            messageList.add(it)
        }
        MessageBox.modelMessageList.forEach {
            messageList.add(it)
        }
        adapter!!.notifyDataSetChanged()
    }

    private fun showJsonDetail(title: String, msg: String) {
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog?.dismiss()
        }
        alertDialog = Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.confirm), null)
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
    }
}