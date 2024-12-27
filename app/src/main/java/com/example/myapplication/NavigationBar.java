package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavigationBar extends LinearLayout {

    private TextView titleTextView;
    private ImageView leftIconImageView;
    private ImageView rightIconImageView;

    public NavigationBar(Context context) {
        super(context);
        init(context);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.navigationbar, this, true);
        titleTextView = findViewById(R.id.nav_title);
        leftIconImageView = findViewById(R.id.nav_left);
        rightIconImageView = findViewById(R.id.nav_right);

        // 添加一些日志来检查视图是否正确初始化
        if (titleTextView == null) {
            throw new IllegalStateException("titleTextView is not initialized");
        }
        if (leftIconImageView == null) {
            throw new IllegalStateException("leftIconImageView is not initialized");
        }
        if (rightIconImageView == null) {
            throw new IllegalStateException("rightIconImageView is not initialized");
        }
    }

    public void setTitle(String title) {
        if (titleTextView != null) {
            titleTextView.setText(title);
        } else {
            throw new IllegalStateException("titleTextView is null");
        }
    }

    public void setLeftIcon(int iconResId) {
        if (leftIconImageView != null) {
            leftIconImageView.setImageResource(iconResId);
        } else {
            throw new IllegalStateException("leftIconImageView is null");
        }
    }

    public void setRightIcon(int iconResId) {
        if (rightIconImageView != null) {
            rightIconImageView.setImageResource(iconResId);
        } else {
            throw new IllegalStateException("rightIconImageView is null");
        }
    }

    public void setLeftClickListener(OnClickListener listener) {
        if (leftIconImageView != null) {
            leftIconImageView.setOnClickListener(listener);
        } else {
            throw new IllegalStateException("leftIconImageView is null");
        }
    }

    public void setRightClickListener(OnClickListener listener) {
        if (rightIconImageView != null) {
            rightIconImageView.setOnClickListener(listener);
        } else {
            throw new IllegalStateException("rightIconImageView is null");
        }
    }
}