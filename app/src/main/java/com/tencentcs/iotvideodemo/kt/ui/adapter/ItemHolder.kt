package com.tencentcs.iotvideodemo.kt.ui.adapter


class ItemHolder<T>(val resLayoutId: Int,
                    var layoutType: Int = 0){
    var bindData:(ViewHolder<T>.(item: T, position: Int) -> Unit)? = null
    var bindDataPayloads:(ViewHolder<T>.(item: T, position: Int, payloads: MutableList<Any>) -> Unit)? = null
    var bindEvent:(ViewHolder<T>.(item: T, position: Int) -> Unit)? = null

    fun bindData(bind:(ViewHolder<T>.(item: T, position: Int) -> Unit)?): ItemHolder<T> {
        bindData = bind
        return this
    }

    fun bindDataPayloads(bind:(ViewHolder<T>.(item: T, position: Int, payloads: MutableList<Any>) -> Unit)?): ItemHolder<T> {
        bindDataPayloads = bind
        return this
    }

    fun bindEvent(bind:(ViewHolder<T>.(item: T, position: Int) -> Unit)?): ItemHolder<T> {
        bindEvent = bind
        return this
    }

}
