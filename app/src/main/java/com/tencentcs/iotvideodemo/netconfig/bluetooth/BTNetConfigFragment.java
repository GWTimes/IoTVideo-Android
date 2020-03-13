package com.tencentcs.iotvideodemo.netconfig.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.tencentcs.iotvideo.netconfig.bluetooth.BLEManager;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class BTNetConfigFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "BTNetConfigFragment";

    private Button mBluetoothSwitchBtn;
    private Button mBluetoothScanBtn;
    private RecyclerView mBluetoothRV;
    private RecyclerView.Adapter<BLEViewHolder> mAdapter;

    private List<BleDevice> mBleDeviceList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bt_net_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);mBluetoothSwitchBtn = view.findViewById(R.id.bluetooth_switch);
        mBluetoothScanBtn = view.findViewById(R.id.scan_bluetooth);
        mBluetoothRV = view.findViewById(R.id.bluetooth_list);
        mBluetoothScanBtn.setOnClickListener(this);
        mBluetoothSwitchBtn.setOnClickListener(this);
        if (BLEManager.isBlueEnable()) {
            mBluetoothSwitchBtn.setText(R.string.disable_bluetooth);
        } else {
            mBluetoothSwitchBtn.setText(R.string.enable_bluetooth);
        }

        BLEManager.init(getActivity().getApplication());
        mBleDeviceList = new ArrayList<>();

        mAdapter = new RecyclerView.Adapter<BLEViewHolder>() {

            @NonNull
            @Override
            public BLEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_ble_device, parent, false);
                return new BLEViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull BLEViewHolder holder, int position) {
                final BleDevice bleDevice = mBleDeviceList.get(position);
                if (!TextUtils.isEmpty(bleDevice.getName())) {
                    holder.tvBleName.setText(bleDevice.getName());
                } else {
                    holder.tvBleName.setText(bleDevice.getMac());
                }
                holder.tvBleName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        connect(bleDevice.getMac());
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mBleDeviceList.size();
            }
        };
        mBluetoothRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mBluetoothRV.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bluetooth_switch:
                if (BLEManager.isBlueEnable()) {
                    BLEManager.disableBluetooth();
                    mBluetoothSwitchBtn.setText(R.string.enable_bluetooth);
                } else {
                    BLEManager.enableBluetooth();
                    mBluetoothSwitchBtn.setText(R.string.disable_bluetooth);
                }
                break;
            case R.id.scan_bluetooth:
                BLEManager.initCommonConfigure();
                setScanRule();
                requestPermissions(new OnPermissionsListener() {
                    @Override
                    public void OnPermissions(boolean granted) {
                        scan();
                    }
                }, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(60000)// 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void scan() {
        BLEManager.scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                LogUtils.i(TAG, "onScanFinished " + scanResultList.size());
            }

            @Override
            public void onScanStarted(boolean success) {
                LogUtils.i(TAG, "onScanStarted " + success);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                LogUtils.i(TAG, "onScanning " + bleDevice.getName() + " " + bleDevice.getMac());
                if (!TextUtils.isEmpty(bleDevice.getName())) {
                    mBleDeviceList.add(bleDevice);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void connect(String mac) {
        BLEManager.connect(mac, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                LogUtils.i(TAG, "onStartConnect");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                LogUtils.i(TAG, "onConnectFail");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                LogUtils.i(TAG, "onConnectSuccess");
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                LogUtils.i(TAG, "onDisConnected");
            }
        });
    }

    private static final class BLEViewHolder extends RecyclerView.ViewHolder {
        TextView tvBleName;

        BLEViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBleName = itemView.findViewById(R.id.ble_name);
        }
    }
}
