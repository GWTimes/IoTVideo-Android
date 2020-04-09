package com.tencentcs.iotvideodemo

import android.content.Intent
import android.os.Bundle

import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils
import com.tencentcs.iotvideodemo.accountmgr.login.LoginActivity
import com.tencentcs.iotvideodemo.base.BaseActivity
import com.tencentcs.iotvideodemo.utils.AppSPUtils

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        setContentView(R.layout.activity_welcome)
        val needChangeService = AppSPUtils.getInstance().getBoolean(this, AppSPUtils.NEED_SWITCH_SERVER_TYPE, false)
        if (needChangeService) {
            AppSPUtils.getInstance().putBoolean(this, AppSPUtils.NEED_SWITCH_SERVER_TYPE, false)
            AccountSPUtils.getInstance().putInteger(this, AccountSPUtils.VALIDITY_TIMESTAMP, 0)
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            if (AccountSPUtils.getInstance().isLogin(this)) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        finish()
    }

    override fun onBackPressed() {

    }
}
