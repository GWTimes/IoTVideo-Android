package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.http.HttpCode;
import com.tencentcs.iotvideo.messagemgr.IModelListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;
import com.tencentcs.iotvideodemo.MyApp;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.deviceshare.DeviceShareActivity;
import com.tencentcs.iotvideodemo.accountmgrtc.data.TencentcsDeviceList;
import com.tencentcs.iotvideodemo.accountmgrtc.httpservice.TencentcsAccountMgr;
import com.tencentcs.iotvideodemo.base.BaseFragment;
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

public class DeviceListFragment extends BaseFragment {
    private static final String TAG = "DeviceManagerActivity";

    private RecyclerView mRVDeviceList;
    private RecyclerView.Adapter<DeviceListFragment.DeviceItemHolder> mAdapter;
    private List<DeviceList.Device> mDeviceInfoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDeviceInfoList = new ArrayList<>();
        mRVDeviceList = view.findViewById(R.id.device_list);
        mRVDeviceList.addItemDecoration(new RecycleViewDivider(getActivity(), RecycleViewDivider.VERTICAL));
        mRVDeviceList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mAdapter = new RecyclerView.Adapter<DeviceListFragment.DeviceItemHolder>() {
            @NonNull
            @Override
            public DeviceListFragment.DeviceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_manager_device, parent, false);
                DeviceListFragment.DeviceItemHolder holder = new DeviceListFragment.DeviceItemHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull final DeviceListFragment.DeviceItemHolder holder, int position) {
                final DeviceList.Device deviceInfo = mDeviceInfoList.get(position);
                holder.tvDeviceName.setText(deviceInfo.getDeviceName());
                if (!TextUtils.isEmpty(deviceInfo.getDevId())) {
                    if (DeviceModelManager.getInstance().isOnline(deviceInfo.getDevId())) {
                        holder.tvOnline.setText("在线");
                        holder.tvOnline.setTextColor(getActivity().getResources().getColor(R.color.normal));
                    } else {
                        holder.tvOnline.setText("离线");
                        holder.tvOnline.setTextColor(getActivity().getResources().getColor(R.color.dangerous));
                    }
                }
                holder.tvType.setText("owner".equals(deviceInfo.getShareType()) ? "主人" : "访客");
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
    public void onResume() {
        super.onResume();
        if (MyApp.ENABLE_TENCENTCS) {
            tencentQueryDeviceList();
        } else {
            queryDeviceList();
        }
    }

    private void showPopupMenu(final View anchor, final DeviceList.Device device) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), anchor);
        popupMenu.getMenuInflater().inflate(R.menu.device_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_menu_monitor_player:
                        Intent monitorIntent = new Intent(getActivity(), MonitorPlayerActivity.class);
                        monitorIntent.putExtra("deviceID", device.getDevId());
                        startActivity(monitorIntent);
                        break;
                    case R.id.action_menu_playback_player:
                        Intent playbackIntent = new Intent(getActivity(), PlaybackPlayerActivity.class);
                        playbackIntent.putExtra("deviceID", device.getDevId());
                        startActivity(playbackIntent);
                        break;
                    case R.id.action_menu_model:
                        Intent messageIntent = new Intent(getActivity(), DeviceMessageActivity.class);
                        messageIntent.putExtra("deviceID", device.getDevId());
                        startActivity(messageIntent);
                        break;
                    case R.id.action_menu_ota:
                        if ("owner".equals(device.getShareType())) {
                            Intent shareIntent = new Intent(getActivity(), DeviceOTAActivity.class);
                            shareIntent.putExtra("deviceID", device.getDevId());
                            startActivity(shareIntent);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_share:
                        if ("owner".equals(device.getShareType())) {
                            Intent shareIntent = new Intent(getActivity(), DeviceShareActivity.class);
                            shareIntent.putExtra("Device", device);
                            startActivity(shareIntent);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_unbind:
                        if ("owner".equals(device.getShareType())) {
                            if (MyApp.ENABLE_TENCENTCS) {
                                tencentcsUnbindDevice(device);
                            } else {
                                unbindDevice(device);
                            }
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_vas:
                        if ("owner".equals(device.getShareType())) {
                            Intent vasIntent = new Intent(getActivity(), VasActivity.class);
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
                    registerNotify();
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

    private void tencentQueryDeviceList() {
        TencentcsAccountMgr.getHttpService().DescribeBindDev(TencentcsAccountMgr.getAccessId(), new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG, "tencentQueryDeviceList = " + response.toString());
                TencentcsDeviceList deviceList = JSONUtils.JsonToEntity(response.toString(), TencentcsDeviceList.class);
                if (deviceList.getResponse() == null || deviceList.getResponse().getData() == null) {
                    Snackbar.make(mRVDeviceList, "device count = 0", Snackbar.LENGTH_LONG).show();
                    return;
                }
                mDeviceInfoList.clear();
                for (TencentcsDeviceList.ResponseBean.Device tencentcsDevice : deviceList.getResponse().getData()) {
                    mDeviceInfoList.add(new DeviceList.Device(tencentcsDevice.getTid(), tencentcsDevice.getDeviceName(),
                            tencentcsDevice.getRole(), tencentcsDevice.getDeviceModel()));
                }
                updateDeviceModel();
                registerNotify();
                if (mDeviceInfoList.size() != 0) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(mRVDeviceList, "device count = 0", Snackbar.LENGTH_LONG).show();
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

    private void tencentcsUnbindDevice(final DeviceList.Device device) {
        TencentcsAccountMgr.getHttpService().DeleteBinding(TencentcsAccountMgr.getAccessId(), device.getDevId(), "owner", new SubscriberListener() {
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
        ConstraintLayout llDeviceInfo;
        TextView tvDeviceName;
        TextView tvOperator;
        TextView tvOnline;
        TextView tvType;

        DeviceItemHolder(View view) {
            super(view);
            llDeviceInfo = view.findViewById(R.id.ll_device_info);
            tvDeviceName = view.findViewById(R.id.device_name);
            tvOperator = view.findViewById(R.id.operate_device);
            tvOnline = view.findViewById(R.id.tv_online);
            tvType = view.findViewById(R.id.tv_type);
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
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {

                    }
                });
            }
        }
    }

    private void registerNotify() {
        IoTVideoSdk.getMessageMgr().addModelListener(new IModelListener() {
            @Override
            public void onNotify(ModelMessage data) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
