/*
 * Copyright 2017 The Android Open Source Project
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

import android.os.Bundle;

import com.tencentcs.iotvideodemo.R;
import com.tencentcs.iotvideodemo.base.BaseActivity;

public class CameraActivity extends BaseActivity {
    public static final String OPERATE_TYPE = "operate_type";

    public static final int OPERATE_PREVIEW = 0;

    public static final int OPERATE_RECORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        int operaType = getIntent().getIntExtra(OPERATE_TYPE, OPERATE_PREVIEW);
        if (null == savedInstanceState) {
            if (operaType == OPERATE_PREVIEW) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, CameraPreviewFragment.newInstance())
                        .commit();
            } else if (operaType == OPERATE_RECORD) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, VideoRecordFragment.newInstance())
                        .commit();
            }
        }
    }

}
