package com.tencentcs.iotvideodemo.kt.ui.adapter

import android.view.View
import android.widget.TextView

fun <T> ViewHolder<T>.setText(view: View,text:String?) {
    if (view is TextView && !text.isNullOrEmpty()) {
        view.text = text
    }
}

fun <T> ViewHolder<T>.getItem(action: (item: T) -> Unit) {
    val pos = adapterPosition
    if (pos >= 0) {
        action(adapter.data[pos])
    }
}

private fun <T> ViewHolder<T>.getItemB(action: (item: T) -> Boolean): Boolean {
    val pos = adapterPosition
    return if (pos >= 0) {
        action(adapter.data[pos])
    } else {
        false
    }
}

fun <T> ViewHolder<T>.onClick(view: View, action: (item: T) -> Unit) {
    view.setOnClickListener {
        getItem(action)
    }
}

fun <T> ViewHolder<T>.onLongClick(view: View, action: (item: T) -> Boolean) {
    view.setOnLongClickListener {
        getItemB(action)
    }
}