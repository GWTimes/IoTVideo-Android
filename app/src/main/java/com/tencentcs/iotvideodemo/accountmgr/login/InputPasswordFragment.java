package com.tencentcs.iotvideodemo.accountmgr.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class InputPasswordFragment extends BaseFragment implements View.OnClickListener {
    private AutoCompleteTextView mInputPassword;
    private AutoCompleteTextView mInputPasswordRepeat;
    private AutoCompleteTextView mInputVCode;
    private LoginViewModel mLoginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputPassword = view.findViewById(R.id.tv_password);
        mInputPasswordRepeat = view.findViewById(R.id.tv_password_repeat);
        mInputVCode = view.findViewById(R.id.tv_vcode);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        if (LoginManager.isEmailValid(mLoginViewModel.getCurrentAccount())) {
            mInputVCode.setVisibility(View.GONE);
        }
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
            String pwd = mInputPassword.getText().toString();
            String pwdRepeat = mInputPasswordRepeat.getText().toString();
            String vcode = mInputVCode.getText().toString();
            if (!TextUtils.equals(pwd, pwdRepeat)) {
                Snackbar.make(mInputPassword, R.string.compare_password, Snackbar.LENGTH_LONG).show();
                return;
            } else if (mInputVCode.getVisibility() == View.VISIBLE && TextUtils.isEmpty(vcode)) {
                Snackbar.make(mInputPassword, R.string.input_vcode, Snackbar.LENGTH_LONG).show();
                return;
            }
            LoginViewModel.OperateType operateType = mLoginViewModel.getOperateData().getValue();
            if (operateType == LoginViewModel.OperateType.Register) {
                mLoginViewModel.register(pwd, vcode);
            } else if (operateType == LoginViewModel.OperateType.ResetPwd) {
                mLoginViewModel.retrieve(pwd, vcode);
            }
        }
    }
}
