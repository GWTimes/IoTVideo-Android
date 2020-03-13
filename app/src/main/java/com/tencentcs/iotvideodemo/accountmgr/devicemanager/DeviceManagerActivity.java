package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.http.HttpCode;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.deviceshare.DeviceShareActivity;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.messagemgr.DeviceMessageActivity;
import com.tencentcs.iotvideodemo.vas.VasActivity;
import com.tencentcs.iotvideodemo.videoplayer.MonitorPlayerActivity;
import com.tencentcs.iotvideodemo.videoplayer.PlaybackPlayerActivity;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceManagerActivity extends BaseActivity {
    private static final String TAG = "DeviceManagerActivity";

    private RecyclerView mRVDeviceList;
    private RecyclerView.Adapter<DeviceItemHolder> mAdapter;
    private List<DeviceList.Device> mDeviceInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manager);
        mDeviceInfoList = new ArrayList<>();
        mRVDeviceList = findViewById(R.id.device_list);
        mRVDeviceList.addItemDecoration(new RecycleViewDivider(this, RecycleViewDivider.VERTICAL));
        mRVDeviceList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAdapter = new RecyclerView.Adapter<DeviceItemHolder>() {
            @NonNull
            @Override
            public DeviceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(DeviceManagerActivity.this).inflate(R.layout.item_manager_device, parent, false);
                DeviceItemHolder holder = new DeviceItemHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull final DeviceItemHolder holder, int position) {
                final DeviceList.Device deviceInfo = mDeviceInfoList.get(position);
                holder.tvDeviceName.setText(deviceInfo.getDeviceName());
                holder.tvOnline.setText(DeviceModelManager.getInstance().isOnline(deviceInfo.getDevId()) ? "在线" : "离线");
                holder.llDeviceInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(holder.tvOperator, deviceInfo);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mDeviceInfoList.size();
            }
        };
        mRVDeviceList.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryDeviceList();
    }

    private void showPopupMenu(final View anchor, final DeviceList.Device device) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.device_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_menu_monitor_player:
                        Intent monitorIntent = new Intent(DeviceManagerActivity.this, MonitorPlayerActivity.class);
                        monitorIntent.putExtra("deviceID", device.getDevId());
                        startActivity(monitorIntent);
                        break;
                    case R.id.action_menu_playback_player:
                        Intent playbackIntent = new Intent(DeviceManagerActivity.this, PlaybackPlayerActivity.class);
                        playbackIntent.putExtra("deviceID", device.getDevId());
                        startActivity(playbackIntent);
                        break;
                    case R.id.action_menu_model:
                        Intent messageIntent = new Intent(DeviceManagerActivity.this, DeviceMessageActivity.class);
                        messageIntent.putExtra("deviceID", device.getDevId());
                        startActivity(messageIntent);
                        break;
                    case R.id.action_menu_share:
                        if ("owner".equals(device.getShareType())) {
                            Intent shareIntent = new Intent(DeviceManagerActivity.this, DeviceShareActivity.class);
                            shareIntent.putExtra("Device", device);
                            startActivity(shareIntent);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_unbind:
                        if ("owner".equals(device.getShareType())) {
                            unbindDevice(device);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_vas:
                        if ("owner".equals(device.getShareType())) {
                            Intent vasIntent = new Intent(DeviceManagerActivity.this, VasActivity.class);
                            vasIntent.putExtra("Device", device);
                            startActivity(vasIntent);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void queryDeviceList() {
        AccountMgr.getHttpService().deviceList(new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG, "queryDeviceList = " + response.toString());
                DeviceList deviceList = JSONUtils.JsonToEntity(response.toString(), DeviceList.class);
                if (deviceList.getCode() == HttpCode.ERROR_0 && deviceList.getData() != null) {
                    mDeviceInfoList = deviceList.getData();
                    updateDeviceModel();
                    if (mDeviceInfoList.size() != 0) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Snackbar.make(mRVDeviceList, "device count = 0", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(mRVDeviceList, response.toString(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                Snackbar.make(mRVDeviceList, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void unbindDevice(final DeviceList.Device device) {
        AccountMgr.getHttpService().deviceUnbind(device.getDevId(), new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                mDeviceInfoList.remove(device);
                mAdapter.notifyDataSetChanged();
                Snackbar.make(mRVDeviceList, response.toString(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                Snackbar.make(mRVDeviceList, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    class DeviceItemHolder extends RecyclerView.ViewHolder {
        LinearLayout llDeviceInfo;
        TextView tvDeviceName;
        TextView tvOperator;
        TextView tvOnline;

        DeviceItemHolder(View view) {
            super(view);
            llDeviceInfo = view.findViewById(R.id.ll_device_info);
            tvDeviceName = view.findViewById(R.id.device_name);
            tvOperator = view.findViewById(R.id.operate_device);
            tvOnline = view.findViewById(R.id.tv_online);
        }
    }

    private void updateDeviceModel(){
        if(mDeviceInfoList != null && mDeviceInfoList.size() > 0){
            for (DeviceList.Device device : mDeviceInfoList){
                IoTVideoSdk.getMessageMgr().readProperty(device.getDevId(), "", new IResultListener<ModelMessage>(){

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(ModelMessage msg) {
                        DeviceModelManager.getInstance().onNotify(msg);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
            }
        }
    }
}
