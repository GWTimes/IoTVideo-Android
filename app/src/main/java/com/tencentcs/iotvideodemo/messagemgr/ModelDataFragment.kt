package com.tencentcs.iotvideodemo.messagemgr

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tencentcs.iotvideo.IoTVideoSdk
import com.tencentcs.iotvideo.utils.rxjava.IResultListener
import com.tencentcs.iotvideo.messagemgr.ModelMessage
import com.tencentcs.iotvideo.utils.LogUtils
import com.tencentcs.iotvideodemo.R
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceModelManager
import com.tencentcs.iotvideodemo.kt.base.BaseFragment
import com.tencentcs.iotvideodemo.kt.ui.ListItemDecoration
import com.tencentcs.iotvideodemo.kt.ui.adapter.ItemHolder
import com.tencentcs.iotvideodemo.kt.ui.adapter.SimpleAdapter
import com.tencentcs.iotvideodemo.kt.ui.adapter.onClick
import com.tencentcs.iotvideodemo.kt.ui.adapter.setText
import com.tencentcs.iotvideodemo.kt.utils.ViewUtils.dip2px
import com.tencentcs.iotvideodemo.kt.widget.dialog.CommonDialogFragment
import com.tencentcs.iotvideodemo.kt.widget.dialog.CommonEditDialogFragment
import com.tencentcs.iotvideodemo.utils.Utils
import kotlinx.android.synthetic.main.item_device_model_type.view.*
import kotlinx.android.synthetic.main.item_device_model_function.view.*

import java.util.ArrayList

class ModelDataFragment : BaseFragment() {

    var data: ArrayList<DeviceModelItemData> = ArrayList()

    private lateinit var mAdapter: SimpleAdapter<DeviceModelItemData>

    private var mDeviceMessageMgrViewModel: DeviceMessageMgrViewModel? = null

    override fun getResId(): Int {
        return R.layout.fragment_get_data
    }

    override fun init(savedInstanceState: Bundle?) {

        val recyclerView = mRootView.findViewById<RecyclerView>(R.id.rv_mode_list)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(ListItemDecoration(dip2px(1f), Color.parseColor("#eeeeee")))

        val typeItem = ItemHolder<DeviceModelItemData>(R.layout.item_device_model_type, 0)
                .bindData { data, position ->
                    setText(itemView.tv_type, data.typeData!!.name)
                }
                .bindEvent { data, position ->
                    onClick(itemView) {
//                        doGetDataClick(data, position)
                    }
                }
        val functionItem = ItemHolder<DeviceModelItemData>(R.layout.item_device_model_function, 1)
                .bindData { data, position ->
                    setText(itemView.tv_function, data.functionData!!.name)
                    if (data.typeData!!.name == "Action" || data.typeData!!.name == "ProWritable") {
                        itemView.btn_operate.setText(R.string.edit)
                    } else {
                        itemView.btn_operate.setText(R.string.check)
                    }
                }
                .bindEvent { data, position ->
                    onClick(itemView.btn_operate) {
                        if (data.typeData!!.name == "Action" || data.typeData!!.name == "ProWritable") {
                            doSetDataClick(data, position)
                        } else {
                            doGetDataClick(data, position)
                        }
                    }
                }
        mAdapter = SimpleAdapter<DeviceModelItemData>(data, typeItem, functionItem) { data, postion ->
            if (data.type == 0) 0 else 1
        }

        recyclerView.adapter = mAdapter

        mDeviceMessageMgrViewModel = ViewModelProviders.of(activity!!).get(DeviceMessageMgrViewModel::class.java)
        mDeviceMessageMgrViewModel?.modelLiveData?.observe(activity!!, Observer {
            data.clear()
            data.addAll(it)
            mAdapter.notifyDataSetChanged()
        })
    }

    private fun doGetDataClick(data: DeviceModelItemData, position: Int) {
        var path = data.typeData!!.name
        data.functionData?.let {
            path += "."
            path += it.name
        }

        LogUtils.d(TAG, "readProperty path is $path")

        IoTVideoSdk.getMessageMgr().readProperty(mDeviceMessageMgrViewModel?.deviceId, path, object : IResultListener<ModelMessage> {
            override fun onStart() {
            }

            override fun onSuccess(p0: ModelMessage?) {
                LogUtils.d(TAG, "readProperty onSuccess " + p0!!.data)
                CommonDialogFragment.newDialog()
                        .title(path)
                        .tips(p0.data)
                        .setConfirmStyle(true)
                        .outSideFinish(false)
                        .show(this@ModelDataFragment.childFragmentManager, "ShowDataDialog")
            }

            override fun onError(p0: Int, p1: String?) {
                LogUtils.d(TAG, "readProperty error code $p0, content $p1")
                Handler().post {
                    Utils.showToast("获取${path}失败")
                }
            }
        })
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
                    IoTVideoSdk.getMessageMgr().writeProperty(mDeviceMessageMgrViewModel?.deviceId, path, it, object : IResultListener<ModelMessage> {
                        override fun onSuccess(p0: ModelMessage?) {
                            LogUtils.d(TAG, "writeProperty" + p0!!.data)
                            if (this@ModelDataFragment.context != null) {
                                Handler().post {
                                    Utils.showToast("设置${path}成功")
                                }
//                                mDeviceMessageMgrViewModel?.initModelData(this@ModelDataFragment.context!!, mDeviceMessageMgrViewModel?.deviceId!!)
                                DeviceModelManager.getInstance().onNotify(
                                        ModelMessage(mDeviceMessageMgrViewModel?.deviceId, 0, 0, 0, path, it))
                                mDeviceMessageMgrViewModel?.updateModelData()
                            }
                        }

                        override fun onError(p0: Int, p1: String?) {
                            LogUtils.d(TAG, "writeProperty error code $p0, content $p1")
                            Handler().post {
                                Utils.showToast("设置${path}失败, error:${p0}")
                            }
                        }

                        override fun onStart() {
                        }
                    })
                })
                .show(this@ModelDataFragment.childFragmentManager, "SetDataDialog")
    }
}
