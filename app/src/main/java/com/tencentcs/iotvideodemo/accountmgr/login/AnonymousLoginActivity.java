package com.tencentcs.iotvideodemo.accountmgr.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideodemo.BuildConfig;
import com.tencentcs.iotvideodemo.MainActivity;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.base.HttpRequestState;

public class AnonymousLoginActivity extends BaseActivity {

    private EditText mEtSecretId, mEtSecretKey, mEtTid;
    private TextView mTvVersion;

    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_login);
        initView();
    }

    private void initView() {
        mEtSecretId = findViewById(R.id.et_secret_id);
        mEtSecretKey = findViewById(R.id.et_secret_key);
        mEtTid = findViewById(R.id.et_tid);
        mTvVersion = findViewById(R.id.tv_app_version);
        mTvVersion.setText(BuildConfig.VERSION_NAME);

//        mEtSecretId.setText("AKIDJI7i39Df3CG5qM9jY7SiksuocFeov3HF");
//        mEtSecretKey.setText("ylJbecMp2zh8PRO5VllVyA7TbUAdrqaC");
//        mEtTid.setText("031400005f3a3e3dfc1d6a9fa0c990f7");

          mEtSecretId.setText("AKIDNULxMXvVH2axvbwB6ltxl5IEivsf6vFR");
          mEtSecretKey.setText("bAZynRwIerwmOkriLjroYVNDqeIhqjMd");
          mEtTid.setText("031400005f3c94224c917095e75c48cd");

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anonymousLoginClicked();
            }
        });

        mLoginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(this)).get(LoginViewModel.class);
        mLoginViewModel.getAnonymousLoginState().observe(this, new Observer<HttpRequestState>() {
            @Override
            public void onChanged(final HttpRequestState loginState) {
                switch (loginState.getStatus()) {
                    case START:
                        showProgressDialog();
                        break;
                    case SUCCESS:
                        dismissProgressDialog();
                        AccountSPUtils.getInstance().putBoolean(AnonymousLoginActivity.this, AccountSPUtils.ANONYMOUS_USER, true);
                        Intent intent = new Intent(AnonymousLoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case ERROR:
                        dismissProgressDialog();
                        Snackbar.make(mEtSecretId, loginState.getStatusTip(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void anonymousLoginClicked() {
        String secretId;
        String secretKey;
        String tId;
        secretId = mEtSecretId.getText().toString();
        secretKey = mEtSecretKey.getText().toString();
        tId = mEtTid.getText().toString();
        if (TextUtils.isEmpty(secretId) || TextUtils.isEmpty(secretKey) || TextUtils.isEmpty(tId)) {
            Snackbar.make(mEtSecretId, "SecretId、SecretKey和Tid都不能为空", Snackbar.LENGTH_LONG).show();
            return;
        }
        AccountMgr.init("", "", "");
        AccountMgr.setSecretInfo(secretId, secretKey, "");
        AccountSPUtils.getInstance().putString(this, AccountSPUtils.SECRET_ID, secretId);
        AccountSPUtils.getInstance().putString(this, AccountSPUtils.SECRET_KEY, secretKey);
        AccountSPUtils.getInstance().putString(this, AccountSPUtils.ANONYMOUS_TID, tId);
        mLoginViewModel.loginAnonymous(1440, tId, "");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
