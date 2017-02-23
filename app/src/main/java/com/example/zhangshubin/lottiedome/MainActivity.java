package com.example.zhangshubin.lottiedome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.animation_view)
    LottieAnimationView mAnimationView;
    @BindView(R.id.animation_view1)
    LottieAnimationView mAnimationView1;
    @BindView(R.id.activity_main)
    LinearLayout mActivityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAnimationView1.setAnimation("all.json");
        mAnimationView1.loop(true);
    }
}
