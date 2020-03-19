package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class StartingActivity extends AppCompatActivity {

    private static final String TAG = "StartingActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        Log.d(TAG, "onCreate: before timer...");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run: inside the timer");
                if(mAuth.getCurrentUser() != null)
                {
                    Log.d(TAG, "run: user is not null");
                    Intent intent = new Intent(StartingActivity.this, HomeScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.d(TAG, "run: user is null");
                    Intent intent = new Intent(StartingActivity.this, EmailVerificationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
        Log.d(TAG, "onCreate: after timer...");
    }
}
