package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.messagemgr.IModelListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.netconfig.NetConfigInfo;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.deviceshare.DeviceShareActivity;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.messagemgr.DeviceMessageActivity;
import com.tencentcs.iotvideodemo.netconfig.NetConfigActivity;
import com.tencentcs.iotvideodemo.netconfig.PrepareNetConfigActivity;
import com.tencentcs.iotvideodemo.utils.AppConfig;
import com.tencentcs.iotvideodemo.vas.CloudStorageActivity;
import com.tencentcs.iotvideodemo.videoplayer.MonitorPlayerActivity;
import com.tencentcs.iotvideodemo.videoplayer.MultiMonitorPlayerActivity;
import com.tencentcs.iotvideodemo.videoplayer.PlaybackPlayerActivity;
import com.tencentcs.iotvideodemo.videoplayer.LocalAlbumActivity;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class DeviceListFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "DeviceManagerActivity";

    private static final boolean ENABLE_MULTI_MONITOR = false;

    private RecyclerView mRVDeviceList;
    private Button mBtnMultiMonitor;
    private TextView mTvAddDevice;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.Adapter<DeviceItemHolder> mAdapter;
    private List<DeviceList.Device> mDeviceInfoList;
    private List<DeviceList.Device> mDeviceSelectedList;
    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDeviceInfoList = new ArrayList<>();
        mDeviceSelectedList = new ArrayList<>();
        mRVDeviceList = view.findViewById(R.id.device_list);
        mBtnMultiMonitor = view.findViewById(R.id.btn_multi_monitor);
        mBtnMultiMonitor.setOnClickListener(this);
        mTvAddDevice = view.findViewById(R.id.tv_add_device);
        mTvAddDevice.setOnClickListener(this);
        mRVDeviceList.addItemDecoration(new RecycleViewDivider(getActivity(), RecycleViewDivider.VERTICAL));
        mRVDeviceList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mAdapter = new RecyclerView.Adapter<DeviceItemHolder>() {
            @NonNull
            @Override
            public DeviceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_manager_device, parent, false);
                DeviceItemHolder holder = new DeviceItemHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull final DeviceItemHolder holder, int position) {
                final DeviceList.Device deviceInfo = mDeviceInfoList.get(position);
                holder.tvDeviceName.setText(deviceInfo.getDeviceName());
                if (!TextUtils.isEmpty(deviceInfo.getDevId())) {
                    if (DeviceModelHelper.isOnline(deviceInfo.getDevId())) {
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
                holder.llDeviceInfo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!ENABLE_MULTI_MONITOR) {
                            return false;
                        }
                        deviceInfo.setSelected(!deviceInfo.isSelected());
                        if (deviceInfo.isSelected()) {
                            mDeviceSelectedList.add(deviceInfo);
                        } else {
                            mDeviceSelectedList.remove(deviceInfo);
                        }
                        updateMultiMonitorBtn();
                        mAdapter.notifyDataSetChanged();
                        return true;
                    }
                });
                if (deviceInfo.isSelected()) {
                    holder.llDeviceInfo.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                } else {
                    holder.llDeviceInfo.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
            }

            @Override
            public int getItemCount() {
                return mDeviceInfoList.size();
            }
        };
        mRVDeviceList.setAdapter(mAdapter);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryDeviceList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        queryDeviceList();
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
                        monitorIntent.putExtra("useMediaCodec", AppConfig.USE_MEIDACODEC);
                        monitorIntent.putExtra("renderDirectly", AppConfig.RENDER_DIRECTLY);
                        monitorIntent.putExtra("renderDirectlyType", AppConfig.RENDER_DIRECTLY_TYPE);
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
                            unbindDevice(device);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_vas:
                        if ("owner".equals(device.getShareType())) {
                            Intent vasIntent = new Intent(getActivity(), CloudStorageActivity.class);
                            vasIntent.putExtra("Device", device);
                            startActivity(vasIntent);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_local_album:
                        Intent albumIntent = new Intent(getActivity(), LocalAlbumActivity.class);
                        albumIntent.putExtra("deviceID", device.getDevId());
                        startActivity(albumIntent);
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
                mTvAddDevice.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG, "queryDeviceList = " + response.toString());
                DeviceList deviceList = JSONUtils.JsonToEntity(response.toString(), DeviceList.class);
                if (deviceList.getData() != null) {
                    mDeviceInfoList = deviceList.getData();
                    mDeviceSelectedList.clear();
                    updateMultiMonitorBtn();
                    updateDeviceModel();
                    registerNotify();
                    if (mDeviceInfoList.size() != 0) {
                        mAdapter.notifyDataSetChanged();
                        mTvAddDevice.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(mRVDeviceList, "device count = 0", Snackbar.LENGTH_LONG).show();
                        mTvAddDevice.setVisibility(View.VISIBLE);
                    }
                } else {
                    Snackbar.make(mRVDeviceList, response.toString(), Snackbar.LENGTH_LONG).show();
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                Snackbar.make(mRVDeviceList, e.getMessage(), Snackbar.LENGTH_LONG).show();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (mDeviceInfoList.isEmpty()) {
                    mTvAddDevice.setVisibility(View.VISIBLE);
                }
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
                mDeviceSelectedList.remove(device);
                updateMultiMonitorBtn();
                mAdapter.notifyDataSetChanged();
                Snackbar.make(mRVDeviceList, response.toString(), Snackbar.LENGTH_LONG).show();
                if (mDeviceInfoList.isEmpty()) {
                    mTvAddDevice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                Snackbar.make(mRVDeviceList, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_multi_monitor) {
            Intent playIntent = new Intent(getActivity(), MultiMonitorPlayerActivity.class);
            String[] deviceIdArray = new String[mDeviceSelectedList.size()];
            for (int i = 0; i < mDeviceSelectedList.size(); i++) {
                deviceIdArray[i] = mDeviceSelectedList.get(i).getDevId();
            }
            playIntent.putExtra("deviceIDArray", deviceIdArray);
            startActivity(playIntent);
        } else if (v.getId() == R.id.tv_add_device) {
            NetConfigInfo netConfigInfo = new NetConfigInfo("", "", (byte) 2);
            Intent intent = new Intent(getActivity(), NetConfigActivity.class);
            intent.putExtra("NetConfigInfo", netConfigInfo);
            startActivity(intent);
        }
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

    private Runnable mUpdateModelRunnable = new Runnable() {
        @Override
        public void run() {
            updateDeviceModel();
        }
    };

    private void updateDeviceModel() {
        if (mDeviceInfoList != null && mDeviceInfoList.size() > 0) {
            for (DeviceList.Device device : mDeviceInfoList) {
                IoTVideoSdk.getMessageMgr().readProperty(device.getDevId(), "", new IResultListener<ModelMessage>() {

                    @Override
                    public void onStart() {
                        LogUtils.i(TAG, "updateDeviceModel start");
                        mHandler.removeCallbacks(mUpdateModelRunnable);
                    }

                    @Override
                    public void onSuccess(ModelMessage msg) {
                        try {
                            JSONObject jsonObject = new JSONObject(msg.data);
                            DeviceModelManager.DeviceModel model = new DeviceModelManager.DeviceModel(msg.device, jsonObject);
                            DeviceModelManager.getInstance().setDeviceModel(model);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        LogUtils.e(TAG, "updateDeviceModel " + errorMsg);
                        //如果获取失败，则1分钟之后重新获取
                        mHandler.postDelayed(mUpdateModelRunnable, 60 * 1000);
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

    private void updateMultiMonitorBtn() {
        if (mDeviceSelectedList.size() > 1) {
            mBtnMultiMonitor.setVisibility(View.VISIBLE);
        } else {
            mBtnMultiMonitor.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
