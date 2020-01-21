package com.gwell.iotvideodemo.accountmgr.login;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseFragment;
import com.gwell.iotvideodemo.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";

    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private LoginViewModel mLoginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserNameView = view.findViewById(R.id.tv_user_name);
        mPasswordView = view.findViewById(R.id.tv_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.btn_login || id == EditorInfo.IME_NULL) {
                    loginClicked();
                    return true;
                }
                return false;
            }
        });

        final Button login_button = view.findViewById(R.id.btn_login);
        login_button.setOnClickListener(this);
        Button forgot_password = view.findViewById(R.id.btn_forgot_password);
        forgot_password.setOnClickListener(this);
        Button register = view.findViewById(R.id.btn_forgot_register);
        register.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoginViewModel.getOperateData().setValue(LoginViewModel.OperateType.Login);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (getActivity() instanceof LoginActivity) {
                    ((LoginActivity) getActivity()).hideSoftKeyboard();
                }
                loginClicked();
                break;

            case R.id.btn_forgot_password:
                retrievePasswordClicked();
                break;

            case R.id.btn_forgot_register:
                registerClicked();
                break;
        }
    }

    private void loginClicked() {
        mLoginViewModel.getOperateData().setValue(LoginViewModel.OperateType.Login);
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        mLoginViewModel.login(userName, password, Utils.getPhoneUuid(getContext()));
    }

    private void registerClicked() {
        mLoginViewModel.getFragmentData().setValue(LoginViewModel.Fragment.InputAccount);
        mLoginViewModel.getOperateData().setValue(LoginViewModel.OperateType.Register);
    }

    private void retrievePasswordClicked() {
        mLoginViewModel.getFragmentData().setValue(LoginViewModel.Fragment.InputAccount);
        mLoginViewModel.getOperateData().setValue(LoginViewModel.OperateType.ResetPwd);
    }
}
