package com.example.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chattingapp.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    String phonenumber;
    EditText otpInput;
    Button nextBtn;
    ProgressBar progressBar;
    TextView resendOtpTextView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Long timeoutSeconds = 60L;
    String VerificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpInput = findViewById(R.id.otp_btn); 
        nextBtn = findViewById(R.id.otp_next);
        progressBar = findViewById(R.id.progressbar_btn);
        resendOtpTextView = findViewById(R.id.resend_otp);

        phonenumber = getIntent().getExtras().getString("phone");
        sendOtp(phonenumber, false);

        nextBtn.setOnClickListener(view -> {
            String enteredOtp = otpInput.getText().toString().trim();
            if (enteredOtp.length() < 6) {
                AndroidUtil.showtoast(getApplicationContext(), "Please enter a valid 6-digit OTP");
                return;
            }
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationCode, enteredOtp);
            signIn(credential);
            setInProgress(true);
        });

        resendOtpTextView.setOnClickListener((v) -> sendOtp(phonenumber, true));
    }

    void sendOtp(String phoneNumber, Boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showtoast(getApplicationContext(), "OTP Verification failed");
                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        VerificationCode = s;
                        resendingToken = forceResendingToken;
                        AndroidUtil.showtoast(getApplicationContext(), "OTP sent successfully");
                        setInProgress(false);
                    }
                });

        if (isResend) {
            builder.setForceResendingToken(resendingToken);
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build());
    }

    void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        nextBtn.setVisibility(inProgress ? View.GONE : View.VISIBLE);
        otpInput.setEnabled(!inProgress);
    }

    void signIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(OtpActivity.this, UsernameActivity.class);
                    intent.putExtra("phone", phonenumber);
                    startActivity(intent);
                    finish();
                } else {
                    AndroidUtil.showtoast(getApplicationContext(), "OTP Verification failed");
                }
            }
        });
    }

    void startResendTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timeoutSeconds = 60L;
        resendOtpTextView.setEnabled(false);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    timeoutSeconds--;
                    resendOtpTextView.setText("Resend OTP in " + timeoutSeconds + " seconds");
                    if (timeoutSeconds <= 0) {
                        timer.cancel();
                        resendOtpTextView.setText("Resend OTP");
                        resendOtpTextView.setEnabled(true);
                    }
                });
            }
        }, 0, 1000);
    }
}
