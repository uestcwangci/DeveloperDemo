package com.example.dell.developerdemo.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.dell.developerdemo.R;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

public final class MySlide extends Fragment implements ISlideBackgroundColorHolder {


    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private static final String ARG_BG_COLOR = "bg_color";
    private int layoutResId, bgColor;
    private LinearLayout mainLayout;


    public MySlide() {
    }

    public static MySlide newInstance(int layoutResId, @ColorInt int bgColor) {
        MySlide mySlide = new MySlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        args.putInt(ARG_BG_COLOR, bgColor);
        mySlide.setArguments(args);

        return mySlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().size() != 0) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
            bgColor = getArguments().getInt(ARG_BG_COLOR);
        }
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(layoutResId, container, false);
        mainLayout = v.findViewById(R.id.main_bg);
        mainLayout.setBackgroundColor(bgColor);
        return v;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return bgColor;
//        return Color.parseColor("#000000");
    }
    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (mainLayout != null) {
            mainLayout.setBackgroundColor(backgroundColor);
        }
    }
}