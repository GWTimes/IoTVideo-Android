package com.tencentcs.iotvideodemo.videoplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencentcs.iotvideo.iotvideoplayer.IErrorListener;
import com.tencentcs.iotvideo.iotvideoplayer.IPreparedListener;
import com.tencentcs.iotvideo.iotvideoplayer.IStatusListener;
import com.tencentcs.iotvideo.iotvideoplayer.IUserDataListener;
import com.tencentcs.iotvideo.iotvideoplayer.PlayerStateEnum;
import com.tencentcs.iotvideo.iotvideoplayer.player.TransmissionConnection;
import com.tencentcs.iotvideo.utils.LogUtils;
import com.tencentcs.iotvideo.utils.Utils;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class TransmissionConnectionActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TransConnActivity";

    private Button mBtnConnect;
    private Button mBtnDisconnect;
    private Button mBtnSendCharacter;
    private Button mBtnSendPic;
    private TextView mTvConnectStatus;
    private TextView mTvOutput;
    private EditText mEtInput;
    private ImageView mIvLocalPic;
    private ImageView mIvRemotePic;

    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private TransmissionConnection mConnection;
    private byte[] mPicBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmission_connection);
        mBtnConnect = findViewById(R.id.btn_connect);
        mBtnDisconnect = findViewById(R.id.btn_disconnect);
        mBtnSendCharacter = findViewById(R.id.btn_send);
        mBtnSendPic = findViewById(R.id.btn_send_pic);
        mTvOutput = findViewById(R.id.tv_output);
        mEtInput = findViewById(R.id.et_input);
        mTvConnectStatus = findViewById(R.id.tv_connect_status);
        mIvLocalPic = findViewById(R.id.local_pic);
        mIvRemotePic = findViewById(R.id.remote_pic);
        mBtnConnect.setOnClickListener(this);
        mBtnDisconnect.setOnClickListener(this);
        mBtnSendCharacter.setOnClickListener(this);
        mBtnSendPic.setOnClickListener(this);
        mTvOutput.setMovementMethod(ScrollingMovementMethod.getInstance());

        String deviceId = getIntent().getStringExtra("deviceID");

        mConnection = new TransmissionConnection();
        mConnection.setDataResource(deviceId);
        mConnection.setPreparedListener(mPreparedListener);
        mConnection.setStatusListener(mStatusListener);
        mConnection.setErrorListener(mErrorListener);
        mConnection.setUserDataListener(mUserDataListener);

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("test.jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPicBytes = readInputStream(inputStream);
        mIvLocalPic.setImageBitmap(getPicFromBytes(mPicBytes, null));
        LogUtils.i(TAG, "mPicBytes length = " + mPicBytes.length);
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    private IPreparedListener mPreparedListener = new IPreparedListener() {
        @Override
        public void onPrepared() {
            LogUtils.d(TAG, "onPrepared");
            mTvConnectStatus.setText("开始准备");
            appendToOutput("开始准备");
        }
    };

    private IStatusListener mStatusListener = new IStatusListener() {
        @Override
        public void onStatus(int status) {
            LogUtils.d(TAG, "onStatus status " + status);
            mTvConnectStatus.setText(getPlayStatus(status));
            appendToOutput(getPlayStatus(status));
        }
    };

    private IErrorListener mErrorListener = new IErrorListener() {
        @Override
        public void onError(int error) {
            LogUtils.d(TAG, "onError error " + error);
            mTvConnectStatus.setText(Utils.getErrorDescription(error));
            appendToOutput(Utils.getErrorDescription(error));
        }
    };

    private IUserDataListener mUserDataListener = new IUserDataListener() {
        @Override
        public void onReceive(byte[] data) {
            LogUtils.d(TAG, "onReceive ---- " + data.length);
            if (data.length > 1024 * 10) {
                showRemotePic(data);
                appendToOutput("收到图片");
            } else {
                appendToOutput("收到文字：" + new String(data));
            }
        }
    };

    private void appendToOutput(String text) {
        LogUtils.i(TAG, "appendToOutput " + text);
        mTvOutput.append(formatter.format(new Date()) + " " + text + "\n");
        Rect rect = new Rect();
        mTvOutput.getLineBounds(mTvOutput.getLineCount() - 1, rect);
        int offset = rect.bottom;
        if (offset > mTvOutput.getHeight()) {
            mTvOutput.scrollTo(0, offset - mTvOutput.getHeight());
        }
    }

    private String getPlayStatus(int status) {
        String playStatus = "";
        switch (status) {
            case PlayerStateEnum.STATE_IDLE:
                playStatus = "未初始化";
                break;
            case PlayerStateEnum.STATE_INITIALIZED:
                playStatus = "已初始化";
                break;
            case PlayerStateEnum.STATE_PREPARING:
                playStatus = "连接中...";
                break;
            case PlayerStateEnum.STATE_READY:
                playStatus = "连接成功";
                break;
            case PlayerStateEnum.STATE_LOADING:
                playStatus = "加载中";
                break;
            case PlayerStateEnum.STATE_PLAY:
                playStatus = "播放中";
                break;
            case PlayerStateEnum.STATE_PAUSE: {
                playStatus = "暂停";
            }
            break;
            case PlayerStateEnum.STATE_STOP:
                playStatus = "断开连接";
                break;
        }
        return playStatus;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                connect();
                break;
            case R.id.btn_disconnect:
                disconnect();
                break;
            case R.id.btn_send:
                send(0, mEtInput.getText().toString().getBytes());
                break;
            case R.id.btn_send_pic:
                if (mPicBytes != null && mPicBytes.length > 1024 * 10) {
                    send(1, mPicBytes);
                    mIvRemotePic.setImageBitmap(null);
                }
                break;
        }
    }

    private void connect() {
        mConnection.play();
    }

    private void disconnect() {
        mConnection.stop();
    }

    private void release() {
        if (mConnection != null) {
            mConnection.release();
            mConnection = null;
        }
    }

    private void send(int type, byte[] data) {
        int ret = mConnection.sendUserData(data);
        String text = null;
        if (type == 0) {
            text = new String(data);
        } else if (type == 1) {
            text = "图片";
        }
        if (ret == 0) {
            appendToOutput("发送成功 : " + text);
        } else if (ret == -2) {
            appendToOutput("当前发送缓存空间不够，稍后再试");
        } else if (ret == -3) {
            appendToOutput("单次发送数据不能超过63K");
        } else {
            appendToOutput("发送失败");
        }
    }

    private void showRemotePic(byte[] data) {
        mIvRemotePic.setImageBitmap(getPicFromBytes(data, null));
    }

    private byte[] readInputStream(InputStream inputStream) {
        // 1.建立通道对象
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 2.定义存储空间
        byte[] buffer = new byte[1024];
        // 3.开始读文件
        int len;
        try {
            if (inputStream != null) {
                while ((len = inputStream.read(buffer)) != -1) {
                    // 将Buffer中的数据写到outputStream对象中
                    outputStream.write(buffer, 0, len);
                }
                // 4.关闭流
                outputStream.close();
                inputStream.close();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null) {
            if (opts != null) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            } else {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
        return null;
    }
}
