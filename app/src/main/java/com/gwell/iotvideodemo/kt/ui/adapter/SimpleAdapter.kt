package com.gwell.iotvideodemo.kt.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gwell.iotvideodemo.kt.ui.adapter.ViewHolder.Companion.getViewHolder

open class SimpleAdapter<T> constructor(dataList: List<T>, vararg item: ItemHolder<T>, var onItemViewType: ((item: T, position: Int) -> Int)? = null) : RecyclerView.Adapter<ViewHolder<T>>() {

    var data: List<T> = dataList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var items: MutableList<ItemHolder<T>> = mutableListOf()

    init {
        item.forEach {
            items.add(it)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        if (items.isNotEmpty()) {
            items.filter {
                it.layoutType == viewType
            }.forEach {
                return getViewHolder(parent.context, this, parent, it)
            }
        }
        throw RuntimeException("必须设置item")
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        val itemHolder = holder.itemHolder
        itemHolder.bindData?.invoke(holder, data[position], position)
        itemHolder.bindEvent?.invoke(holder, data[position], position)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            holder.itemHolder.bindDataPayloads?.invoke(holder, data[position], position, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemViewType(position: Int): Int {
        onItemViewType?.let {
            return it.invoke(data[position], position)
        }
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class ViewHolder<T>(itemView: View, val adapter: SimpleAdapter<T>, val itemHolder: ItemHolder<T>) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun <T> getViewHolder(context: Context, adapter: SimpleAdapter<T>, parent: ViewGroup, itemHolder: ItemHolder<T>): ViewHolder<T> {
            val itemView = LayoutInflater.from(context).inflate(itemHolder.resLayoutId, parent, false)
            return ViewHolder(itemView, adapter, itemHolder)
        }
    }
}


