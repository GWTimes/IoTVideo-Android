package com.tencentcs.iotvideodemo.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.videoplayer.MonitorConfig;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;

/**
 * 通过@style/SettingsActivityTheme修改布局、文字大小或颜色等样式
 */
public class DeviceSettingsActivity extends BaseActivity {
    private MonitorConfig mBeforeConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        mBeforeConfig = MonitorConfig.defaultConfig(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            MonitorConfig afterConfig = MonitorConfig.defaultConfig(this);
            Intent intent = new Intent();
            intent.putExtra("hasChanged", !MonitorConfig.compare(mBeforeConfig, afterConfig));
            setResult(1, intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.device_settings_root_preferences, rootKey);
        }
    }
}