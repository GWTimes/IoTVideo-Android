package com.tencentcs.iotvideodemo

import android.Manifest
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
import com.tencentcs.iotvideo.utils.LogUtils
import com.tencentcs.iotvideo.utils.UrlHelper
import com.tencentcs.iotvideo.utils.qrcode.QRCodeHelper
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener
import com.tencentcs.iotvideodemo.accountmgr.AccountSPUtils
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceListFragment
import com.tencentcs.iotvideodemo.accountmgr.login.LoginActivity
import com.tencentcs.iotvideodemo.accountmgrtc.TencentcsLoginActivity
import com.tencentcs.iotvideodemo.accountmgrtc.httpservice.TencentcsAccountMgr
import com.tencentcs.iotvideodemo.accountmgrtc.httpservice.TencentcsAccountSPUtils
import com.tencentcs.iotvideodemo.base.BaseActivity
import com.tencentcs.iotvideodemo.base.BaseActivity.OnPermissionsListener
import com.tencentcs.iotvideodemo.netconfig.PrepareNetConfigActivity
import com.tencentcs.iotvideodemo.test.TestActivity
import com.tencentcs.iotvideodemo.utils.AppSPUtils
import com.tencentcs.iotvideodemo.utils.Utils
import com.tencentcs.iotvideodemo.videoplayer.CustomCaptureActivity
import com.tencentcs.zxing.CaptureActivity
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

        if (BuildConfig.DEBUG) {
            start_test_activity.visibility = View.VISIBLE
            start_test_activity.setOnClickListener(this)
        }
        mNavigationHead = nav_view.getHeaderView(0)
        mNavigationHead!!.tv_app_version.text = BuildConfig.VERSION_NAME
        mNavigationHead!!.logout.setOnClickListener(this)
        mNavigationHead!!.iv_token.setOnClickListener(this)
        mNavigationHead!!.switch_server.isChecked = UrlHelper.getInstance().serverType == UrlHelper.SERVER_DEV
        mNavigationHead!!.switch_server.setOnCheckedChangeListener { buttonView, isChecked ->
            AccountSPUtils.getInstance().putInteger(this@MainActivity, AccountSPUtils.VALIDITY_TIMESTAMP, 0)
            AppSPUtils.getInstance().putBoolean(this@MainActivity, AppSPUtils.NEED_SWITCH_SERVER_TYPE, true)
            AppSPUtils.getInstance().putInteger(this@MainActivity, AppSPUtils.SERVER_TYPE,
                    if (isChecked) UrlHelper.SERVER_DEV else UrlHelper.SERVER_RELEASE)
            Toast.makeText(applicationContext, R.string.effective_after_restarting, Toast.LENGTH_LONG).show()
        }
        if (MyApp.ENABLE_TENCENTCS) {
            mNavigationHead!!.user_id.text = IoTVideoSdk.getTerminalId().toString()
            mNavigationHead!!.iv_secret_id.text = TencentcsAccountMgr.getSecretId()
            mNavigationHead!!.iv_secret_key.text = TencentcsAccountMgr.getSecretKey()
            mNavigationHead!!.iv_token.text = TencentcsAccountMgr.getToken()
            mNavigationHead!!.tv_access_id.text = TencentcsAccountMgr.getAccessId()
            mNavigationHead!!.tv_access_token.text =
                    TencentcsAccountSPUtils.getInstance().getString(this, TencentcsAccountSPUtils.ACCESS_TOKEN, "")
        } else {
            val realToken = AccountSPUtils.getInstance().getString(this, AccountSPUtils.TOKEN, "")
            val secretKey = AccountSPUtils.getInstance().getString(this, AccountSPUtils.SECRET_KEY, "")
            mNavigationHead!!.user_id.text = IoTVideoSdk.getTerminalId().toString()
            mNavigationHead!!.iv_secret_id.text = AccountMgr.getProductId()
            mNavigationHead!!.iv_secret_key.text = ""
            mNavigationHead!!.tv_access_token.text = String.format("%s%s", realToken, secretKey)
            mNavigationHead!!.tv_access_id.text = AccountSPUtils.getInstance().getString(this, AccountSPUtils.ACCESS_ID, "")
        }
        registerNotify()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.logout -> {
                if (MyApp.ENABLE_TENCENTCS) {
                    tencentcsLogout()
                } else {
                    logout()
                }
            }
            R.id.start_test_activity -> startTestActivity()
            R.id.iv_token -> {
                Utils.setClipboard(this, mNavigationHead!!.iv_token.text.toString())
                Snackbar.make(view, R.string.copy_to_clipboard, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun startNetMatchActivity() {
        val intent = Intent(this, PrepareNetConfigActivity::class.java)
        startActivity(intent)
    }

    private fun startTestActivity() {
        val intent = Intent(this, TestActivity::class.java)
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
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }

            override fun onFail(e: Throwable) {
                showProgress(false)
                Snackbar.make(progress_logout, e.message.toString(), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun tencentcsLogout() {
        TencentcsAccountSPUtils.getInstance().clear(this@MainActivity)
        startActivity(Intent(this@MainActivity, TencentcsLoginActivity::class.java))
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
        if (item.itemId == android.R.id.home) {
            if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.openDrawer(GravityCompat.START)
            } else {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            return true
        } else if (item.itemId == R.id.action_menu_add) {
            startNetMatchActivity()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun startCaptureActivity() {
        requestPermissions(OnPermissionsListener { granted ->
            if (granted) {
                val intent = Intent(this@MainActivity, CustomCaptureActivity::class.java)
                startActivityForResult(intent, CAPTURE_REQUEST_CODE)
            }
        }, Manifest.permission.CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (CAPTURE_REQUEST_CODE == requestCode) {
            if (data != null) {
                val scanResult = data.getStringExtra(CaptureActivity.KEY_RESULT)
                LogUtils.i(TAG, "scan result = " + scanResult!!)
                val qrCode = QRCodeHelper.analyse(scanResult)
                LogUtils.i(TAG, "onResultCallback = $qrCode")
                QRCodeHelper.handleQRCode(qrCode, object : SubscriberListener {
                    override fun onStart() {

                    }

                    override fun onSuccess(response: JsonObject) {
                        Snackbar.make(progress_logout, R.string.success, Snackbar.LENGTH_LONG).show()
                    }

                    override fun onFail(e: Throwable) {
                        Snackbar.make(progress_logout, e.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    private fun registerNotify() {
        IoTVideoSdk.getMessageMgr().addEventListener { data -> Toast.makeText(applicationContext, data.data, Toast.LENGTH_LONG).show() }

        IoTVideoSdk.getMessageMgr().addModelListener { data ->
            LogUtils.i(TAG, "onModeChanged deviceId:" + data.device + ", path:" + data.path + ", data:" + data.data)
            Toast.makeText(applicationContext, "deviceId:" + data.device +
                    ", path:" + data.path + ", data:" + data.data, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        private const val CAPTURE_REQUEST_CODE = 1
    }
}
