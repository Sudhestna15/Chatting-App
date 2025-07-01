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
import android.widget.Toast;

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
    Button nextBtn ;
    ProgressBar progressBar;
    TextView resendOtpTextView;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    Long timeoutSeconds= 60L;
    String VerificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpInput= findViewById(R.id.login_otp);
        nextBtn = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView= findViewById(R.id.resend_otp_textveiw);

        phonenumber=getIntent().getExtras().getString("phone");
        sendOtp(phonenumber,false);

        nextBtn.setOnClickListener(view -> {
            String enteredOtp= otpInput.getText().toString();
            PhoneAuthCredential credential= PhoneAuthProvider.getCredential(VerificationCode,enteredOtp);
            signIn(credential);
            setInProgress(true);
        });

        resendOtpTextView.setOnClickListener((v)->{
            sendOtp(phonenumber,true);
        });

    }
    void sendOtp(String phoneNumber, Boolean isResend ){
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)
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
                                AndroidUtil.showtoast(getApplicationContext(),"OTP Verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                VerificationCode= s;
                                resendingToken= forceResendingToken;
                                AndroidUtil.showtoast(getApplicationContext(),"OTP send successfully");
                                setInProgress(false);
                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }
    void setInProgress( boolean inProgress){
        if(inProgress ){
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }
    void signIn(PhoneAuthCredential phoneAuthCredential){
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent =new Intent(OtpActivity.this, Usernameactivity.class);
                    intent.putExtra("phone",phonenumber);
                    startActivity(intent);
                }else{
                    AndroidUtil.showtoast(getApplicationContext(),"OTP Verification failed");
                }
            }
        });
    }


    void startResendTimer(){
        resendOtpTextView.setEnabled(false);
        Timer time = new Timer();
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendOtpTextView.setText("Resend OTP in "+timeoutSeconds+"seconds");
                if(timeoutSeconds <= 0){
                    timeoutSeconds= 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtpTextView.setEnabled(true);
                    });


                }
            }
        },0,1000);
    }
}