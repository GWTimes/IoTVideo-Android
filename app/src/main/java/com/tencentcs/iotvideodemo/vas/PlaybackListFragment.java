package com.tencentcs.iotvideodemo.vas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.tencentcs.iotvideo.httpviap2p.HttpResultTransformUtils;
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
import com.tencentcs.iotvideodemo.widget.BigImageDialog;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;
import com.tencentcs.iotvideodemo.widget.SimpleRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlaybackListFragment extends BaseFragment implements View.OnClickListener , SimpleRecyclerViewAdapter.OnItemClickListener{
    private static final String TAG = "PlaybackListFragment";

    private List<String> mM3U8List;
    private SimpleRecyclerViewAdapter<String> mAdapter;
    private RecyclerView mRVPlaybackList;
    private CloudStorageActivity mParentInstance;
    private Button mBtRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_play_back, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParentInstance = (CloudStorageActivity)getActivity();
        mM3U8List = new ArrayList<>();
        mAdapter = new SimpleRecyclerViewAdapter<>(getActivity(), mM3U8List);
        mRVPlaybackList = view.findViewById(R.id.playback_list);
        mBtRefresh = view.findViewById(R.id.bt_refresh);
        mBtRefresh.setOnClickListener(this);

        mRVPlaybackList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mRVPlaybackList.addItemDecoration(new RecycleViewDivider(getActivity(), RecycleViewDivider.VERTICAL));
        mRVPlaybackList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_refresh) {
            mM3U8List.clear();
            //HttpResultTransformUtils.decompress("eJySuNfAzHDAgCGBAQQkwCQbmGT4uIeBI7kbwv4vwXYDWe77HhAJCAAA//8sfQk5");
            queryPlayList(mParentInstance.getmPlaybackStartTime()/1000,mParentInstance.getmPlaybackEndTime()/1000);
        }
    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        startPlayActivity(mM3U8List.get(position));
    }

    private void queryPlayList(long startTime, long endTime) {
        mParentInstance.getmVasService().getVideoPlayAddressWithDeviceId(mParentInstance.getmDevice().getDevId(), startTime, endTime, new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG,"queryPlayList:" + response.toString());
                if (!isVisible()) {
                    LogUtils.i(TAG,"view is invisible");
                    return;
                }
                PlaybackList playbackList = JSONUtils.JsonToEntity(response.toString(), PlaybackList.class);
                if (playbackList == null) {
                    Snackbar.make(mBtRefresh, "invalid data", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (playbackList.getData() != null && !TextUtils.isEmpty(playbackList.getData().getUrl())) {
                    mM3U8List.add(playbackList.getData().getUrl());
                    if (!playbackList.getData().getEndflag()) {
                        queryPlayList(playbackList.getData().getEndTime(), endTime);
                    }else{
                        mAdapter.notifyDataSetChanged();
                    }
                    LogUtils.i(TAG,"m3u8 play url:" + playbackList.getData().getUrl());
                    Snackbar.make(mBtRefresh, "count = " + mM3U8List.size(), Snackbar.LENGTH_LONG).show();
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

    private void startPlayActivity(String url) {
        Intent intent = new Intent(getActivity(), IjkPlayerActivity.class);
        intent.putExtra("URI", url);
        startActivity(intent);
    }
}
