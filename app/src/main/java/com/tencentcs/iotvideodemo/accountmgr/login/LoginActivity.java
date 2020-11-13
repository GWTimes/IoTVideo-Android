package com.tencentcs.iotvideodemo.accountmgr.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideodemo.BuildConfig;
import com.tencentcs.iotvideodemo.MainActivity;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.utils.Utils;

import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private View mProgressView;
    private FragmentManager mFragmentManager;
    private LoginFragment mLoginFragment;
    private InputAccountFragment mInputAccountFragment;
    private InputPasswordFragment mInputPasswordFragment;
    private TencentcsLoginFragment mTencentcsLoginFragment;
    private Stack<Fragment> mFragmentStack;
    private Fragment mCurrentFragment;
    private LoginViewModel mLoginViewModel;
    private String defaultSecretId = null;
    private String defaultSecretKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFragmentStack = new Stack<>();
        if (null != getIntent()) {
            defaultSecretId = getIntent().getStringExtra(AccountSPUtils.SECRET_ID);
            defaultSecretKey = getIntent().getStringExtra(AccountSPUtils.SECRET_KEY);
        }
        initView();
    }

    public void initView() {
        mProgressView = findViewById(R.id.progress_login);
        mFragmentManager = getSupportFragmentManager();
        mLoginFragment = new LoginFragment();
        mInputAccountFragment = new InputAccountFragment();
        mInputPasswordFragment = new InputPasswordFragment();
        mTencentcsLoginFragment = new TencentcsLoginFragment();
        mLoginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(this)).get(LoginViewModel.class);
        mLoginViewModel.getFragmentData().observe(this, new Observer<LoginViewModel.Fragment>() {
            @Override
            public void onChanged(LoginViewModel.Fragment fragment) {
                LogUtils.i(TAG, "fragment = " + fragment);
                switch (fragment) {
                    case Login:
                        startFragment(mLoginFragment);
                        mFragmentStack.clear();
                        break;
                    case InputAccount:
                        startFragment(mInputAccountFragment);
                        break;
                    case InputPassword:
                        startFragment(mInputPasswordFragment);
                        break;
                    case TencentcsLogin:
                        startFragment(mTencentcsLoginFragment);
                        break;
                }
            }
        });
        LoginViewModel.Fragment defaultValue = Utils.isOemVersion() ?
                LoginViewModel.Fragment.Login : LoginViewModel.Fragment.TencentcsLogin;
        mLoginViewModel.getFragmentData().setValue(defaultValue);
        mLoginViewModel.getLoginState().observe(this, new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState loginState) {
                switch (loginState.getStatus()) {
                    case START:
                        showProgress(true);
                        break;
                    case SUCCESS:
                        showProgress(false);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case ERROR:
                        showProgress(false);
                        Snackbar.make(mProgressView, loginState.getStatusTip(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
        mLoginViewModel.getVCodeState().observe(this, new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState httpRequestState) {
                switch (httpRequestState.getStatus()) {
                    case START:
                        showProgress(true);
                        break;
                    case SUCCESS:
                        showProgress(false);
                        mLoginViewModel.getFragmentData().setValue(LoginViewModel.Fragment.InputPassword);
                        break;
                    case ERROR:
                        showProgress(false);
                        Snackbar.make(mProgressView, httpRequestState.getStatusTip(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
        mLoginViewModel.getOperateData().observe(this, new Observer<LoginViewModel.OperateType>() {
            @Override
            public void onChanged(LoginViewModel.OperateType operateType) {
                switch (operateType) {
                    case Login:
                    case Nothing:
                        setTitle(R.string.title_activity_login);
                        break;
                    case Register:
                        setTitle(R.string.register);
                        break;
                    case ResetPwd:
                        setTitle(R.string.title_activity_retrieve_password);
                        break;
                }
            }
        });
        Observer<HttpRequestState> registerObserver = new Observer<HttpRequestState>() {
            @Override
            public void onChanged(HttpRequestState httpRequestState) {
                switch (httpRequestState.getStatus()) {
                    case SUCCESS:
                        mLoginViewModel.getFragmentData().setValue(LoginViewModel.Fragment.Login);
                        break;
                    case ERROR:
                        Snackbar.make(mProgressView, httpRequestState.getStatusTip(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        };
        mLoginViewModel.getRegisterState().observe(this, registerObserver);
        mLoginViewModel.getResetPwdState().observe(this, registerObserver);
    }

    private void startFragment(Fragment fragment) {
        if (mCurrentFragment == fragment) {
            return;
        }
        if (mCurrentFragment != null) {
            mFragmentStack.push(mCurrentFragment);
            LogUtils.i(TAG, "fragment stack push " + mCurrentFragment.getClass().getSimpleName());
        }
        showFragment(fragment);
    }

    private void backFragment() {
        if (!mFragmentStack.isEmpty()) {
            Fragment fragment = mFragmentStack.pop();
            LogUtils.i(TAG, "fragment stack pop " + fragment.getClass().getSimpleName());
            showFragment(fragment);
        }
    }

    private void showFragment(Fragment fragment) {
        mCurrentFragment = fragment;
        LogUtils.i(TAG, "showFragment = " + mCurrentFragment.getClass().getSimpleName());
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
            backFragment();
            return true;
        } else if (item.getItemId() == R.id.action_menu_setting) {
            startSettingAppEnvActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backFragment();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_setting_menu, menu);
        return true;
    }

    private void startSettingAppEnvActivity() {
        Intent intent = new Intent(this,SettingAppEnvActivity.class);
        startActivity(intent);
    }

    public String getDefaultSecretId() {
        return defaultSecretId;
    }

    public String getDefaultSecretKey() {
        return defaultSecretKey;
    }
}