package com.gwell.iotvideodemo

import android.content.Intent
import android.os.Bundle
import com.gwell.iotvideo.utils.UrlHelper

import com.gwell.iotvideodemo.accountmgr.AccountSPUtils
import com.gwell.iotvideodemo.accountmgr.login.LoginActivity
import com.gwell.iotvideodemo.base.BaseActivity
import com.gwell.iotvideodemo.utils.AppSPUtils

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val needChangeService = AppSPUtils.getInstance().getBoolean(this, AppSPUtils.NEED_SWITCH_SERVER_TYPE, false)
        when {
            needChangeService -> {
                AppSPUtils.getInstance().putBoolean(this, AppSPUtils.NEED_SWITCH_SERVER_TYPE, false)
                AccountSPUtils.getInstance().putInteger(this, AccountSPUtils.VALIDITY_TIMESTAMP, 0)
                startActivity(Intent(this, LoginActivity::class.java))
            }
            AccountSPUtils.getInstance().isLogin(this) -> startActivity(Intent(this, MainActivity::class.java))
            else -> startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

    override fun onBackPressed() {

    }
}
