package com.gwell.iotvideodemo

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.gwell.iotvideo.IoTVideoSdk
import com.gwell.iotvideo.accountmgr.AccountMgr
import com.gwell.iotvideo.utils.LogUtils
import com.gwell.iotvideo.utils.UrlHelper
import com.gwell.iotvideo.utils.qrcode.QRCodeHelper
import com.gwell.iotvideo.utils.rxjava.SubscriberListener
import com.gwell.iotvideodemo.accountmgr.AccountSPUtils
import com.gwell.iotvideodemo.accountmgr.devicemanager.DeviceManagerActivity
import com.gwell.iotvideodemo.accountmgr.login.LoginActivity
import com.gwell.iotvideodemo.base.BaseActivity
import com.gwell.iotvideodemo.base.BaseActivity.OnPermissionsListener
import com.gwell.iotvideodemo.netconfig.PrepareNetConfigActivity
import com.gwell.iotvideodemo.test.TestActivity
import com.gwell.iotvideodemo.utils.AppSPUtils
import com.gwell.iotvideodemo.utils.Utils
import com.gwell.iotvideodemo.videoplayer.CustomCaptureActivity
import com.gwell.zxing.CaptureActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_pager.*
import kotlinx.android.synthetic.main.layout_navigation_head.view.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private var mNavigationHead: View? = null

    private var mFirstTimeClickBack: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        device_manager.setOnClickListener(this)
        net_config.setOnClickListener(this)
        if (BuildConfig.DEBUG) {
            start_test_activity.visibility = View.VISIBLE
            start_test_activity.setOnClickListener(this)
        }
        github.setOnClickListener(this)
        mNavigationHead = nav_view.getHeaderView(0)
        mNavigationHead!!.tv_app_version.text = BuildConfig.VERSION_NAME
        mNavigationHead!!.user_id.text = AccountSPUtils.getInstance().getUserId(this)
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

        val realToken = AccountSPUtils.getInstance().getString(this, AccountSPUtils.TOKEN, "")
        val secretKey = AccountSPUtils.getInstance().getString(this, AccountSPUtils.SECRET_KEY, "")
        mNavigationHead!!.iv_token.text = String.format("%s%s", realToken, secretKey)

        //设置log
        applyForStoragePerMission()

        registerNotify()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.net_config -> startNetMatchActivity()
            R.id.logout -> logout()
            R.id.device_manager -> startDeviceManagerActivity()
            R.id.start_test_activity -> startTestActivity()
            R.id.github -> {
                val intent = Intent()
                intent.data = Uri.parse("https://github.com/GWTimes/IoTVideo-Android")
                intent.action = Intent.ACTION_VIEW
                startActivity(intent)
            }
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

    private fun startDeviceManagerActivity() {
        val intent = Intent(this, DeviceManagerActivity::class.java)
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
        } else if (item.itemId == R.id.action_menu_scan) {
            startCaptureActivity()
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

    override fun applyForPermissionResult(mark: Int, permissionResult: Map<String, Boolean>, applyResult: Boolean) {
        super.applyForPermissionResult(mark, permissionResult, applyResult)
        if (applyResult) {
            val readStorage = permissionResult[Manifest.permission.READ_EXTERNAL_STORAGE]
            val writeStorage = permissionResult[Manifest.permission.WRITE_EXTERNAL_STORAGE]
            if (readStorage != null && writeStorage != null && readStorage && writeStorage) {
                IoTVideoSdk.setDebugMode(true, 1)
            }
        }
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
            Toast.makeText(applicationContext, "deviceId:" + data.device +
                    ", path:" + data.path + ", data:" + data.data, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        private const val CAPTURE_REQUEST_CODE = 1
    }
}
