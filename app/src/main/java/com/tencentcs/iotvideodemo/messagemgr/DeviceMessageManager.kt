package com.tencentcs.iotvideodemo.messagemgr

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.tencentcs.iotvideo.IoTVideoSdk
import com.tencentcs.iotvideo.messagemgr.ModelMessage
import com.tencentcs.iotvideo.utils.LogUtils
import com.tencentcs.iotvideo.utils.rxjava.IResultListener
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceModelManager

class DeviceMessageManager {
    private val TAG = javaClass.simpleName

    private var modeData: JsonObject? = null
    private val jsonParser: JsonParser = JsonParser()

    fun initModelData(context: Context, deviceId: String, modelLiveData: MutableLiveData<java.util.ArrayList<DeviceModelItemData>>) {
        //获取所有的物模型
        IoTVideoSdk.getMessageMgr().readProperty(deviceId, "", object : IResultListener<ModelMessage> {
            override fun onStart() {
                LogUtils.d(TAG, "readProperty start")
            }

            override fun onSuccess(p0: ModelMessage?) {
                LogUtils.d(TAG, "readProperty" + p0!!.data)
                modeData = jsonParser.parse(p0.data).asJsonObject
                updateModelData(modelLiveData)
                val model = DeviceModelManager.DeviceModel(p0.device, modeData)
                DeviceModelManager.getInstance().setDeviceModel(model)
            }

            override fun onError(p0: Int, p1: String?) {
                LogUtils.e(TAG, "readProperty error code $p0, content $p1")
                Toast.makeText(context, "readProperty error code $p0, content $p1", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun updateModelData(deviceId: String, modelLiveData: MutableLiveData<java.util.ArrayList<DeviceModelItemData>>) {
        val deviceModel = DeviceModelManager.getInstance().getDeviceModel(deviceId)
        LogUtils.i(TAG, "updateModelData JsonObject $deviceModel")
        if (deviceModel?.model != null) {
            modeData = deviceModel.model
            updateModelData(modelLiveData)
        }
    }

    private fun updateModelData(modelLiveData: MutableLiveData<java.util.ArrayList<DeviceModelItemData>>) {
        val dataList: ArrayList<DeviceModelItemData> = ArrayList()
        modeData?.apply {
            keySet().forEach { type_it ->
                val typeData = DeviceModelTypeData(type_it, get(type_it).toString())
                dataList.add(DeviceModelItemData(0, typeData, null))

                val jsonData = getAsJsonObject(type_it)
                jsonData.keySet().forEach { function_it ->
                    dataList.add(DeviceModelItemData(1, typeData, DeviceModelFunctionData(function_it,
                            jsonData.get(function_it).toString())))
                }
            }
            modelLiveData.value = dataList
        }
    }
}