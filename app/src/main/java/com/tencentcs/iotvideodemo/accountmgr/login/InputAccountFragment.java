package com.tencentcs.iotvideodemo.accountmgr.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.accountmgr.SafeCheckActivity;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.BuildConfig;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class InputAccountFragment extends BaseFragment implements View.OnClickListener {

    private static final int SAFE_CHECK_REQUEST_CODE = 1;

    private AutoCompleteTextView mUserNameView;
    private LoginViewModel mLoginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserNameView = view.findViewById(R.id.tv_user_name);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_confirm) {
            if (getActivity() instanceof LoginActivity) {
                ((LoginActivity) getActivity()).hideSoftKeyboard();
            }
            if (Utils.isOemVersion()) {
                startSafeCheckActivity();
            } else {
                getVCodeClicked();
            }
        }
    }

    private void getVCodeClicked() {
        String account = mUserNameView.getText().toString();
        if (TextUtils.isEmpty(account)) {
            Snackbar.make(mUserNameView, R.string.input_account, Snackbar.LENGTH_LONG).show();
            return;
        }
        LoginViewModel.OperateType operateType = mLoginViewModel.getOperateData().getValue();
        if (operateType == LoginViewModel.OperateType.Register) {
            mLoginViewModel.checkCode(account, 0);
        } else if (operateType == LoginViewModel.OperateType.ResetPwd) {
            mLoginViewModel.checkCode(account, 1);
        }
    }

    private void startSafeCheckActivity() {
        Intent intent = new Intent(getActivity(), SafeCheckActivity.class);
        startActivityForResult(intent, SAFE_CHECK_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SAFE_CHECK_REQUEST_CODE == requestCode && data != null) {
            final String ticket = data.getStringExtra("ticket");
            final String randstr = data.getStringExtra("randstr");
            LogUtils.i("InputAccountFragment", "onActivityResult ticket = " + ticket + ", randstr = " + randstr);
            SafeCheckCode safeCheckCode = new SafeCheckCode(ticket, randstr);
            mLoginViewModel.getSafeCheckCode().setValue(safeCheckCode);
            getVCodeClicked();
        }
    }
}
