package com.tencentcs.iotvideodemo.messagemgr

data class DeviceModelTypeData(val name: String, val data: String)

data class DeviceModelFunctionData(val name: String, val data: String)

data class DeviceModelItemData(val type: Int, var typeData: DeviceModelTypeData?, var functionData: DeviceModelFunctionData?)

