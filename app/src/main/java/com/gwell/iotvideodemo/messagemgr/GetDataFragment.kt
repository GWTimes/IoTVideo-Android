package com.gwell.iotvideodemo.messagemgr

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gwell.iotvideo.IoTVideoSdk
import com.gwell.iotvideo.utils.rxjava.IResultListener
import com.gwell.iotvideo.messagemgr.ModelMessage
import com.gwell.iotvideo.utils.LogUtils
import com.gwell.iotvideodemo.R
import com.gwell.iotvideodemo.kt.base.BaseFragment
import com.gwell.iotvideodemo.kt.ui.ListItemDecoration
import com.gwell.iotvideodemo.kt.ui.adapter.ItemHolder
import com.gwell.iotvideodemo.kt.ui.adapter.SimpleAdapter
import com.gwell.iotvideodemo.kt.ui.adapter.onClick
import com.gwell.iotvideodemo.kt.ui.adapter.setText
import com.gwell.iotvideodemo.kt.utils.ViewUtils.dip2px
import com.gwell.iotvideodemo.kt.widget.dialog.CommonDialogFragment
import kotlinx.android.synthetic.main.item_device_model_type.view.*
import kotlinx.android.synthetic.main.item_device_model_function.view.*

import java.util.ArrayList

class GetDataFragment : BaseFragment<DeviceMessagePresenter>(), IDeviceModelView {

    var data: ArrayList<DeviceModelItemData> = ArrayList()

    private lateinit var mAdapter: SimpleAdapter<DeviceModelItemData>

    override fun getResId(): Int {
        return R.layout.fragment_get_data
    }

    override fun init(savedInstanceState: Bundle?) {

        var recyclerView = mRootView.findViewById<RecyclerView>(R.id.rv_mode_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(ListItemDecoration(dip2px(1f), Color.parseColor("#eeeeee")))

        val typeItem = ItemHolder<DeviceModelItemData>(R.layout.item_device_model_type, 0)
                .bindData { data, position ->
                    setText(itemView.tv_type, data.typeData!!.name)
                }
                .bindEvent { data, position ->
                    onClick(itemView) {
                        doGetDataClick(data, position)
                    }
                }
        val functionItem = ItemHolder<DeviceModelItemData>(R.layout.item_device_model_function, 1)
                .bindData { data, position ->
                    setText(itemView.tv_function, data.functionData!!.name)
                }
                .bindEvent { data, position ->
                    onClick(itemView) {
                        doGetDataClick(data, position)
                    }
                }
        mAdapter = SimpleAdapter<DeviceModelItemData>(data, typeItem, functionItem) { data, postion ->
            if (data.type == 0) 0 else 1
        }

        recyclerView.adapter = mAdapter

    }

    override fun updateModelData(list: MutableList<DeviceModelItemData>) {
        data.clear()
        data.addAll(list)
        mAdapter.notifyDataSetChanged()
    }

    private fun doGetDataClick(data: DeviceModelItemData, position: Int) {
        var path = data.typeData!!.name
        data.functionData?.let {
            path += "."
            path += it.name
        }

        LogUtils.d(TAG, "readProperty path is $path")

        IoTVideoSdk.getMessageMgr().readProperty(mBasePresenter.deviceId, path, object : IResultListener<ModelMessage> {
            override fun onStart() {
            }

            override fun onSuccess(p0: ModelMessage?) {
                LogUtils.d(TAG, "readProperty" + p0!!.data)
                CommonDialogFragment.newDialog()
                        .title(path)
                        .tips(p0.data)
                        .setConfirmStyle(true)
                        .outSideFinish(false)
                        .show(this@GetDataFragment.childFragmentManager!!, "ShowDataDialog")
            }

            override fun onError(p0: Int, p1: String?) {
                LogUtils.d(TAG, "readProperty error code $p0, content $p1")
                Toast.makeText(this@GetDataFragment.context, "获取${path}失败", Toast.LENGTH_LONG).show()
            }
        })
    }
}
