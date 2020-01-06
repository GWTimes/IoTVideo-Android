package com.gwell.iotvideodemo.messagemgr

import com.gwell.iotvideodemo.kt.base.IBaseView

interface IDeviceModelView : IBaseView {

    fun updateModelData(list: MutableList<DeviceModelItemData>)

}