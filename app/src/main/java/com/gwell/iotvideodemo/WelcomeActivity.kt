package com.gwell.iotvideodemo

import android.content.Intent
import android.os.Bundle

import com.gwell.iotvideodemo.accountmgr.AccountSPUtils
import com.gwell.iotvideodemo.accountmgr.login.LoginActivity
import com.gwell.iotvideodemo.base.BaseActivity

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        if (AccountSPUtils.getInstance().isLogin(this)) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

    override fun onBackPressed() {

    }
}
