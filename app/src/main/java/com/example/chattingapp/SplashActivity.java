package com.example.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.chattingapp.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable(){
            public void run(){
                if(FirebaseUtil.isLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                }
            finish();
            }
        },1000);
    }
}