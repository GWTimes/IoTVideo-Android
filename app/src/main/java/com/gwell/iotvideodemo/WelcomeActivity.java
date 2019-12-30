package com.gwell.iotvideodemo;

import android.content.Intent;
import android.os.Bundle;

import com.gwell.iotvideo.accountmgr.AccountMgr;
import com.gwell.iotvideodemo.accountmgr.login.LoginActivity;
import com.gwell.iotvideodemo.base.BaseActivity;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        boolean isLogin = AccountMgr.getInstance().isLogin(this);
        if (!isLogin) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
