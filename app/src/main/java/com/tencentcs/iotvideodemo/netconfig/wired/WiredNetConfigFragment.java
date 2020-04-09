package com.tencentcs.iotvideodemo.netconfig.wired;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.netconfig.DeviceInfo;
import com.tencentcs.iotvideo.netconfig.NetConfigInfo;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModel;
import com.tencentcs.iotvideodemo.netconfig.NetConfigViewModelFactory;
import com.tencentcs.iotvideodemo.videoplayer.MonitorPlayerActivity;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WiredNetConfigFragment extends BaseFragment {

    private LinearLayout mRootView;
    private TextView mTvNetConfigInfo;
    private RecyclerView mRVDeviceList;
    private NetConfigViewModel mNetConfigInfoViewModel;
    private RecyclerView.Adapter<DeviceItemHolder> mAdapter;
    private List<DeviceInfo> mDeviceInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wired_net_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDeviceInfoList = new ArrayList<>();
        mRootView = view.findViewById(R.id.root_view);
        mTvNetConfigInfo = view.findViewById(R.id.net_config_info);
        mRVDeviceList = view.findViewById(R.id.device_list);
        mRVDeviceList.addItemDecoration(new RecycleViewDivider(getActivity(), RecycleViewDivider.VERTICAL));
        mRVDeviceList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mAdapter = new RecyclerView.Adapter<DeviceItemHolder>() {
            @NonNull
            @Override
            public DeviceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_scan_device, parent, false);
                DeviceItemHolder holder = new DeviceItemHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull DeviceItemHolder holder, int position) {
                final DeviceInfo deviceInfo = mDeviceInfoList.get(position);
                holder.tvDeviceName.setText(deviceInfo.tencentID);
                holder.tvBindDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bindDevice(deviceInfo.tencentID);
                    }
                });
                holder.tvMonitor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent playIntent = new Intent(WiredNetConfigFragment.this.getContext(), MonitorPlayerActivity.class);
                        playIntent.putExtra("deviceID", deviceInfo.tencentID);
                        startActivity(playIntent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mDeviceInfoList.size();
            }
        };
        mRVDeviceList.setAdapter(mAdapter);
        view.findViewById(R.id.find_devices).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findDevices();
            }
        });
        mNetConfigInfoViewModel = ViewModelProviders.of(getActivity(), new NetConfigViewModelFactory())
                .get(NetConfigViewModel.class);
        NetConfigInfo netConfigInfo = mNetConfigInfoViewModel.getNetConfigInfo();
        mTvNetConfigInfo.setText(netConfigInfo.toString());
        mNetConfigInfoViewModel.getLanDeviceData().observe(getActivity(), new Observer<DeviceInfo[]>() {
            @Override
            public void onChanged(DeviceInfo[] deviceInfos) {
                if (deviceInfos != null && deviceInfos.length > 0) {
                    mDeviceInfoList.clear();
                    mDeviceInfoList.addAll(Arrays.asList(deviceInfos));
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(mRootView, "device count = " + mDeviceInfoList.size(), Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mRootView, "no device", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    class DeviceItemHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName;
        TextView tvBindDevice;
        TextView tvMonitor;

        DeviceItemHolder(View view) {
            super(view);
            tvBindDevice = view.findViewById(R.id.bind_device);
            tvDeviceName = view.findViewById(R.id.device_name);
            tvMonitor = view.findViewById(R.id.monitor_device);
        }
    }

    private void findDevices() {
        mNetConfigInfoViewModel.findDevice();
    }

    private void bindDevice(String devId) {
        mNetConfigInfoViewModel.bindDevice(devId);
    }
}
