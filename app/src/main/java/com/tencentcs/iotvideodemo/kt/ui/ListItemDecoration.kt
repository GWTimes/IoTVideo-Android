package com.tencentcs.iotvideodemo.kt.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListItemDecoration (var mDividerHeight:Int = 0,mColor: Int = Color.TRANSPARENT,var isShowFirst:Boolean = false) : RecyclerView.ItemDecoration() {

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mPaint.color = mColor
        mPaint.style = Paint.Style.FILL
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        var layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            var startIndex = if(isShowFirst) 0 else 1
            for(position in startIndex until parent.childCount){
                var child = parent.getChildAt(position)
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    val rect = Rect(child.left,child.top-mDividerHeight,child.right,child.top)
                    c.drawRect(rect,mPaint)
                }else if(layoutManager.orientation == LinearLayoutManager.HORIZONTAL){
                    val rect = Rect(child.left,child.top,child.left + mDividerHeight,child.bottom)
                    c.drawRect(rect,mPaint)
                }
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        var layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val childAdapterPosition = parent.getChildAdapterPosition(view)
            if (isShowFirst || (!isShowFirst && childAdapterPosition != 0)) {
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    outRect.top = mDividerHeight
                }else if(layoutManager.orientation == LinearLayoutManager.HORIZONTAL){
                    outRect.left = mDividerHeight
                }
            }
        }
    }


}
