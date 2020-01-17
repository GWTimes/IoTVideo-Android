package com.gwell.iotvideodemo.accountmgr.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.gwell.iotvideodemo.R;
import com.gwell.iotvideodemo.base.BaseActivity;

import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.OPERATE_LOGIN;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_ERROR;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_START;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_SUCCESS;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_ERROR;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_START;
import static com.gwell.iotvideodemo.accountmgr.login.LoginViewModel.STATE_VCODE_SUCCESS;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private View mProgressView;
    private FragmentManager mFragmentManager;
    private LoginFragment mLoginFragment;
    private RetrievePasswordFragment mRetrievePasswordFragment;
    private RegisterFragment mRegisterFragment;
    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView() {
        mProgressView = findViewById(R.id.progress_login);
        mFragmentManager = getSupportFragmentManager();
        mLoginFragment = new LoginFragment();
        mRegisterFragment = new RegisterFragment();
        mRetrievePasswordFragment = new RetrievePasswordFragment();
        showFragment(mLoginFragment);
        setTitle(R.string.title_activity_login);
        mLoginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(this)).get(LoginViewModel.class);
        mLoginViewModel.getOperator().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer operator) {
                if (operator == LoginViewModel.OPERATE_LOGIN) {
                    showFragment(mLoginFragment);
                    setTitle(R.string.title_activity_login);
                } else if (operator == LoginViewModel.OPERATE_REGISTER) {
                    showFragment(mRegisterFragment);
                    setTitle(R.string.title_activity_register);
                } else if (operator == LoginViewModel.OPERATE_RETRIEVE_PASSWORD) {
                    showFragment(mRetrievePasswordFragment);
                    setTitle(R.string.title_activity_retrieve_password);
                }
            }
        });
        mLoginViewModel.getLoginState().observe(this, new Observer<LoginViewModel.LoginState>() {
            @Override
            public void onChanged(LoginViewModel.LoginState loginState) {
                switch (loginState.state) {
                    case STATE_START:
                    case STATE_VCODE_START:
                        showProgress(true);
                        break;
                    case STATE_VCODE_SUCCESS:
                        showProgress(false);
                        break;
                    case STATE_SUCCESS:
                        showProgress(false);
                        if (mLoginViewModel.getOperateType() == OPERATE_LOGIN) {
                            finish();
                        }
                        break;
                    case STATE_ERROR:
                    case STATE_VCODE_ERROR:
                        showProgress(false);
                        Snackbar.make(mProgressView, loginState.e.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showFragment(mLoginFragment);
            setTitle(R.string.title_activity_login);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showFragment(mLoginFragment);
        setTitle(R.string.title_activity_login);
    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
