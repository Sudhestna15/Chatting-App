package com.example.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class OtpActivity extends AppCompatActivity {

    String phonenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        phonenumber=getIntent().getExtras().getString("phone");
        Toast.makeText(getApplicationContext(),phonenumber,Toast.LENGTH_LONG).show();
    }
}