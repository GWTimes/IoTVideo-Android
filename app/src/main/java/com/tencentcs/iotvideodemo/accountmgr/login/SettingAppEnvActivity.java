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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.UrlHelper;
import com.tencentcs.iotvideodemo.MainActivity;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.base.HttpRequestState;
import com.tencentcs.iotvideodemo.utils.AppSPUtils;
import com.tencentcs.iotvideodemo.utils.Utils;

import java.util.Stack;

public class SettingAppEnvActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";


    private Switch mStSettingAppEnv;
    private TextView mTvHint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_env_setting);
        initView();
    }

    public void initView() {
        mStSettingAppEnv = findViewById(R.id.st_switch_app_env);
        mTvHint = findViewById(R.id.tv_hint);

        if (UrlHelper.SERVER_DEV == AppSPUtils.getInstance().getInteger(this, AppSPUtils.SERVER_TYPE, UrlHelper.SERVER_RELEASE)) {
            mStSettingAppEnv.setChecked(true);
        }else{
            mStSettingAppEnv.setChecked(false);
        }
        mStSettingAppEnv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AppSPUtils.getInstance().putInteger(getBaseContext(), AppSPUtils.SERVER_TYPE, UrlHelper.SERVER_DEV);
                    Snackbar.make(mTvHint, "已经切换到测试环境，请杀死应用并重启应用", Snackbar.LENGTH_LONG).show();
                }else{
                    AppSPUtils.getInstance().putInteger(getBaseContext(), AppSPUtils.SERVER_TYPE, UrlHelper.SERVER_RELEASE);
                    Snackbar.make(mTvHint, "已经切换到正式环境，请杀死应用并重启应用", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backActivity();
    }

    private void backActivity() {
        finish();
    }
}