package com.tencentcs.iotvideodemo.vas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.httpviap2p.HttpResultTransformUtils;
import com.tencentcs.iotvideo.netconfig.DeviceInfo;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModel;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModelFactory;
import com.tencentcs.iotvideodemo.videoplayer.MonitorPlayerActivity;
import com.tencentcs.iotvideodemo.videoplayer.ijkplayer.IjkPlayerActivity;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;
import com.tencentcs.iotvideodemo.widget.SimpleRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventListFragment extends BaseFragment implements View.OnClickListener, SimpleRecyclerViewAdapter.OnItemClickListener{

    private static final String TAG = "PlaybackListFragment";

    private List<String> mEventList;
    private SimpleRecyclerViewAdapter<String> mAdapter;
    private RecyclerView mRVPlaybackList;
    private CloudStorageActivity mParentInstance;
    private Button mBtRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_event_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParentInstance = (CloudStorageActivity)getActivity();
        mEventList = new ArrayList<>();
        mAdapter = new SimpleRecyclerViewAdapter<>(getActivity(), mEventList);
        mRVPlaybackList = view.findViewById(R.id.event_list);

        mRVPlaybackList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mRVPlaybackList.addItemDecoration(new RecycleViewDivider(getActivity(), RecycleViewDivider.VERTICAL));
        mRVPlaybackList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mBtRefresh = view.findViewById(R.id.bt_refresh);
        mBtRefresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_refresh) {
            mEventList.clear();
            queryEventList(mParentInstance.getmPlaybackStartTime()/1000,mParentInstance.getmPlaybackEndTime()/1000);
        }
    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        queryEventInfo(mEventList.get(position));
    }

    private void queryEventList(long startTime, long endTime) {
        LogUtils.i(TAG,"queryEventList deviceId:" + mParentInstance.getmDevice().getDevId() + "; startTime:" + startTime + "; endTime:" + endTime);
        mParentInstance.getmVasService().getEventListWithDeviceId(mParentInstance.getmDevice().getDevId(), startTime, endTime, -1,50, new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                String jsonStr = response.toString();
                LogUtils.i(TAG,"queryEventList response strSize:" + jsonStr.length() + "; jsonStr:" + jsonStr);
                if (!isVisible()) {
                    LogUtils.i(TAG,"view is invisible");
                    return;
                }
                CloudEventList eventList = JSONUtils.JsonToEntity(response.toString(), CloudEventList.class);
                if (eventList == null) {
                    Snackbar.make(mBtRefresh, "invalid data", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (eventList.getData() != null
                        && null != eventList.getData().getList()
                        && eventList.getData().getList().size() > 0) {
                    for (CloudEventList.DataEntity.ListEntity entity : eventList.getData().getList()) {
                        mEventList.add(entity.getAlarmId());
                    }
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(mBtRefresh, "count = " + mEventList.size(), Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mBtRefresh, "未查询到事件数据", Snackbar.LENGTH_LONG).show();
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

    private void queryEventInfo(final String alarmId) {
        mParentInstance.getmVasService().queryEventInfo(mParentInstance.getmDevice().getDevId(), alarmId, new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG,"queryEventInfo response:" + response.toString());
                if (!isVisible()) {
                    LogUtils.i(TAG,"view is invisible");
                    return;
                }
                EventInfo eventInfo = JSONUtils.JsonToEntity(response.toString(), EventInfo.class);
                if (eventInfo == null) {
                    Snackbar.make(mBtRefresh, "invalid data", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (eventInfo.getData() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("event info:" + alarmId);
                    builder.setMessage(eventInfo.getData().toString());
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setCancelable(true);
                    //设置正面按钮
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteEvent(alarmId);
                            dialog.dismiss();
                        }
                    });
                    //设置反面按钮
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    Snackbar.make(mBtRefresh, "invalid data", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFail(@NonNull Throwable e) {

            }
        });
    }


    public void deleteEvent(String alarmId) {
        List<String> alarmIdList = new ArrayList<>();
        alarmIdList.add(alarmId);
        mParentInstance.getmVasService().deleteEventsWithDeviceId(mParentInstance.getmDevice().getDevId(), alarmIdList, new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG,"deleteEvent response:" + response.toString());
                if (!isVisible()) {
                    LogUtils.i(TAG,"view is invisible");
                    return;
                }
                if (response.has("code") && 0 == response.get("code").getAsInt()){
                    Snackbar.make(mBtRefresh, "delete event successful", Snackbar.LENGTH_LONG).show();
                    mEventList.remove(alarmId);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(mBtRefresh, "invalid data", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                LogUtils.i(TAG,"deleteEvent exception:" + e.getMessage());
            }
        });
    }
}
