package com.gwell.iotvideodemo.messagemgr

import com.gwell.iotvideo.IoTVideoSdk
import com.gwell.iotvideo.utils.rxjava.IResultListener
import com.gwell.iotvideo.messagemgr.ModelMessage
import com.gwell.iotvideo.utils.LogUtils
import com.gwell.iotvideodemo.kt.base.BasePresenter
import org.json.JSONObject

class DeviceMessagePresenter(var mDeviceModelView: IDeviceModelView) : BasePresenter(){

    private var modeData : JSONObject? = null

    var deviceId: Long = 0

    fun initModelData(deviceId: Long){
        //获取所有的物模型
        IoTVideoSdk.getMessageMgr().readProperty(deviceId, "", object : IResultListener<ModelMessage> {
            override fun onStart() {
                LogUtils.d(TAG, "readProperty start")
            }
            override fun onSuccess(p0: ModelMessage?) {
                LogUtils.d(TAG, "readProperty" + p0!!.data)
                modeData = JSONObject(p0.data)
                updateModelData()
            }

            override fun onError(p0: Int, p1: String?) {
                LogUtils.e(TAG, "readProperty error code $p0, content $p1")
            }
        })
    }

    private fun updateModelData(){
        var dataList: ArrayList<DeviceModelItemData> = ArrayList()
        modeData?.apply {
            keys().forEach { type_it ->
                var typeData =  DeviceModelTypeData(type_it, getString(type_it))
                dataList.add(DeviceModelItemData(0, typeData, null))

                var jsonData = JSONObject(getString(type_it))
                jsonData.keys().forEach {function_it ->
                    dataList.add(DeviceModelItemData(1, typeData, DeviceModelFunctionData(function_it, jsonData.getString(function_it))))
                }
            }
            mDeviceModelView.updateModelData(dataList)
        }
    }
}