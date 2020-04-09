package com.tencentcs.iotvideodemo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencentcs.iotvideodemo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleRecyclerViewAdapter<T> extends RecyclerView.Adapter<SimpleItemHolder> {
    private Context mContext;
    private List<T> mDataList;
    private OnItemClickListener mOnItemClickListener;
    private int mItemVerticalPadding;

    public SimpleRecyclerViewAdapter(Context context, List<T> list) {
        mContext = context;
        mDataList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setItemVerticalPadding(int value) {
        mItemVerticalPadding = value;
    }

    @NonNull
    @Override
    public SimpleItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_simple_recyclerview, parent, false);
        SimpleItemHolder holder = new SimpleItemHolder(view);
        holder.rootView.setPadding(0, mItemVerticalPadding, 0, mItemVerticalPadding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleItemHolder holder, final int position) {
        T simpleItem = mDataList.get(position);
        holder.contentView.setText(simpleItem.toString());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onRecyclerViewItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface OnItemClickListener {
        void onRecyclerViewItemClick(final int position);
    }
}
