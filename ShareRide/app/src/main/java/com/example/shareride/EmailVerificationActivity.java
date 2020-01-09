package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EmailVerificationActivity extends AppCompatActivity {

    public void goToSignInActivity(View view)
    {
        startActivity(new Intent(EmailVerificationActivity.this,SignInActivity.class));
    }

    public void goToLoginActivity(View view)
    {
        startActivity(new Intent(EmailVerificationActivity.this,LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
    }
}
