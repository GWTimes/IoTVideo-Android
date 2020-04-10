package com.tencentcs.iotvideodemo.messagemgr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DeviceMessageMgrViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(DeviceMessageMgrViewModel::class.java)) {
            DeviceMessageMgrViewModel() as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
