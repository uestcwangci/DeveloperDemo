package com.example.dell.developerdemo.activities;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.example.dell.developerdemo.R;
import com.example.dell.developerdemo.fragments.MySlide;
import com.github.paolorotolo.appintro.AppIntro;


public class IntroActivity extends AppIntro {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全面屏显示
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
//        添加页面默认方式
//        SliderPage sliderPage = new SliderPage();
//        sliderPage.setTitle("This is Title");
//        sliderPage.setDescription("A loooooooooooooooooooooooong description");
//        sliderPage.setDescColor(Color.parseColor("#11ff00"));
//        sliderPage.setImageDrawable(R.mipmap.handshake);
//        addSlide(AppIntroFragment.newInstance(sliderPage));

        addSlide(MySlide.newInstance(R.layout.fragment_sample_slide, Color.parseColor("#3f72af")));
        addSlide(MySlide.newInstance(R.layout.fragment_sample_slide2, Color.parseColor("#e23e57")));
//        askForPermissions(permissionStr, 2);
        setColorTransitionsEnabled(true);


        // Hide Skip/Done button.e
        showSkipButton(false);
//        setProgressButtonEnabled(false);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
//        setVibrate(true);
//        setVibrateIntensity(30);


        // Set Animation
//        setFlowAnimation();
//        setFadeAnimation();
//        setZoomAnimation();
//        setDepthAnimation();
//        setSlideOverAnimation();
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
  
}
