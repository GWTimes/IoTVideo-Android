package com.tencentcs.iotvideodemo.vas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencentcs.iotvideo.IoTVideoSdk;
import com.tencentcs.iotvideo.accountmgr.AccountMgr;
import com.tencentcs.iotvideo.messagemgr.ModelMessage;
import com.tencentcs.iotvideo.utils.JSONUtils;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.rxjava.IResultListener;
import com.tencentcs.iotvideo.utils.rxjava.SubscriberListener;
import com.tencentcs.iotvideo.vas.VasMgr;
import com.tencentcs.iotvideo.vas.VasService;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.accountmgr.devicemanager.DeviceList;
import com.tencentcs.iotvideodemo.base.BaseActivity;
import com.tencentcs.iotvideodemo.netconfig.FragmentAdapter;
import com.tencentcs.iotvideodemo.netconfig.ap.APNetConfigFragment;
import com.tencentcs.iotvideodemo.netconfig.qrcode.QRCodeNetConfigFragment;
import com.tencentcs.iotvideodemo.netconfig.wired.WiredNetConfigFragment;
import com.tencentcs.iotvideodemo.videoplayer.ExoPlayerActivity;
import com.tencentcs.iotvideodemo.videoplayer.ijkplayer.IjkPlayerActivity;
import com.tencentcs.iotvideodemo.widget.RecycleViewDivider;
import com.tencentcs.iotvideodemo.widget.SimpleRecyclerViewAdapter;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class CloudStorageActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CloudStorageActivity";

    private static final String[] PACKAGE_ARRAY = {
            "yc1m3d",
            "yc1m7d",
            "yc1m30d",
            "yc1y3d",
            "yc1y7d",
            "yc1y30d",
            "ye1m3d",
            "ye1m7d",
            "ye1m30d",
            "ye1y3d",
            "ye1y7d",
            "ye1y30d",
    };
    private static final String[] PACKAGE_PRICE = {
            "1$",
            "2$",
            "3$",
            "4$",
            "5$",
            "6$",
            "7$",
            "8$",
            "9$",
            "10$",
            "11$",
            "12$",
    };

    private static final String[] STORAGE_REGION = {
        "ap-guangzhou","ap-singapore"
    };

    private TextView mTvBuyStartTime, mTvBuyEndTime, mTvPlaybackStartTime, mTvPlaybackEndTime;

    private Spinner mPackageSpinner;
    private TextView mTvPackagePrice;

    private VasService mVasService;

    private int mBuyStartTime, mBuyEndTime;
    private long mPlaybackStartTime, mPlaybackEndTime;
    private String mCurrentPackageName;

    private DeviceList.Device mDevice;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MenuItem mRefreshMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_storage);

        mPackageSpinner = findViewById(R.id.spinner_package);
        mTvPackagePrice = findViewById(R.id.tv_package_price);
        mTvBuyStartTime = findViewById(R.id.buy_start_time);
        mTvBuyStartTime.setOnClickListener(this);
        mTvBuyEndTime = findViewById(R.id.buy_end_time);
        mTvBuyEndTime.setOnClickListener(this);
        mTvPlaybackStartTime = findViewById(R.id.playback_start_time);
        mTvPlaybackStartTime.setOnClickListener(this);
        mTvPlaybackEndTime = findViewById(R.id.playback_end_time);
        mTvPlaybackEndTime.setOnClickListener(this);

        mVasService = VasMgr.getVasService();
        findViewById(R.id.buy).setOnClickListener(this);
        findViewById(R.id.cloud_service_query).setOnClickListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, Arrays.asList(PACKAGE_ARRAY));
        mPackageSpinner.setAdapter(arrayAdapter);
        mPackageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mTvPackagePrice.setText(PACKAGE_PRICE[i]);
                mCurrentPackageName = PACKAGE_ARRAY[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (getIntent() != null) {
            mDevice = (DeviceList.Device) getIntent().getSerializableExtra("Device");
            LogUtils.i(TAG, "mDevice = " + mDevice.toString());
        }

        initViewPager();
    }

    private void initViewPager() {
        mTabLayout = findViewById(R.id.tab_layout_main);
        mViewPager = findViewById(R.id.view_pager_main);

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.cloud_video_area));
        titles.add(getString(R.string.cloud_play_url_list));
        titles.add(getString(R.string.cloud_event_list));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TsListFragment());
        fragments.add(new PlaybackListFragment());
        fragments.add(new EventListFragment());

        mViewPager.setOffscreenPageLimit(0);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buy_start_time) {
            showDatePickerDialog(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    mBuyStartTime = dataToTime(year, monthOfYear, dayOfMonth, mTvBuyStartTime, mTvPlaybackStartTime);
                    mPlaybackStartTime = mBuyStartTime * 1000L;
                }
            });
        } else if (view.getId() == R.id.buy_end_time) {
            showDatePickerDialog(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    mBuyEndTime = dataToTime(year, monthOfYear, dayOfMonth, mTvBuyEndTime, mTvPlaybackEndTime);
                    mPlaybackEndTime = mBuyEndTime * 1000L;
                }
            });
        } else if (view.getId() == R.id.playback_start_time) {
            showTimePickerDialog(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    mPlaybackStartTime = timeToTime(mBuyStartTime, i, i1, mTvBuyStartTime, mTvPlaybackStartTime);
                }
            });
        } else if (view.getId() == R.id.playback_end_time) {
            showTimePickerDialog(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    mPlaybackEndTime = timeToTime(mBuyEndTime, i, i1, mTvBuyEndTime, mTvPlaybackEndTime);
                }
            });
        } else if (view.getId() == R.id.buy) {
            AccountMgr.getHttpService().cloudStorageCreate( mDevice.getDevId(), mCurrentPackageName, 1,
                    STORAGE_REGION[0], new SubscriberListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(@NonNull JsonObject response) {
                    Snackbar.make(mTvBuyStartTime, response.toString(), Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onFail(@NonNull Throwable e) {
                    Snackbar.make(mTvBuyStartTime, e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }else if (view.getId() == R.id.cloud_service_query) {
            queryBuyedService();
        }
    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePickerDialog(TimePickerDialog.OnTimeSetListener listener) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, listener,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);

        timePickerDialog.show();
    }

    private int dataToTime(int year, int monthOfYear, int dayOfMonth, TextView dateTextView, TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        dateTextView.setText(date);
        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        timeTextView.setText(time);
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    private long timeToTime(int dataTime, int hourOfDay, int minute, TextView dateTextView, TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dataTime * 1000L);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        dateTextView.setText(date);
        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        timeTextView.setText(time);
        return calendar.getTimeInMillis();
    }

    public long getmPlaybackStartTime() {
        return mPlaybackStartTime;
    }

    public long getmPlaybackEndTime() {
        return mPlaybackEndTime;
    }

    public DeviceList.Device getmDevice() {
        return mDevice;
    }

    public VasService getmVasService() {
        return mVasService;
    }

    private void queryBuyedService() {
        IoTVideoSdk.getMessageMgr().readProperty(mDevice.getDevId(), "ProWritable._cloudStoage", new IResultListener<ModelMessage>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(ModelMessage msg) {
                LogUtils.i(TAG,"queryBuyedService successful:" + msg);
                if (isFinishing()) {
                    return;
                }
                if (TextUtils.isEmpty(msg.data)) {
                    Snackbar.make(mTvPackagePrice,"Invalid data",Snackbar.LENGTH_LONG).show();
                    return;
                }
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(msg.data).getAsJsonObject();
                if (null == jsonObject) {
                    Snackbar.make(mTvPackagePrice,"Invalid data",Snackbar.LENGTH_LONG).show();
                    return;
                }

                JsonObject storageJson = jsonObject.get("setVal").getAsJsonObject();
                LogUtils.i(TAG,"json string:" + storageJson.toString());
                CloudStorageInfo storageInfo = JSONUtils.JsonToEntity(storageJson.toString(), CloudStorageInfo.class);
                if (null == storageInfo) {
                    Snackbar.make(mTvPackagePrice,"Invalid data",Snackbar.LENGTH_LONG).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(CloudStorageActivity.this);
                builder.setTitle("云服务套餐信息");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("套餐类型："+ storageInfo.getServiceTypeInfo());
                stringBuilder.append("\n");
                stringBuilder.append("到期时间："+ storageInfo.getUtcExpireInfo());
                stringBuilder.append("\n");
                stringBuilder.append("套餐状态："+ storageInfo.getServiceStateInfo());
                builder.setMessage(stringBuilder.toString());
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setCancelable(true);
                //设置正面按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.i(TAG,"queryBuyedService onError:" + errorCode + "; errorMsg:" + errorMsg);
            }
        });
    }
}
