package com.tencentcs.iotvideodemo.vas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.IoTVideoError;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.messagemgr.DataMessage;
import com.tencentcs.iotvideo.netconfig.DeviceInfo;
import com.tencentcs.iotvideo.netconfig.NetConfigInfo;
import com.tencentcs.iotvideo.netconfig.NetConfigResult;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.Utils;
import com.tencentcs.iotvideo.utils.qrcode.QRCode;
import com.tencentcs.iotvideo.utils.qrcode.QRCodeHelper;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModel;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModelFactory;
import com.tencentcs.iotvideodemo.videoplayer.ijkplayer.IjkPlayerActivity;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;
import com.tencentcs.iotvideodemo.widget.SimpleRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class TsListFragment extends BaseFragment implements View.OnClickListener{
    private static final String TAG = "PlaybackListFragment";

    private List<String> mTsList;
    private SimpleRecyclerViewAdapter<String> mAdapter;
    private RecyclerView mRVPlaybackList;
    private CloudStorageActivity mParentInstance;
    private Button mBtRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_ts_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParentInstance = (CloudStorageActivity)getActivity();
        mTsList = new ArrayList<>();
        mAdapter = new SimpleRecyclerViewAdapter<>(getActivity(), mTsList);
        mRVPlaybackList = view.findViewById(R.id.ts_list);

        mRVPlaybackList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mRVPlaybackList.addItemDecoration(new RecycleViewDivider(getActivity(), RecycleViewDivider.VERTICAL));
        mRVPlaybackList.setAdapter(mAdapter);
        mBtRefresh = view.findViewById(R.id.bt_refresh);
        mBtRefresh.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_refresh) {
            mTsList.clear();
            queryTsList(mParentInstance.getmPlaybackStartTime()/1000,mParentInstance.getmPlaybackEndTime()/1000);
        }
    }

    private void queryTsList(long startTime, long endTime) {
        mParentInstance.getmVasService().getVideoPlayListWithDeviceId(mParentInstance.getmDevice().getDevId(), startTime, endTime, new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG,"queryTsList response:" + response.toString());
                if (!isVisible()) {
                    LogUtils.i(TAG,"view is invisible");
                    return;
                }
                TsListEntity tsList = JSONUtils.JsonToEntity(response.toString(), TsListEntity.class);
                if (tsList == null) {
                    Snackbar.make(mBtRefresh, "invalid data", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (tsList.getData() != null && tsList.getData().size() >= 0) {
                    for (TsListEntity.DataEntity tsItem : tsList.getData()) {
                        mTsList.add(tsItem.getStart() + "~" + tsItem.getEnd());
                    }
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(mBtRefresh, "count = " + mTsList.size(), Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mBtRefresh, "invalid data", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                if (!isVisible()) {
                    LogUtils.i(TAG,"view is invisible");
                    return;
                }
                Snackbar.make(mBtRefresh, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
