package com.fireextinguisher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fireextinguisher.client.ClientQRCodeScanActivity;
import com.fireextinguisher.utils.Constant;
import com.fireextinguisher.utils.Utility;

public class SplashScreenActivity extends AppCompatActivity {

    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mContext = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utility.getSharedPreferences(mContext, Constant.userId) != null) {
                    Intent intent = new Intent(mContext, SelectClientNameActivity.class);
                    startActivity(intent);
                    finish();
                } else if (Utility.getSharedPreferences(mContext, Constant.clientId) != null) {
                    Intent intent = new Intent(mContext, ClientQRCodeScanActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2500);
    }
}