package com.tencentcs.iotvideodemo.messagemgr

import com.tencentcs.iotvideodemo.kt.base.IBaseView

interface IDeviceModelView : IBaseView {

    fun updateModelData(list: MutableList<DeviceModelItemData>)

}