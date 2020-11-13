package com.tencentcs.iotvideodemo.accountmgr.devicemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.messagemgr.IModelListener;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils;
import com.tencentcs.iotvideodemo.accountmgr.deviceshare.DeviceShareActivity;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.messagemgr.DeviceMessageActivity;
import com.tencentcs.iotvideodemo.netconfig.PrepareNetConfigActivity;
import com.tencentcs.iotvideodemo.utils.Utils;
import com.tencentcs.iotvideodemo.vas.CloudStorageActivity;
import com.tencentcs.iotvideodemo.videoplayer.LocalAlbumActivity;
import com.tencentcs.iotvideodemo.videoplayer.MonitorPlayerActivity;
import com.tencentcs.iotvideodemo.videoplayer.MultiMonitorPlayerActivity;
import com.tencentcs.iotvideodemo.videoplayer.PlaybackPlayerActivity;
import com.tencentcs.iotvideodemo.videoplayer.TransmissionConnectionActivity;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DeviceListFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "DeviceManagerActivity";

    private RecyclerView mRVDeviceList;
    private TextView mTvAddDevice;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.Adapter<DeviceItemHolder> mAdapter;
    private List<DeviceList.Device> mDeviceInfoList;
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
        mRVDeviceList = view.findViewById(R.id.device_list);
        mTvAddDevice = view.findViewById(R.id.tv_add_device);
        mTvAddDevice.setOnClickListener(this);
        mRVDeviceList.addItemDecoration(new RecycleViewDivider(getActivity(), RecycleViewDivider.VERTICAL));
        mRVDeviceList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mAdapter = new RecyclerView.Adapter<DeviceItemHolder>() {
            @NonNull
            @Override
            public DeviceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_manager_device, parent, false);
                DeviceItemHolder holder;
                holder = new DeviceItemHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull final DeviceItemHolder holder, int position) {
                final DeviceList.Device deviceInfo = mDeviceInfoList.get(position);
                holder.tvDeviceName.setText(deviceInfo.getRemarkName());
                if (!TextUtils.isEmpty(deviceInfo.getDevId())) {
                    if (DeviceModelHelper.isOnline(deviceInfo.getDevId())) {
                        holder.tvOnline.setText("在线");
                        holder.tvOnline.setTextColor(getResources().getColor(R.color.normal));
                    } else {
                        holder.tvOnline.setText("离线");
                        holder.tvOnline.setTextColor(getResources().getColor(R.color.dangerous));
                    }
                }
                holder.tvType.setText(deviceInfo.getRelation() == 1 ? "主人" : "访客");
                holder.tvSysCate.setText(deviceInfo.getDeviceCate() == 1 ? "一期" : "二期");
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
        if (getActivity() == null) {
            return;
        }
        PopupMenu popupMenu = new PopupMenu(getActivity(), anchor);
        popupMenu.getMenuInflater().inflate(R.menu.device_popup_menu, popupMenu.getMenu());
        if (Utils.isOemVersion()) {
            popupMenu.getMenu().findItem(R.id.action_menu_share).setVisible(true);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (getActivity() == null) {
                    return false;
                }
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
                    case R.id.action_menu_transmission_connection:
                        Intent transmissionIntent = new Intent(getActivity(), TransmissionConnectionActivity.class);
                        transmissionIntent.putExtra("deviceID", device.getDevId());
                        startActivity(transmissionIntent);
                        break;
                    case R.id.action_menu_model:
                        Intent messageIntent = new Intent(getActivity(), DeviceMessageActivity.class);
                        messageIntent.putExtra("deviceID", device.getDevId());
                        startActivity(messageIntent);
                        break;
                    case R.id.action_menu_ota:
                        if (device.getRelation() == 1) {
                            Intent shareIntent = new Intent(getActivity(), DeviceOTAActivity.class);
                            shareIntent.putExtra("deviceID", device.getDevId());
                            startActivity(shareIntent);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_share:
                        if (device.getRelation() == 1) {
                            Intent shareIntent = new Intent(getActivity(), DeviceShareActivity.class);
                            shareIntent.putExtra("Device", device);
                            startActivity(shareIntent);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_unbind:
                        if (device.getRelation() == 1) {
                            unbindDevice(device);
                        } else {
                            Snackbar.make(anchor, R.string.you_are_not_owner, Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.action_menu_vas:
                        if (device.getRelation() == 1) {
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
                    case R.id.action_menu_multi_monitor:
                        multiMonitorClicked(getActivity(), device);
                        break;
                    case R.id.action_menu_nvr:
                        nvrClicked(getActivity(), device);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void multiMonitorClicked(@NonNull Context context, final DeviceList.Device device) {
        int deviceCount = mDeviceInfoList.size();
        String[] multiChoiceItems = new String[deviceCount];
        boolean[] checkedItems = new boolean[deviceCount];
        for (int i = 0; i < deviceCount; i++) {
            DeviceList.Device deviceItem = mDeviceInfoList.get(i);
            multiChoiceItems[i] = deviceItem.getRemarkName();
            checkedItems[i] = false;
        }
        List<String> selectedDeviceList = new LinkedList<>();
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.multi_monitor))
                .setMultiChoiceItems(multiChoiceItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean selected) {
                        if (selected) {
                            selectedDeviceList.add(mDeviceInfoList.get(i).getDevId());
                        } else {
                            selectedDeviceList.remove(mDeviceInfoList.get(i).getDevId());
                        }
                    }
                })
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent playIntent = new Intent(getActivity(), MultiMonitorPlayerActivity.class);
                        String[] deviceIdArray = new String[selectedDeviceList.size()];
                        selectedDeviceList.toArray(deviceIdArray);
                        playIntent.putExtra("deviceIDArray", deviceIdArray);
                        startActivity(playIntent);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void nvrClicked(@NonNull Context context, final DeviceList.Device device) {
        String[] multiChoiceItems = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        boolean[] checkedItems = new boolean[multiChoiceItems.length];
        ArrayList<Integer> selectedSourceList = new ArrayList<>();
        new AlertDialog.Builder(context)
                .setTitle("选择source id")
                .setMultiChoiceItems(multiChoiceItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean selected) {
                        if (selected) {
                            selectedSourceList.add(Integer.valueOf(multiChoiceItems[i]));
                        } else {
                            selectedSourceList.remove(Integer.valueOf(multiChoiceItems[i]));
                        }
                    }
                })
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        Collections.sort(selectedSourceList);
                        Intent nvrIntent = new Intent(getActivity(), MultiMonitorPlayerActivity.class);
                        String[] deviceIdArray = new String[selectedSourceList.size()];
                        Arrays.fill(deviceIdArray, device.getDevId());
                        nvrIntent.putExtra("deviceIDArray", deviceIdArray);
                        nvrIntent.putIntegerArrayListExtra("sourceIDArray", selectedSourceList);
                        startActivity(nvrIntent);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void queryDeviceList() {
        if (AccountSPUtils.getInstance().isAnonymousUser(getContext())) {
            //初始化数据
            List<DeviceList.Device> deviceList = new ArrayList<>();
            DeviceList.Device device = new DeviceList.Device();
            final String tid = AccountSPUtils.getInstance().getTid(getContext());
            device.setDevId(tid);
            device.setRemarkName(tid);
            device.setRelation(0);
            device.setVersion(0);
            device.setGroupId(0);
            device.setPermission(271);
            device.setSecretKey(AccountMgr.getSecretKey());
            device.setNoDisturb(null);
            device.setDeviceCate(2);
            deviceList.add(device);
            //显示数据
            mDeviceInfoList = deviceList;
            updateDeviceModel();
            registerNotify();
            mAdapter.notifyDataSetChanged();
            if (mDeviceInfoList.size() != 0) {
                mTvAddDevice.setVisibility(View.GONE);
            } else {
                Snackbar.make(mRVDeviceList, "device count = 0", Snackbar.LENGTH_LONG).show();
                mTvAddDevice.setVisibility(View.VISIBLE);
            }
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        AccountMgr.getHttpService().deviceList(new SubscriberListener() {
            @Override
            public void onStart() {
                mTvAddDevice.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(@NonNull JsonObject response) {
                LogUtils.i(TAG, "queryDeviceList = " + response.toString());
                DeviceList deviceList = JSONUtils.JsonToEntity(response.toString(), DeviceList.class);
                if (deviceList.getData() != null && deviceList.getData().getDeviceList() != null) {
                    mDeviceInfoList = deviceList.getData().getDeviceList();
                    updateDeviceModel();
                    registerNotify();
                    mAdapter.notifyDataSetChanged();
                    if (mDeviceInfoList.size() != 0) {
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
                if (e.getMessage() != null) {
                    Snackbar.make(mRVDeviceList, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
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
                mAdapter.notifyDataSetChanged();
                Snackbar.make(mRVDeviceList, response.toString(), Snackbar.LENGTH_LONG).show();
                if (mDeviceInfoList.isEmpty()) {
                    mTvAddDevice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(@NonNull Throwable e) {
                if (e.getMessage() != null) {
                    Snackbar.make(mRVDeviceList, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_add_device) {
            Intent intent = new Intent(getActivity(), PrepareNetConfigActivity.class);
            startActivity(intent);
        }
    }

    static class DeviceItemHolder extends RecyclerView.ViewHolder {
        ConstraintLayout llDeviceInfo;
        TextView tvDeviceName;
        TextView tvOperator;
        TextView tvOnline;
        TextView tvType;
        TextView tvSysCate;

        DeviceItemHolder(View view) {
            super(view);
            llDeviceInfo = view.findViewById(R.id.ll_device_info);
            tvDeviceName = view.findViewById(R.id.device_name);
            tvOperator = view.findViewById(R.id.operate_device);
            tvOnline = view.findViewById(R.id.tv_online);
            tvType = view.findViewById(R.id.tv_type);
            tvSysCate = view.findViewById(R.id.tv_sys_cate);
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
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = jsonParser.parse(msg.data).getAsJsonObject();
                        DeviceModelManager.DeviceModel model = new DeviceModelManager.DeviceModel(msg.device, jsonObject);
                        DeviceModelManager.getInstance().setDeviceModel(model);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
