package com.gwell.iotvideodemo.vas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.gwell.http.SubscriberListener;
import com.gwell.http.utils.HttpUtils;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideo.vas.VasService;
import com.gwell.iotvideo.vas.vas;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceList;
import com.gwell.iotvideodemo.base.BaseActivity;
import com.gwell.iotvideodemo.videoplayer.ExoPlayerActivity;
import com.gwell.iotvideodemo.videoplayer.IjkPlayerActivity;
import com.gwell.iotvideodemo.widget.RecycleViewDivider;
import com.gwell.iotvideodemo.widget.SimpleRecyclerViewAdapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VasActivity extends BaseActivity implements View.OnClickListener, SimpleRecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "VasActivity";

    private RadioGroup mRGChannel;
    private TextView mTvBuyStartTime, mTvBuyEndTime, mTvPlaybackStartTime, mTvPlaybackEndTime;
    private RecyclerView mRVPlaybackList;
    private SimpleRecyclerViewAdapter<String> mAdapter;
    private List<String> mM3U8List;

    private VasService mVasService;

    private int mBuyStartTime, mBuyEndTime;
    private long mPlaybackStartTime, mPlaybackEndTime;

    private DeviceList.Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vas);
        mRGChannel = findViewById(R.id.rg_group);
        mRGChannel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                LogUtils.i(TAG, "onCheckedChanged i = " + i);
                if (i == R.id.rb_p2p) {
                    mVasService = vas.getVasService(vas.VIA_P2P);
                } else if (i == R.id.rb_http) {
                    mVasService = vas.getVasService(vas.VIA_HTTP);
                }
            }
        });
        findViewById(R.id.test).setOnClickListener(this);
        mTvBuyStartTime = findViewById(R.id.buy_start_time);
        mTvBuyStartTime.setOnClickListener(this);
        mTvBuyEndTime = findViewById(R.id.buy_end_time);
        mTvBuyEndTime.setOnClickListener(this);
        mTvPlaybackStartTime = findViewById(R.id.playback_start_time);
        mTvPlaybackStartTime.setOnClickListener(this);
        mTvPlaybackEndTime = findViewById(R.id.playback_end_time);
        mTvPlaybackEndTime.setOnClickListener(this);
        mRVPlaybackList = findViewById(R.id.playback_list);
        mM3U8List = new ArrayList<>();
        mAdapter = new SimpleRecyclerViewAdapter<>(this, mM3U8List);
        mRVPlaybackList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRVPlaybackList.addItemDecoration(new RecycleViewDivider(this, RecycleViewDivider.VERTICAL));
        mRVPlaybackList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mVasService = vas.getVasService();
        findViewById(R.id.buy).setOnClickListener(this);
        findViewById(R.id.playback).setOnClickListener(this);
        if (getIntent() != null) {
            mDevice = (DeviceList.Device) getIntent().getSerializableExtra("Device");
            LogUtils.i(TAG, "mDevice = " + mDevice.toString());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.test) {

        } else if (view.getId() == R.id.buy_start_time) {
            showDatePickerDialog(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    mBuyStartTime = dataToTime(year, monthOfYear, dayOfMonth, mTvBuyStartTime, mTvPlaybackStartTime);
                    mPlaybackStartTime = mBuyStartTime * 1000L;
                }
            });
        } else if (view.getId() == R.id.buy_end_time) {
            showDatePickerDialog(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    mBuyEndTime = dataToTime(year, monthOfYear, dayOfMonth, mTvBuyEndTime, mTvPlaybackEndTime);
                    mPlaybackEndTime = mBuyEndTime * 1000L;
                }
            });
        } else if (view.getId() == R.id.playback_start_time) {
            showTimePickerDialog(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    mPlaybackStartTime = timeToTime(mBuyStartTime, i, i1, mTvBuyStartTime, mTvPlaybackStartTime);
                }
            });
        } else if (view.getId() == R.id.playback_end_time) {
            showTimePickerDialog(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    mPlaybackEndTime = timeToTime(mBuyEndTime, i, i1, mTvBuyEndTime, mTvPlaybackEndTime);
                }
            });
        } else if (view.getId() == R.id.buy) {
            mVasService.cloudStorageCreate(mDevice.getDevId(), mBuyStartTime, mBuyEndTime, new SubscriberListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(JsonObject response) {
                    Snackbar.make(mTvBuyStartTime, response.toString(), Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onFail(Throwable e) {
                    Snackbar.make(mTvBuyStartTime, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        } else if (view.getId() == R.id.playback) {
            mVasService.cloudStoragePlayback(mDevice.getDevId(), 28800, mPlaybackStartTime, mPlaybackEndTime, new SubscriberListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(JsonObject response) {
                    PlaybackList playbackList = HttpUtils.JsonToEntity(response.toString(), PlaybackList.class);
                    if (playbackList == null) {
                        Snackbar.make(mRVPlaybackList, "invalid data", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (playbackList.getData() != null && playbackList.getData().getPalyList() != null) {
                        mM3U8List.clear();
                        for (PlaybackList.DataBean.PalyListBean item : playbackList.getData().getPalyList()) {
                            mM3U8List.add(item.getM3u8Url());
                        }
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(mRVPlaybackList, "count = " + mM3U8List.size(), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(mRVPlaybackList, "invalid data", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFail(Throwable e) {
                    Snackbar.make(mRVPlaybackList, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePickerDialog(TimePickerDialog.OnTimeSetListener listener) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, listener,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);

        timePickerDialog.show();
    }

    private int dataToTime(int year, int monthOfYear, int dayOfMonth, TextView dateTextView, TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        dateTextView.setText(date);
        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        timeTextView.setText(time);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    private long timeToTime(int dataTime, int hourOfDay, int minute, TextView dateTextView, TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dataTime * 1000L);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        dateTextView.setText(date);
        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        timeTextView.setText(time);
        return calendar.getTimeInMillis();
    }

    private void startPlayActivity(String url) {
        Intent intent = new Intent(this, IjkPlayerActivity.class);
        intent.putExtra("URI", url);
        startActivity(intent);
    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        startPlayActivity(mM3U8List.get(position));
    }
}
