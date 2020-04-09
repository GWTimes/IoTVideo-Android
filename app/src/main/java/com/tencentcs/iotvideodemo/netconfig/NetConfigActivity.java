package com.tencentcs.iotvideodemo.netconfig;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.tencentcs.iotvideo.netconfig.NetConfig;
import com.tencentcs.iotvideo.netconfig.NetConfigInfo;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.netconfig.ap.APNetConfigFragment;
import com.tencentcs.iotvideodemo.netconfig.qrcode.QRCodeNetConfigFragment;
import com.tencentcs.iotvideodemo.netconfig.wired.WiredNetConfigFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class NetConfigActivity extends BaseActivity {
    private static final String TAG = "NetConfigActivity";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private NetConfigInfo mNetConfigInfo;
    private NetConfigViewModel mNetConfigInfoViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_config);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_action_more));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        initViewPager();
        mNetConfigInfo = (NetConfigInfo) getIntent().getSerializableExtra("NetConfigInfo");
        mNetConfigInfoViewModel = ViewModelProviders.of(this, new NetConfigViewModelFactory())
                .get(NetConfigViewModel.class);
        mNetConfigInfoViewModel.updateNetConfigInfo(mNetConfigInfo);
        mNetConfigInfoViewModel.getBindStateData().observe(this, new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState httpRequestState) {
                switch (httpRequestState.getStatus()) {
                    case START:
                        showProgressDialog();
                        break;
                    case SUCCESS:
                        dismissProgressDialog();
                        Snackbar.make(mViewPager, httpRequestState.getStatusTip(), Snackbar.LENGTH_LONG).show();
                        break;
                    case ERROR:
                        dismissProgressDialog();
                        Snackbar.make(mViewPager, httpRequestState.getStatusTip(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
        mNetConfigInfoViewModel.getNetConfigStateData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mViewPager, s, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void initViewPager() {
        mTabLayout = findViewById(R.id.tab_layout_main);
        mViewPager = findViewById(R.id.view_pager_main);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.wired_net_config));
//        titles.add(getString(R.string.qrcode_net_config));
//        titles.add(getString(R.string.ap_net_config));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
//        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
//        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new WiredNetConfigFragment());
//        fragments.add(new QRCodeNetConfigFragment());
//        fragments.add(new APNetConfigFragment());

        mViewPager.setOffscreenPageLimit(0);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.net_config_menu, menu);
//        return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_wired:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.action_menu_qrcode:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.action_menu_ap:
                mViewPager.setCurrentItem(2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
