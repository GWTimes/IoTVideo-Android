package com.tencentcs.iotvideodemo.widget;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleModifyModelDialog extends Dialog {
    private final static String TAG = "SingleModifyModelDialog";
    private TextView mTvTitle;
    private String path;
    private LayoutInflater inflate;
    private Activity mContext;
    private SelectModelDialog.ClickResultListener mResultListener;
    private TextView mTvConfirm;
    private TextView mTvCancel;
    private EditText mEtModifyValue;
    private TextView mTvDelete;
    private String allData;
    private String key;

    public SingleModifyModelDialog(@NonNull Activity context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        inflate = LayoutInflater.from(mContext);
        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dm.widthPixels - 200, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(inflate.inflate(R.layout.layout_modify_single_model_dialog,null),params);
        mTvTitle = findViewById(R.id.tv_title);
        mTvConfirm = findViewById(R.id.tv_ok);
        mTvCancel = findViewById(R.id.tv_cancel);
        mEtModifyValue = findViewById(R.id.et_value);
        mTvDelete = findViewById(R.id.tv_delete);

        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mResultListener) {
                    JsonParser parser = new JsonParser();

                    //JsonObject valueData = new JsonObject();
                    LogUtils.i(TAG,"path:" + path);
                    String[] keyList = path.split("\\.");
                    LogUtils.i(TAG,"keyList:" + Arrays.toString(keyList));
                    String parentKey = keyList[keyList.length - 1];
                    LogUtils.i(TAG,"parentKey:" + parentKey);
                    JsonObject jsonObject = parser.parse(allData).getAsJsonObject().getAsJsonObject(parentKey);
                    boolean isNumber = false;
                    try {
                        jsonObject.get(key).getAsInt();
                        isNumber = true;
                    } catch (Exception e) {
                        isNumber = false;
                    }
                    String jsonValue = mEtModifyValue.getText().toString().trim();
                    if (TextUtils.isEmpty(jsonValue)) {
                        Toast.makeText(mContext, "数据不能为空", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (isNumber && !TextUtils.isDigitsOnly(jsonValue)) {
                        Toast.makeText(mContext, "输入非法", Toast.LENGTH_SHORT);
                        return;
                    } else if (!isNumber) {
                        jsonValue = "\"" + jsonValue + "\"";
                    }
                    mResultListener.onModifySingleModel(path + "." + key, jsonValue);
                }
                dismiss();
            }
        });

        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void setData(String allData, String key, String path, String initValue) {
        LogUtils.i(TAG,"setData path:" + path + "; initValue:" + initValue);
        this.path = path;
        mTvTitle.setText(path + "." + key);
        this.allData = allData;
        this.key = key;
        mEtModifyValue.setText(initValue);
        String[] pathKey = path.split("\\.");
        if (pathKey[1].startsWith("_")) {//内置物模型不允许删除
            mTvDelete.setVisibility(View.GONE);
        }else{
            mTvDelete.setVisibility(View.VISIBLE);
        }
    }

    public void setmResultListener(SelectModelDialog.ClickResultListener mResultListener) {
        this.mResultListener = mResultListener;
    }

    @Override
    public void show() {
        super.show();
    }
}
