package com.tencentcs.iotvideodemo.rxbus2.event

import android.os.Bundle

class RenderUpdateEvent(var workId: String, var progress: Int, var type: Int, var isFinish: Boolean = false) {
    var bundle: Bundle? = null
}
