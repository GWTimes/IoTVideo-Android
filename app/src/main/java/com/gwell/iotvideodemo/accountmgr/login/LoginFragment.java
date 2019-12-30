package com.gwell.iotvideodemo.accountmgr.login;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.gwell.iotvideodemo.MainActivity;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseFragment;

import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_SUCCESS;

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
        mLoginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);
        mLoginViewModel.getLoginState().observe(getActivity(), new Observer<LoginViewModel.LoginState>() {
            @Override
            public void onChanged(LoginViewModel.LoginState loginState) {
                if (mLoginViewModel.getOperateType() == LoginViewModel.OPERATE_LOGIN) {
                    if (loginState.state == STATE_SUCCESS) {
                        startMainActivity();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
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
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        mLoginViewModel.login(userName, password);
    }

    private void registerClicked() {
        mLoginViewModel.getOperator().setValue(LoginViewModel.OPERATE_REGISTER);
    }

    private void retrievePasswordClicked() {
        mLoginViewModel.getOperator().setValue(LoginViewModel.OPERATE_RETRIEVE_PASSWORD);
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
