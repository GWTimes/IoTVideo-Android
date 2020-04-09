package com.tencentcs.iotvideodemo.videoplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.utils.StorageManager;
import com.tencentcs.iotvideodemo.videoplayer.ijkplayer.IjkPlayerActivity;
import com.tencentcs.iotvideodemo.widget.BigImageDialog;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;
import com.tencentcs.iotvideodemo.widget.SimpleRecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class LocalAlbumActivity extends BaseActivity implements SimpleRecyclerViewAdapter.OnItemClickListener {
    private static final String TAG = "LocalAlbumActivity";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRVVideoList;
    private SimpleRecyclerViewAdapter<String> mAdapter;
    private BigImageDialog mBigImageDialog;
    private List<String> mVideoList;
    private String mDeviceId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_album);
        mRVVideoList = findViewById(R.id.album);
        mRVVideoList.addItemDecoration(new RecycleViewDivider(this, RecycleViewDivider.VERTICAL));
        mRVVideoList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mVideoList = new ArrayList<>();
        mAdapter = new SimpleRecyclerViewAdapter<>(this, mVideoList);
        mAdapter.setItemVerticalPadding(40);
        mRVVideoList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listAlbum();
            }
        });

        if (getIntent() != null) {
            String devId = getIntent().getStringExtra("deviceID");
            if (!TextUtils.isEmpty(devId)) {
                mDeviceId = devId;
                LogUtils.i(TAG, "mDeviceId = " + mDeviceId);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAlbum();
    }

    private void listAlbum() {
        mVideoList.clear();
        File recordFile = new File(StorageManager.getVideoPath() + File.separator + mDeviceId);
        if (recordFile.exists()) {
            String[] fileNameArray = recordFile.list();
            if (fileNameArray != null) {
                mVideoList.addAll(Arrays.asList(fileNameArray));
            }
        }
        File pictureFile = new File(StorageManager.getPicPath() + File.separator + mDeviceId);
        if (pictureFile.exists()) {
            String[] fileNameArray = pictureFile.list();
            if (fileNameArray != null) {
                mVideoList.addAll(Arrays.asList(fileNameArray));
            }
        }
        if (mVideoList.isEmpty()) {
            Snackbar.make(mRVVideoList, "no files", Snackbar.LENGTH_SHORT).show();
        } else {
            Collections.sort(mVideoList);
            mAdapter.notifyDataSetChanged();
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        if (mVideoList.get(position).endsWith(".mp4")) {
            File recordFile = new File(StorageManager.getVideoPath() + File.separator + mDeviceId
                    + File.separator + mVideoList.get(position));
            if (position % 2 == 0) {
                startExoPlayActivity(recordFile.getAbsolutePath());
            } else {
                startIjkPlayActivity(recordFile.getAbsolutePath());
            }
        } else if (mVideoList.get(position).endsWith(".jpeg")) {
            File snapFile = new File(StorageManager.getPicPath() + File.separator + mDeviceId
                    + File.separator + mVideoList.get(position));
            showBigPictureDialog(Uri.fromFile(snapFile));
        }
    }

    private void startExoPlayActivity(String url) {
        Intent intent = new Intent(this, ExoPlayerActivity.class);
        intent.putExtra("URI", url);
        startActivity(intent);
    }

    private void startIjkPlayActivity(String url) {
        Intent intent = new Intent(this, IjkPlayerActivity.class);
        intent.putExtra("URI", url);
        startActivity(intent);
    }

    private void showBigPictureDialog(Uri uri) {
        if (mBigImageDialog == null) {
            mBigImageDialog = new BigImageDialog(this);
        }
        if (!mBigImageDialog.isShowing()) {
            mBigImageDialog.show();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mBigImageDialog.loadBitmap(bitmap);
        } else {
            mBigImageDialog.dismiss();
        }
    }
}
