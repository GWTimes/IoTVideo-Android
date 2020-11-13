package com.tencentcs.iotvideodemo.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.tencentcs.iotvideodemo.R;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class JumpPreference extends Preference {
    private String mRightMessage;

    public JumpPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.JumpPreference, defStyleAttr, defStyleRes);

        mRightMessage = a.getString(R.styleable.JumpPreference_rightMessage);

        a.recycle();
    }

    public JumpPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public JumpPreference(Context context, AttributeSet attrs) {
        this(context, attrs, getAttr(context, R.attr.jumpPreferenceStyle,
                android.R.attr.preferenceStyle));
    }

    public JumpPreference(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View rightView = holder.findViewById(R.id.right);
        if (rightView instanceof TextView) {
            ((TextView) rightView).setText(mRightMessage);
        }
    }

    /**
     * @return The resource ID value in the {@code context} specified by {@code attr}. If it does
     * not exist, {@code fallbackAttr}.
     */
    public static int getAttr(@NonNull Context context, int attr, int fallbackAttr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        if (value.resourceId != 0) {
            return attr;
        }
        return fallbackAttr;
    }
}
