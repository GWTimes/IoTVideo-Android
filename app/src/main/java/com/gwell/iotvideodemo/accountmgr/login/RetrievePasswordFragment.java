package com.gwell.iotvideodemo.accountmgr.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseFragment;

import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_SUCCESS;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_SUCCESS;

public class RetrievePasswordFragment extends BaseFragment implements View.OnClickListener {
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView, mVCodeView;
    private LoginViewModel mLoginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_retrieve_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserNameView = view.findViewById(R.id.tv_user_name);
        mPasswordView = view.findViewById(R.id.tv_password);
        mVCodeView = view.findViewById(R.id.tv_vcode);
        view.findViewById(R.id.btn_get_vcode).setOnClickListener(this);
        view.findViewById(R.id.btn_retrieve).setOnClickListener(this);

        mLoginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);
        mLoginViewModel.getLoginState().observe(getActivity(), new Observer<LoginViewModel.LoginState>() {
            @Override
            public void onChanged(LoginViewModel.LoginState loginState) {
                if (mLoginViewModel.getOperateType() == LoginViewModel.OPERATE_RETRIEVE_PASSWORD) {
                    switch (loginState.state) {
                        case STATE_VCODE_SUCCESS:
                            Snackbar.make(mUserNameView, R.string.input_vcode, Snackbar.LENGTH_LONG).show();
                            break;
                        case STATE_SUCCESS:
                            Snackbar.make(mUserNameView, loginState.json, Snackbar.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_vcode:
                if (getActivity() instanceof LoginActivity) {
                    ((LoginActivity) getActivity()).hideSoftKeyboard();
                }
                getVCodeClicked();
                break;
            case R.id.btn_retrieve:
                if (getActivity() instanceof LoginActivity) {
                    ((LoginActivity) getActivity()).hideSoftKeyboard();
                }
                retrieveClicked();
                break;
        }
    }

    private void getVCodeClicked() {
        String account = mUserNameView.getText().toString();
        mLoginViewModel.checkCode(account);
    }

    private void retrieveClicked() {
        String account = mUserNameView.getText().toString();
        String pwd = mPasswordView.getText().toString();
        String vcode = mVCodeView.getText().toString();
        mLoginViewModel.retrieve(account, pwd, vcode);
    }
}
