package com.tencentcs.iotvideodemo.kt.base

import io.reactivex.disposables.Disposable

interface IBasePresenter {

    //将网络请求的每一个disposable添加进入CompositeDisposable，再退出时候一并注销
    fun addDisposable(disposable: Disposable)

    fun removeDisposable(disposable: Disposable)

    fun dispose()

}
