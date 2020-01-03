package com.gwell.iotvideodemo.accountmgr.deviceshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.gwell.http.SubscriberListener;
import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideo.utils.LogUtils;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceList;
import com.gwell.iotvideodemo.base.BaseActivity;
import com.gwell.iotvideodemo.netconfig.FragmentAdapter;
import com.gwell.iotvideodemo.widget.ItemTouchHelperCallback;
import com.gwell.iotvideodemo.widget.OnMoveAndSwipedListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class DeviceShareActivity extends BaseActivity {
    private static final String TAG = "DeviceShareActivity";

    private RecyclerView mRVShareList;
    private MyAdapter mAdapter;
    private List<String> mShareList;
    private DeviceList.Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mDevice = (DeviceList.Device) getIntent().getSerializableExtra("Device");
            LogUtils.i(TAG, "mDevice = " + mDevice.toString());
            DeviceViewModel deviceViewModel = ViewModelProviders.of(this, new DeviceViewModelFactory()).get(DeviceViewModel.class);
            deviceViewModel.updateDevice(mDevice);
        }
        setContentView(R.layout.activity_device_share);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mShareList = new ArrayList<>();
        mRVShareList = findViewById(R.id.share_list);
        mAdapter = new MyAdapter();
        mRVShareList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRVShareList.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRVShareList);
        initViewPager();

        listSharedUsers();
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new AccountShareFragment());
        fragments.add(new QRCodeShareFragment());
        viewPager.setOffscreenPageLimit(fragments.size() - 1);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.account_share));
        titles.add(getString(R.string.qrcode_share));

        tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private static class ItemHolder extends RecyclerView.ViewHolder {
        TextView accountName;

        ItemHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.share_item_name);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<ItemHolder> implements OnMoveAndSwipedListener {
        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(DeviceShareActivity.this).inflate(R.layout.item_share, parent, false);
            ItemHolder holder = new ItemHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            holder.accountName.setText(mShareList.get(position));
        }

        @Override
        public int getItemCount() {
            return mShareList.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            return false;
        }

        @Override
        public void onItemDismiss(int position) {

        }
    }

    private void listSharedUsers() {
        AccountMgr.getInstance().listSharedUsers(mDevice.getDevId(), new SubscriberListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(JsonObject response) {
                LogUtils.i(TAG, "listSharedUsers " + response.toString());
            }

            @Override
            public void onFail(Throwable e) {
                Snackbar.make(mRVShareList, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
