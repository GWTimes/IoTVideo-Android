package com.tencentcs.iotvideodemo.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;

import java.util.ArrayList;
import java.util.List;

public class SelectModelDialog extends Dialog {
    private final static String TAG = "SelectModelDialog";
    private ListView mLvListData;
    private TextView mTvTitle;
    private ListDataAdapter mAdapter;
    private String path;
    private LayoutInflater inflate;
    private Activity mContext;
    private ClickResultListener mResultListener;
    private TextView mTvConfirm;
    private TextView mTvModelInfo;
    private String allData;

    public interface ClickResultListener{
        void onModifySingleModel(String path, String value);
    }

    public SelectModelDialog(@NonNull Activity context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        inflate = LayoutInflater.from(mContext);
        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dm.widthPixels - 200, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(inflate.inflate(R.layout.layout_select_dialog_fragment,null),params);
        mLvListData = findViewById(R.id.lv_mode_list);
        mTvTitle = findViewById(R.id.tv_title);
        mAdapter = new ListDataAdapter();
        mLvListData.setAdapter(mAdapter);
        mTvConfirm = findViewById(R.id.tv_ok);
        mTvModelInfo = findViewById(R.id.tv_all_info);

        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setListData(String path,String allData, List<String> needEditData) {
        LogUtils.i(TAG,"setListData path:" + path + "; needEditData:" + needEditData);
        this.path = path;
        this.allData = allData;
        mTvModelInfo.setText(allData);
        mAdapter.updateListData(needEditData);
        mTvTitle.setText(path);
    }

    private class ListDataAdapter extends BaseAdapter {

        private List<String> listData;

        ListDataAdapter() {
            if (null == listData) {
                listData = new ArrayList<>();
            }else{
                listData.clear();
            }

        }

        private void updateListData(List<String> listData) {
            this.listData = listData;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int i) {
            return listData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View contentView, ViewGroup viewGroup) {
            ViewHolder viewHolder ;
            if (null == contentView) {
                contentView = inflate.inflate(R.layout.item_device_model_function, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.mLLParentView = contentView.findViewById(R.id.ll_parent_view);
                viewHolder.tvModelKey = contentView.findViewById(R.id.tv_function);
                viewHolder.tvEdit = contentView.findViewById(R.id.btn_operate);
                contentView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) contentView.getTag();
            }

            viewHolder.mLLParentView.setBackgroundResource(R.color.viewfinder_text_color);
            viewHolder.tvModelKey.setText(listData.get(i));
            viewHolder.tvEdit.setText("编辑");
            viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showModifyInputDialog(listData.get(i));
                    dismiss();
                }
            });
            return contentView;
        }
    }

    private class ViewHolder{
        LinearLayout mLLParentView;
        TextView tvModelKey;
        MaterialButton tvEdit;
    }

    public void setmResultListener(ClickResultListener mResultListener) {
        this.mResultListener = mResultListener;
    }

    @Override
    public void show() {
        super.show();
    }

    private void showModifyInputDialog(String itemData) {
        String[] keyAndValue = itemData.split("=");
        SingleModifyModelDialog modelDialog = new SingleModifyModelDialog(mContext);
        modelDialog.setData(allData, keyAndValue[0], path, keyAndValue[1]);
        modelDialog.setmResultListener(mResultListener);
        modelDialog.show();
    }
}
