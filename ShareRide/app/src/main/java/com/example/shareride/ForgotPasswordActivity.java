package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";

    private FirebaseAuth mAuth;
    private EditText emailET;
    private String email;

    public void send(View view)
    {
        if(validateFields())
        {
            sendEmail();
        }
        else
        {
            Toast.makeText(this, "Required field is empty.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getWindow().setBackgroundDrawableResource(R.drawable.background5);

        initializeWidgets();
        initializeFirebaseInstances();
    }
    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets");
        emailET = findViewById(R.id.email_edittext);
    }
    private void initializeFirebaseInstances()
    {
        Log.d(TAG, "initializeFirebaseInstances: initializing firebase instances.");
        mAuth = FirebaseAuth.getInstance();
    }
    private boolean validateFields()
    {
        email = emailET.getText().toString();
        return !TextUtils.isEmpty(email);
    }
    private void sendEmail()
    {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Email send.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send email", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPasswordActivity.this, "Error in sending email.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: Exception: "+e.getLocalizedMessage());
            }
        });
    }
}
