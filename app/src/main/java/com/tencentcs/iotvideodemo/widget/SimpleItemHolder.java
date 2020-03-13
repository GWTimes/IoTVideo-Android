package com.tencentcs.iotvideodemo.widget;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencentcs.iotvideodemo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemHolder extends RecyclerView.ViewHolder {
    public LinearLayout rootView;
    public TextView contentView;

    public SimpleItemHolder(@NonNull View itemView) {
        super(itemView);
        rootView = itemView.findViewById(R.id.root_view);
        contentView = itemView.findViewById(R.id.content_view);
    }
}
