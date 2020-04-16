package com.tencentcs.iotvideodemo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.tencentcs.iotvideo.IoTVideoSdk
import com.tencentcs.iotvideo.accountmgr.AccountMgr
import com.tencentcs.iotvideo.netconfig.NetConfigInfo
import com.tencentcs.iotvideo.utils.LogUtils
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceListFragment
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceModelManager
import com.tencentcs.iotvideodemo.accountmgr.login.LoginActivity
import com.tencentcs.iotvideodemo.base.BaseActivity
import com.tencentcs.iotvideodemo.messagemgr.MessageBox
import com.tencentcs.iotvideodemo.messagemgr.MessageBoxActivity
import com.tencentcs.iotvideodemo.netconfig.NetConfigActivity
import com.tencentcs.iotvideodemo.netconfig.PrepareNetConfigActivity
import com.tencentcs.iotvideodemo.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_pager.*
import kotlinx.android.synthetic.main.layout_navigation_head.view.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private var mNavigationHead: View? = null

    private var mFirstTimeClickBack: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fl_main, DeviceListFragment()).commit()

        mNavigationHead = nav_view.getHeaderView(0)
        mNavigationHead!!.tv_app_version.text = "${BuildConfig.VERSION_NAME}(build ${BuildConfig.VERSION_CODE})"
        mNavigationHead!!.logout.setOnClickListener(this)
        mNavigationHead!!.iv_token.setOnClickListener(this)

        mNavigationHead!!.user_id.text = IoTVideoSdk.getTerminalId().toString()
        mNavigationHead!!.tv_access_id.text = AccountMgr.getAccessId()
        mNavigationHead!!.tv_access_token.text = AccountMgr.getAccessToken()
        mNavigationHead!!.iv_secret_id.text = AccountMgr.getSecretId()
        mNavigationHead!!.iv_secret_key.text = AccountMgr.getSecretKey()
        mNavigationHead!!.iv_token.text = AccountMgr.getToken()
        registerNotify()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.logout -> {
                logout()
            }
            R.id.iv_token -> {
                Utils.setClipboard(this, mNavigationHead!!.iv_token.text.toString())
                Snackbar.make(view, R.string.copy_to_clipboard, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun startNetMatchActivity() {
        val netConfigInfo = NetConfigInfo("", "", 2.toByte())
        val intent = Intent(this, NetConfigActivity::class.java)
        intent.putExtra("NetConfigInfo", netConfigInfo)
        startActivity(intent)
    }

    private fun startMessgeBoxActivity() {
        val intent = Intent(this, MessageBoxActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        AccountMgr.getHttpService().logout(object : SubscriberListener {
            override fun onStart() {
                showProgress(true)
                drawer_layout.closeDrawer(GravityCompat.START)
            }

            override fun onSuccess(response: JsonObject) {
                showProgress(false)
                AccountSPUtils.getInstance().clear(this@MainActivity)
                IoTVideoSdk.getMessageMgr().removeModelListener(DeviceModelManager.getInstance())
                IoTVideoSdk.unregister()
                MessageBox.eventMessageList.clear()
                MessageBox.modelMessageList.clear()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }

            override fun onFail(e: Throwable) {
                showProgress(false)
                AccountSPUtils.getInstance().clear(this@MainActivity)
                IoTVideoSdk.getMessageMgr().removeModelListener(DeviceModelManager.getInstance())
                IoTVideoSdk.unregister()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        })
    }

    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        progress_logout.visibility = if (show) View.VISIBLE else View.GONE
        progress_logout.animate().setDuration(shortAnimTime.toLong()).alpha((if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                progress_logout.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - mFirstTimeClickBack < 2000) {
                finish()
            } else {
                mFirstTimeClickBack = currentTime
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == android.R.id.home -> {
                if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.openDrawer(GravityCompat.START)
                } else {
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
                return true
            }
            item.itemId == R.id.action_menu_add -> {
                startNetMatchActivity()
                return true
            }
            item.itemId == R.id.action_menu_message -> {
                startMessgeBoxActivity()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun registerNotify() {
        IoTVideoSdk.getMessageMgr().addEventListener { data ->
            Toast.makeText(applicationContext, "onEventChanged ${data.topic} : ${data.data}", Toast.LENGTH_SHORT).show()
            MessageBox.eventMessageList.add(data)
            LogUtils.i(TAG, "onEventChanged ${data.topic} : ${data.data}")
        }

        IoTVideoSdk.getMessageMgr().addModelListener { data ->
            LogUtils.i(TAG, "onModeChanged deviceId:${data.device}, path:${data.path}, data:${data.data}")
            MessageBox.modelMessageList.add(data)
            Toast.makeText(applicationContext, "onModeChanged deviceId:${data.device}, path:${data.path}, data:${data.data}", Toast.LENGTH_SHORT).show()
        }

        IoTVideoSdk.getMessageMgr().addAppLinkListener {
            when (it) {
                IoTVideoSdk.APP_LINK_ONLINE -> Toast.makeText(applicationContext, "App已上线", Toast.LENGTH_SHORT).show()
                IoTVideoSdk.APP_LINK_OFFLINE -> Toast.makeText(applicationContext, "App已离线", Toast.LENGTH_SHORT).show()
                IoTVideoSdk.APP_LINK_ACCESS_TOKEN_ERROR -> Toast.makeText(applicationContext, "Access Token Error", Toast.LENGTH_SHORT).show()
                IoTVideoSdk.APP_LINK_TID_INIT_ERROR -> Toast.makeText(applicationContext, "TID初始化失败", Toast.LENGTH_SHORT).show()
                IoTVideoSdk.APP_LINK_INVALID_TID -> Toast.makeText(applicationContext, "TID无效", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
