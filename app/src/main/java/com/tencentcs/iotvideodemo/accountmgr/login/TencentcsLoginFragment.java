package com.tencentcs.iotvideodemo.accountmgr.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.BuildConfig;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.utils.Utils;

public class TencentcsLoginFragment extends BaseFragment {
    private final static String TAG = "TencentcsLoginFragment";
    private EditText mEtSecretId, mEtSecretKey, mEtToken, mEtUserName;
    private TextView mTvVersion;
    private LoginViewModel mLoginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tencentcs_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEtSecretId = view.findViewById(R.id.et_secret_id);
        mEtSecretKey = view.findViewById(R.id.et_secret_key);
        mEtToken = view.findViewById(R.id.et_token);
        mEtUserName = view.findViewById(R.id.et_user_name);
        mTvVersion = view.findViewById(R.id.tv_app_version);
        mTvVersion.setText(BuildConfig.VERSION_NAME);
        view.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
        view.findViewById(R.id.btn_anonymous_login).setOnClickListener(v -> {
            anonymousLoginClicked();
        });
        if (!TextUtils.isEmpty(((LoginActivity) getActivity()).getDefaultSecretId())) {
            mEtSecretId.setText(((LoginActivity) getActivity()).getDefaultSecretId());
        }
        if (!TextUtils.isEmpty(((LoginActivity) getActivity()).getDefaultSecretKey())) {
            mEtSecretKey.setText(((LoginActivity) getActivity()).getDefaultSecretKey());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);
        Observer<HttpRequestState> observer = new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState httpRequestState) {
                if (null == getActivity()) {
                    LogUtils.i(TAG,"activity is destroyed");
                    return;
                }
                switch (httpRequestState.getStatus()) {
                    case SUCCESS:
                        mLoginViewModel.login(Utils.getPhoneUuid(getActivity()));
                        break;
                    case ERROR:
                        LogUtils.i(TAG,"login error:" + httpRequestState.getStatusTip());
                        Snackbar.make(mEtSecretId, httpRequestState.getStatusTip(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        };
        mLoginViewModel.getRegisterState().observe(getActivity(), observer);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoginViewModel.getOperateData().setValue(LoginViewModel.OperateType.Login);
    }

    private void loginClicked() {
        String secretId = "";
        String secretKey = "";
        String token = "";
        String userName = "";
        secretId = mEtSecretId.getText().toString();
        secretKey = mEtSecretKey.getText().toString();
        token = mEtToken.getText().toString();
        userName = mEtUserName.getText().toString();
        if (!TextUtils.isEmpty(secretId) && !TextUtils.isEmpty(secretKey) && !TextUtils.isEmpty(userName)) {
            AccountMgr.init("", "", "");
            AccountMgr.setSecretInfo(secretId, secretKey, token);
            AccountSPUtils.getInstance().putString(getActivity(), AccountSPUtils.SECRET_ID, secretId);
            AccountSPUtils.getInstance().putString(getActivity(), AccountSPUtils.SECRET_KEY, secretKey);
            AccountSPUtils.getInstance().putString(getActivity(), AccountSPUtils.TOKEN, token);
            mLoginViewModel.register(userName);
        } else {
            Snackbar.make(mEtSecretId, "SecretId、SecretKey和用户名都不能为空", Snackbar.LENGTH_LONG).show();
        }
    }

    private void anonymousLoginClicked() {
        Intent intent = new Intent(getActivity(), AnonymousLoginActivity.class);
        startActivity(intent);
        if (null != getActivity()) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
