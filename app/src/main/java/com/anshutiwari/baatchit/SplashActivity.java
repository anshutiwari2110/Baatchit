package com.anshutiwari.baatchit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME = 5000;
    LottieAnimationView mLotIcon;
    TextView mTvAppName;
    TextView mTvEncryptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mTvAppName = findViewById(R.id.tv_appName);
        mTvEncryptionText = findViewById(R.id.tv_encryption);
        mLotIcon = findViewById(R.id.lottie_appIcon);

        mLotIcon.animate().translationX(3000).setDuration(1000).setStartDelay(4000);
        mTvAppName.animate().translationX(3000).setDuration(1000).setStartDelay(4000);
        mTvEncryptionText.animate().translationX(3000).setDuration(1000).setStartDelay(4000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
            }
        },SPLASH_TIME);

    }
}