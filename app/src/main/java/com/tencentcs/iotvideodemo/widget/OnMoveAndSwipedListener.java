package com.tencentcs.iotvideodemo.widget;

public interface OnMoveAndSwipedListener {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
