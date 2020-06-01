package com.tencentcs.iotvideodemo.videoplayer;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tencentcs.iotvideo.iotvideoplayer.CallTypeEnum;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.utils.AppConfig;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MonitorPlayerActivity extends BaseActivity implements View.OnClickListener, MonitorPlayerFragment.OutputListener {
    private static final String TAG = "MonitorPlayerActivity";

    private FragmentManager fragmentManager;
    private TextView mLogTextView;
    private MonitorConfig mMonitorConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_player);
        mLogTextView = findViewById(R.id.output_txt);
        mLogTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        findViewById(R.id.tv_clear).setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();

        String deviceId = getIntent().getStringExtra("deviceID");

        MonitorPlayerFragment fragment = new MonitorPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("deviceID", deviceId);
        mMonitorConfig = MonitorConfig.defaultConfig();
        bundle.putSerializable(MonitorConfig.class.getSimpleName(), mMonitorConfig);
        fragment.setArguments(bundle);
        showFragment(R.id.monitor_1, fragment);
        fragment.setOutputListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_clear) {
            mLogTextView.setText("");
        }
    }

    private void showFragment(int resId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(resId, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onOutput(String text) {
        mLogTextView.append(text + "\n");
        Rect rect = new Rect();
        mLogTextView.getLineBounds(mLogTextView.getLineCount() - 1, rect);
        int offset = rect.bottom;
        if (offset > mLogTextView.getHeight()) {
            mLogTextView.scrollTo(0, offset - mLogTextView.getHeight());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_menu_setting) {
            new OEMDialog(this, mMonitorConfig).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private class OEMDialog extends AppCompatDialog {
        private Button confirmBtn;
        private Button cancelBtn;
        private Switch supportTalkSwitch;
        private Switch supportCameraSwitch;
        private Switch useMediaCodecAudioDecodeSwitch;
        private Switch useMediaCodecVideoDecodeSwitch;
        private Switch useMediaCodecAudioEncodeSwitch;
        private Spinner mDefinitionSpinner;

        private OEMDialog(@NonNull Context context, MonitorConfig config) {
            super(context);
            setContentView(R.layout.dialog_oem_monitor);
            setTitle("监控参数设置");
            confirmBtn = findViewById(R.id.btn_confirm);
            cancelBtn = findViewById(R.id.btn_cancel);
            supportTalkSwitch = findViewById(R.id.support_talk);
            supportCameraSwitch = findViewById(R.id.support_camera);
            useMediaCodecAudioDecodeSwitch = findViewById(R.id.use_mediacodec_audio_decode);
            useMediaCodecVideoDecodeSwitch = findViewById(R.id.use_mediacodec_video_decode);
            useMediaCodecAudioEncodeSwitch = findViewById(R.id.use_mediacodec_audio_encode);
            mDefinitionSpinner = findViewById(R.id.spinner_definition);

            List<String> definitionList = new ArrayList<>();
            definitionList.add("LD");
            definitionList.add("SD");
            definitionList.add("HD");
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, definitionList);
            mDefinitionSpinner.setAdapter(arrayAdapter);

            supportTalkSwitch.setChecked(config.supportTalk);
            supportCameraSwitch.setChecked(config.supportCamera);
            useMediaCodecAudioDecodeSwitch.setChecked(config.useMediaCodecAudioDecode);
            useMediaCodecVideoDecodeSwitch.setChecked(config.useMediaCodecVideoDecode);
            useMediaCodecAudioEncodeSwitch.setChecked(config.useMediaCodecAudioEncode);
            if (AppConfig.MONITOR_DEFINITION == CallTypeEnum.VIDEO_DEFINITION_FL) {
                mDefinitionSpinner.setSelection(0);
            } else if (AppConfig.MONITOR_DEFINITION == CallTypeEnum.VIDEO_DEFINITION_SD) {
                mDefinitionSpinner.setSelection(1);
            } else if (AppConfig.MONITOR_DEFINITION == CallTypeEnum.VIDEO_DEFINITION_HD) {
                mDefinitionSpinner.setSelection(2);
            }

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppConfig.SUPPORT_TALK = supportTalkSwitch.isChecked();
                    AppConfig.SUPPORT_CAMERA = supportCameraSwitch.isChecked();
                    AppConfig.USE_MEDIACODEC_AUDIO_DECODE = useMediaCodecAudioDecodeSwitch.isChecked();
                    AppConfig.USE_MEDIACODEC_VIDEO_DECODE = useMediaCodecVideoDecodeSwitch.isChecked();
                    AppConfig.USE_MEDIACODEC_AUDIO_ENCODE = useMediaCodecAudioEncodeSwitch.isChecked();
                    mMonitorConfig = MonitorConfig.defaultConfig();

                    Toast.makeText(context, "退出重新进入监控生效", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            mDefinitionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0) {
                        AppConfig.MONITOR_DEFINITION = CallTypeEnum.VIDEO_DEFINITION_FL;
                    } else if (i == 1) {
                        AppConfig.MONITOR_DEFINITION = CallTypeEnum.VIDEO_DEFINITION_SD;
                    } else if (i == 2) {
                        AppConfig.MONITOR_DEFINITION = CallTypeEnum.VIDEO_DEFINITION_HD;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }
}
