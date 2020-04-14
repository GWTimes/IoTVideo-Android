package com.tencentcs.iotvideodemo.messagemgr

import android.content.Context
import java.util.ArrayList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

internal class DeviceMessageMgrViewModel : ViewModel() {
    val modelLiveData: MutableLiveData<ArrayList<DeviceModelItemData>> = MutableLiveData()

    private val messageManager: DeviceMessageManager = DeviceMessageManager()

    var deviceId: String? = null

    fun initModelData(context: Context, deviceId: String) {
        messageManager.initModelData(context, deviceId, modelLiveData)
        this.deviceId = deviceId
    }

    fun updateModelData() {
        messageManager.updateModelData(deviceId!!, modelLiveData)
    }
}
