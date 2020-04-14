package com.tencentcs.iotvideodemo.base;

import android.content.pm.PackageManager;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencentcs.iotvideo.utils.LogUtils;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    private RxPermissions mRxPermissions;

    private Disposable mPermissionDisposable;

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

    protected boolean isPermissionsGranted(String[] permissions) {
        if (getActivity() == null) {
            return false;
        }
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    protected boolean shouldShowRequestPermissionRationale(String[] permissions) {
        if (getActivity() == null) {
            return false;
        }
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                return false;
            }
        }
        return true;
    }

    protected interface OnPermissionsListener {
        void OnPermissions(boolean granted);
    }
}
