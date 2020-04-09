package com.tencentcs.iotvideodemo.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.UrlHelper;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.utils.StatusBarUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    private RxPermissions mRxPermissions;

    private Disposable mPermissionDisposable;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(getClass().getSimpleName(), "OnCreate");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        if (!UrlHelper.getInstance().isRelease()) {
            setTitle(getTitle());
        }
//        //设置状态栏颜色
//        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i(getClass().getSimpleName(), "onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!UrlHelper.getInstance().isRelease()) {
            title = title + "(测试环境)";
        }
        super.setTitle(title);
    }

    /**
     * 检查单个权限是否拥有
     *
     * @param permission
     * @return
     */
    protected int checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    /**
     * 多个权限申请结果回调
     *
     * @param permissionResult 权限申请结果
     * @param applyResult      方法执行结果 成功申请为true  任意一个权限shouldShowRequestPermissionRationale=false则返回 false
     * @param mark             申请权限时传递的标记
     */
    protected void applyForPermissionResult(int mark, Map<String, Boolean> permissionResult, boolean applyResult) {

    }

    /**
     * 检查是否拥有存储写入权限(存储读取权限不需要申请)
     */
    protected void applyForStoragePerMission() {
        final String[] storage = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        final Map<String, Boolean> storageResult = new HashMap<>();
        if (checkPermission(storage[0]) == PackageManager.PERMISSION_GRANTED
                &&checkPermission(storage[1])==PackageManager.PERMISSION_GRANTED) {
            storageResult.put(storage[0], true);
            storageResult.put(storage[1], true);
            applyForPermissionResult(-1,storageResult,true);
        }else {
            new RxPermissions(this).request(storage)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                storageResult.put(storage[0], true);
                                storageResult.put(storage[1], true);
                                applyForPermissionResult(-1, storageResult, true);
                            }else {
                                storageResult.put(storage[0], false);
                                storageResult.put(storage[1], false);
                                applyForPermissionResult(-1, storageResult, false);
                            }
                        }
                    });
        }
    }

    protected void requestPermissions(final OnPermissionsListener listener, String... permissions) {
        if (isPermissionsGranted(permissions)) {
            if (listener != null) {
                listener.OnPermissions(true);
                return;
            }
        }
        if (mRxPermissions == null) {
            mRxPermissions = new RxPermissions(this);
        }
        if (mPermissionDisposable != null && !mPermissionDisposable.isDisposed()) {
            mPermissionDisposable.isDisposed();
            LogUtils.e(TAG, "stop last permissions request");
        }
        mPermissionDisposable = mRxPermissions
                .request(permissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (listener != null) {
                            listener.OnPermissions(granted);
                        }
                    }
                });
    }

    protected void requestPermissions(final OnPermissionsListener listener, int tipStringResId, String... permissions) {
        if (isPermissionsGranted(permissions)) {
            if (listener != null) {
                listener.OnPermissions(true);
                return;
            }
        }
        if (shouldShowRequestPermissionRationale(permissions)) {
            showPermissionTipDialog(listener, tipStringResId, permissions);
        } else {
            requestPermissions(listener, permissions);
        }
    }

    protected boolean isPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    protected boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void showPermissionTipDialog(final OnPermissionsListener listener, int tipStringResId, final String... permissions) {
        new AlertDialog.Builder(this)
                .setMessage(getString(tipStringResId))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(listener, permissions);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    protected interface OnPermissionsListener {
        void OnPermissions(boolean granted);
    }

    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        return true;
                    }
                    return false;
                }
            });
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
