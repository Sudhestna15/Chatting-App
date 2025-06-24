package com.example.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    ProgressBar progressbar;

    Button sendotpbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        countryCodePicker=  findViewById(R.id.login_countrycode);
        phoneInput= (EditText) findViewById(R.id.login_mobile_number);
        sendotpbtn= (Button) findViewById(R.id.otp_btn);
        progressbar= (ProgressBar) findViewById(R.id.progressbar_btn);

        progressbar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendotpbtn.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number not valid");
                return;
            }
            Intent intent =new Intent(LoginPhoneNumberActivity.this,OtpActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
                    startActivity(intent);
        });


    }
}