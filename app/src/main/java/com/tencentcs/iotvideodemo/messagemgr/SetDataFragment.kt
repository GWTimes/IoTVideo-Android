package com.tencentcs.iotvideodemo.messagemgr

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencentcs.iotvideo.IoTVideoSdk
import com.tencentcs.iotvideo.utils.rxjava.IResultListener
import com.tencentcs.iotvideo.messagemgr.ModelMessage
import com.tencentcs.iotvideo.utils.LogUtils
import com.tencentcs.iotvideodemo.R
import com.tencentcs.iotvideodemo.kt.base.BaseFragment
import com.tencentcs.iotvideodemo.kt.ui.ListItemDecoration
import com.tencentcs.iotvideodemo.kt.ui.adapter.ItemHolder
import com.tencentcs.iotvideodemo.kt.ui.adapter.SimpleAdapter
import com.tencentcs.iotvideodemo.kt.ui.adapter.onClick
import com.tencentcs.iotvideodemo.kt.ui.adapter.setText
import com.tencentcs.iotvideodemo.kt.utils.ViewUtils
import com.tencentcs.iotvideodemo.kt.widget.dialog.CommonEditDialogFragment
import kotlinx.android.synthetic.main.item_device_model_function.view.*
import kotlinx.android.synthetic.main.item_device_model_type.view.*

import java.util.ArrayList

class SetDataFragment : BaseFragment<DeviceMessagePresenter>(), IDeviceModelView {

    var data: ArrayList<DeviceModelItemData> = ArrayList()

    private lateinit var mAdapter: SimpleAdapter<DeviceModelItemData>

    override fun getResId(): Int {
        return R.layout.fragment_set_data
    }

    override fun init(savedInstanceState: Bundle?) {

        var recyclerView = mRootView.findViewById<RecyclerView>(R.id.rv_mode_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(ListItemDecoration(ViewUtils.dip2px(1f), Color.parseColor("#eeeeee")))

        val typeItem = ItemHolder<DeviceModelItemData>(R.layout.item_device_model_type, 0)
                .bindData { data, position ->
                    setText(itemView.tv_type, data.typeData!!.name)
                }
                .bindEvent { data, position ->
                    onClick(itemView) {
                        doSetDataClick(data, position)
                    }
                }
        val functionItem = ItemHolder<DeviceModelItemData>(R.layout.item_device_model_function, 1)
                .bindData { data, position ->
                    setText(itemView.tv_function, data.functionData!!.name)
                }
                .bindEvent { data, position ->
                    onClick(itemView) {
                        doSetDataClick(data, position)
                    }
                }
        mAdapter = SimpleAdapter<DeviceModelItemData>(data, typeItem, functionItem) { data, postion ->
            if (data.type == 0) 0 else 1
        }

        recyclerView.adapter = mAdapter
    }

    override fun updateModelData(list: MutableList<DeviceModelItemData>) {
        data.clear()
        for (item in list) {
            if (item.typeData!!.name == "Action" || item.typeData!!.name == "ProWritable") {
                data.add(item)
            }
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun doSetDataClick(data: DeviceModelItemData, position: Int) {
        var path = data.typeData!!.name
        data.functionData?.let {
            path += "."
            path += it.name
        }

        var jsondata = data.typeData!!.data
        data.functionData?.let {
            jsondata = it.data
        }

        CommonEditDialogFragment.newDialog()
                .title(path)
                .tips(jsondata)
                .outSideFinish(false)
                .callback(ok = {
                    LogUtils.d(TAG, "writeProperty path is $path, data is $it")
                    IoTVideoSdk.getMessageMgr().writeProperty(mBasePresenter.deviceId, path, it, object : IResultListener<ModelMessage> {
                        override fun onSuccess(p0: ModelMessage?) {
                            LogUtils.d(TAG, "writeProperty" + p0!!.data)
                            if (this@SetDataFragment.context != null) {
                                Toast.makeText(this@SetDataFragment.context, "设置${path}成功", Toast.LENGTH_LONG).show()
                                mBasePresenter.initModelData(this@SetDataFragment.context!!, mBasePresenter.deviceId)
                            }
                        }

                        override fun onError(p0: Int, p1: String?) {
                            LogUtils.d(TAG, "writeProperty error code $p0, content $p1")
                            if (this@SetDataFragment.context != null) {
                                Toast.makeText(this@SetDataFragment.context, "设置${path}失败, error:${p0}", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onStart() {
                        }
                    })
                })
                .show(this@SetDataFragment.childFragmentManager!!, "SetDataDialog")
    }
}
