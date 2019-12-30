package com.gwell.iotvideodemo.kt.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter : IBasePresenter {

    protected var TAG = this.javaClass.simpleName

    private var mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    override fun removeDisposable(disposable: Disposable) {
        mCompositeDisposable.remove(disposable)
    }

    override fun dispose() {
        mCompositeDisposable.dispose()
    }

    fun clearDisposable() {
        mCompositeDisposable.clear()
    }

    fun hasDisposable() = mCompositeDisposable.size() > 0

}

