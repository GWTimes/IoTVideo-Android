package com.tencentcs.iotvideodemo.accountmgr.deviceshare;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceList;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.netconfig.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class DeviceShareActivity extends BaseActivity {
    private static final String TAG = "DeviceShareActivity";

    private DeviceList.Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mDevice = (DeviceList.Device) getIntent().getSerializableExtra("Device");
            LogUtils.i(TAG, "mDevice = " + mDevice.toString());
            DeviceShareViewModel deviceShareViewModel = ViewModelProviders.of(this, new DeviceViewModelFactory()).get(DeviceShareViewModel.class);
            deviceShareViewModel.updateDevice(mDevice);
        }
        setContentView(R.layout.activity_device_share);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initViewPager();
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DeviceShareListFragment());
        fragments.add(new AccountShareFragment());
        fragments.add(new QRCodeShareFragment());
        viewPager.setOffscreenPageLimit(fragments.size() - 1);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.share_list));
        titles.add(getString(R.string.account_share));
        titles.add(getString(R.string.qrcode_share));

        tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(2)));

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
