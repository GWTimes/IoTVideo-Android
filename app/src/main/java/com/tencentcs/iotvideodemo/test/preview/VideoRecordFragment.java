/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencentcs.iotvideodemo.test.preview;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tencentcs.iotvideo.iotvideoplayer.capture.VideoCapture;
import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseFragment;
import com.tencentcs.iotvideodemo.utils.StorageManager;

import androidx.core.app.ActivityCompat;

public class VideoRecordFragment extends BaseFragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "VideoRecordFragment";

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    /**
     * Button to record video
     */
    private Button mButtonVideo;

    /**
     * Whether the app is recording video now
     */
    private boolean mIsRecordingVideo;

    private VideoCapture mVideoCapture;

    public static VideoRecordFragment newInstance() {
        return new VideoRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_record, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mButtonVideo = view.findViewById(R.id.video);
        mButtonVideo.setOnClickListener(this);
        if (StorageManager.isVideoPathAvailable()) {
            mVideoCapture = new VideoCapture(StorageManager.getVideoPath());
        } else {
            Toast.makeText(getContext(), "storage is not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        if (mVideoCapture != null && mVideoCapture.isCameraOpen()) {
            closeCamera();
        }
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video: {
                if (mIsRecordingVideo) {
                    closeCamera();
                } else {
                    openCamera();
                }
                break;
            }
        }
    }

    private void openCamera() {
        if (mVideoCapture == null) {
            return;
        }
        requestPermissions(new BaseFragment.OnPermissionsListener() {
            @Override
            public void OnPermissions(boolean granted) {
                final Activity activity = getActivity();
                if (null == activity || activity.isFinishing()) {
                    return;
                }
                if (granted) {
                    mVideoCapture.openCamera(activity);
                    mIsRecordingVideo = true;
                    mButtonVideo.setText(R.string.stop);
                }
            }
        }, VIDEO_PERMISSIONS);
    }

    private void closeCamera() {
        if (mVideoCapture == null) {
            return;
        }
        mVideoCapture.closeCamera();
        mIsRecordingVideo = false;
        mButtonVideo.setText(R.string.record);
        Toast.makeText(getContext(), mVideoCapture.getVideoFilePath(), Toast.LENGTH_LONG).show();
    }

}
