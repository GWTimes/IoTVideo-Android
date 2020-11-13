package com.tencentcs.iotvideodemo.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.tencentcs.iotvideodemo.R;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

public class IoTPreferenceCategory extends PreferenceCategory {
    private @ColorInt int mLeftColor;

    public IoTPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IoTPreferenceCategory, defStyleAttr, defStyleRes);

        mLeftColor = a.getColor(R.styleable.IoTPreferenceCategory_leftColor,
                context.getResources().getColor(R.color.colorAccent));

        a.recycle();
    }

    public IoTPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public IoTPreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, getAttr(context, R.attr.preferenceCategoryStyle,
                android.R.attr.preferenceCategoryStyle));
    }

    public IoTPreferenceCategory(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View leftView = holder.findViewById(R.id.left_image);
        leftView.setBackgroundColor(mLeftColor);
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
