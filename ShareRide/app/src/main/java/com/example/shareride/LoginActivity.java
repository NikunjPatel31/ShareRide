package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText emailET, passwordET;
    private FirebaseAuth mAtuh;
    private Button loginBtn;
    private ProgressBar progressBar;
    private TextView forgotPasswordTV;
    private ConstraintLayout rootLayout;

    private String email, password;

    public void login(View view)
    {
        Log.d(TAG, "login: loging in user.");
        if(CommanClass.isNetworkAvailable(this))
        {
            loginBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            if(fieldsValidation())
            {
                signInUser();
            }
            else
            {
                progressBar.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            Snackbar snackbar = Snackbar
                    .make(rootLayout, "No internet is available", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
    public void goToSignInActivity(View view)
    {
        startActivity(new Intent(LoginActivity.this,EmailVerificationActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeWidgets();
        initializeFirebaseInstance();
        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });
    }

    private void initializeFirebaseInstance()
    {
        mAtuh = FirebaseAuth.getInstance();
    }

    private void initializeWidgets()
    {
        emailET = (EditText) findViewById(R.id.email_edittext);
        passwordET = (EditText) findViewById(R.id.password_edittext);
        loginBtn = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        forgotPasswordTV = findViewById(R.id.forgot_password_textView);
        rootLayout = findViewById(R.id.constraint_layout_root);
    }

    private boolean fieldsValidation()
    {
        Log.d(TAG, "fieldsValidation: validating the fields.");
        email = emailET.getText().toString();
        password = passwordET.getText().toString();

        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Fields are empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return true;
        }
    }

    private void signInUser()
    {
        Log.d(TAG, "signInUser: siging in user.");
        mAtuh.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Log.d(TAG, "onComplete: user successfully signed in.");
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onComplete: error in signing in user. taskException: "+task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                Log.d(TAG, "onFailure: error in signing user. Exception: "+e.getLocalizedMessage());
                if(e instanceof FirebaseAuthInvalidCredentialsException)
                {
                    Toast.makeText(LoginActivity.this, "Entered Password is incorrect.", Toast.LENGTH_SHORT).show();
                }
                else if (e instanceof FirebaseAuthInvalidUserException)
                {
                    String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

                    if (errorCode.equals("ERROR_USER_NOT_FOUND"))
                    {
                        Toast.makeText(LoginActivity.this, "No Account found.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
