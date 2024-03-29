package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class EmailVerificationActivity extends AppCompatActivity {

    private static final String TAG = "EmailVerificationActivi";

    private EditText emailET, passwordET, confirmPasswordET;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;
    private ConstraintLayout rootLayout;

    private FirebaseAuth mAuth;

    private String email, password, confirmPassword;

    public void goToSignInActivity(View view)
    {
        Log.d(TAG, "goToSignInActivity: ");
        if(!CommanClass.isNetworkAvailable(this))
        {
            Snackbar snackbar = Snackbar
                    .make(rootLayout, "No internet is available", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else
        {
            if(fieldsValidation())
            {
                signInUser();
            }
        }
    }

    public void goToLoginActivity(View view)
    {
        startActivity(new Intent(EmailVerificationActivity.this,LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        initializeWidgets();
        progressBar();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        emailET = (EditText) findViewById(R.id.email_edittext);
        passwordET = (EditText) findViewById(R.id.password_edittext);
        confirmPasswordET = (EditText) findViewById(R.id.confirm_password_edittext);
        rootLayout = findViewById(R.id.constraint_layout_root);
    }

    private boolean fieldsValidation()
    {
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        confirmPassword = confirmPasswordET.getText().toString();

        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPassword))
        {
            Log.d(TAG, "fieldsValidation: fields are empty.");
            Toast.makeText(this, "Fields are empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            Log.d(TAG, "fieldsValidation: fields are not empty.");
            if(password.equals(confirmPassword))
            {
                return true;
            }
            else
            {
                Log.d(TAG, "fieldsValidation: confirm Password is not same.");
                Toast.makeText(this, "Confirm Password is not same.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    private void signInUser()
    {
        Log.d(TAG, "signInUser: creating user.");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Log.d(TAG, "onComplete: user created successfully.");
                    sendVerificationEmail();
                }
                else
                {
                    Log.d(TAG, "onComplete: task Exception: "+task.getException());
                    Toast.makeText(EmailVerificationActivity.this, "Error in creating user.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                if (e instanceof FirebaseAuthInvalidUserException) {
                    String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

                    if(errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE"))
                    {
                        Toast.makeText(EmailVerificationActivity.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Log.d(TAG, "onFailure: error code: "+((FirebaseAuthInvalidUserException) e).getErrorCode());
                    }
                }
                else if (e instanceof FirebaseAuthInvalidCredentialsException)
                {
                    String errorCode = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();

                    if(errorCode.equals("ERROR_INVALID_EMAIL"))
                    {
                        Toast.makeText(EmailVerificationActivity.this, "Incorrect email.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (e instanceof FirebaseAuthUserCollisionException)
                {
                    String errorCode = ((FirebaseAuthUserCollisionException) e).getErrorCode();
                    if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE"))
                    {
                        Toast.makeText(EmailVerificationActivity.this, "Email address is already in use.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void sendVerificationEmail()
    {
        try
        {
            Log.d(TAG, "goToSignInActivity: sending verification email.");
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Log.d(TAG, "onComplete: Email successfully send to user.");
                        Toast.makeText(EmailVerificationActivity.this, "Email send.", Toast.LENGTH_SHORT).show();
                        dialog();
                    }
                    else
                    {
                        Log.d(TAG, "onComplete: task Exception: "+task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Exception: "+e.getLocalizedMessage());
                }
            });
        }
        catch (Exception e)
        {
            Log.d(TAG, "goToSignInActivity: Exception: "+e.getLocalizedMessage());
        }
    }

    private void dialog()
    {
        progressDialog.dismiss();
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Email Verification")
                .setMessage("We sent you a verification email. Please verify email before moving further.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: button clicked on alert dialog.");
                        Intent intent = new Intent(EmailVerificationActivity.this, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        alertDialog.show();
    }

    private void progressBar()
    {
        progressDialog = new ProgressDialog(this,R.style.CustomProgressDialog);
        progressDialog.setMessage("Creating account.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

}
