package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;
    private CircleImageView profilePicIV;
    private EditText firstNameET, lastNameET, pincodeET, cityET, contactET, yearOfBirthET;
    private Spinner genderSpinner;
    private TextView genderSpinnerTV;
    private ArrayList<String> genderArrayList;

    private String firstName, lastName, pincode, city, contact, yearOfBirth;

    public void apply(View view)
    {
        Log.d(TAG, "apply: button pressed.");
        Toast.makeText(this, "Button pressed", Toast.LENGTH_SHORT).show();
        if(fieldsValidation())
        {
            if(EmailVerifed())
            {
                sendUserData();
                Toast.makeText(SignInActivity.this, "Sending data.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignInActivity.this, HomeScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else
            {
                dialog();
            }
        }
        else
        {
            Log.d(TAG, "apply: some fieled are missing.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.background5) ;
        setContentView(R.layout.activity_sign_in);

        initializeFirebaseInstance();
        initializeWidgets();
        spinnerArrayAdapter();
        spinnerListener();
    }

    private void initializeFirebaseInstance()
    {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void initializeWidgets()
    {
        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        genderSpinnerTV = (TextView) findViewById(R.id.gender_textview);
        yearOfBirthET = (EditText) findViewById(R.id.year_of_birth_edittext);
        profilePicIV = (CircleImageView) findViewById(R.id.profile_image);
        firstNameET = (EditText) findViewById(R.id.first_name_edittext);
        lastNameET = (EditText) findViewById(R.id.last_name_edittext);
        pincodeET = (EditText) findViewById(R.id.pincode_edittext);
        cityET = (EditText) findViewById(R.id.city_edittext);
        contactET = (EditText) findViewById(R.id.contact_edittext);
    }

    private void spinnerArrayAdapter()
    {
        genderArrayList = new ArrayList<>();
        genderArrayList.add("Male");
        genderArrayList.add("Female");
        ArrayAdapter<String> genderArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,genderArrayList);
        genderSpinner.setAdapter(genderArrayAdapter);
    }

    private void spinnerListener()
    {
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderSpinnerTV.setText(genderSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean fieldsValidation()
    {
        firstName = firstNameET.getText().toString();
        lastName = lastNameET.getText().toString();
        pincode = pincodeET.getText().toString();
        city = cityET.getText().toString();
        contact = contactET.getText().toString();
        yearOfBirth = yearOfBirthET.getText().toString();

        if(TextUtils.isEmpty(firstName)
                && TextUtils.isEmpty(lastName)
                && TextUtils.isEmpty(pincode)
                && TextUtils.isEmpty(city)
                && TextUtils.isEmpty(contact)
                && TextUtils.isEmpty(yearOfBirth))
        {
            Toast.makeText(this, "Fields are empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if(contact.length() == 10)
            {
                Log.d(TAG, "fieldsValidation: all fielda are validated.");
                return true;
            }
            else
            {
                Toast.makeText(this, "Contact number is not valid.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    private void sendUserData()
    {
        Log.d(TAG, "sendUserData: sending user details to database.");
        String UID = mAuth.getUid();
        DatabaseReference mChildDB = databaseReference.child("Users").child(UID);
        mChildDB.child("First Name").setValue(firstName);
        mChildDB.child("Last Name").setValue(lastName);
        mChildDB.child("DOB").setValue(yearOfBirth);
        mChildDB.child("Contact").setValue(contact);
        mChildDB.child("City").setValue(city);
        mChildDB.child("Pincode").setValue(pincode);
    }

    private boolean EmailVerifed()
    {
        mAuth.getCurrentUser().reload();
        if(mAuth.getCurrentUser().isEmailVerified())
        {
            return true;
        }
        else
        {
            mAuth.getCurrentUser().reload();
            if(mAuth.getCurrentUser().isEmailVerified())
            {
                Log.d(TAG, "EmailVerifed: Email is verified.");
                return true;
            }
            else
            {
                Log.d(TAG, "EmailVerifed: Email is not verified.");
                Toast.makeText(this, "Email is not verified.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    private void dialog()
    {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Email Verification")
                .setMessage("Email is not verified. Please verify it.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: button clicked on alert dialog.");

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

    private void deleteUser()
    {
        if(mAuth.getCurrentUser() != null)
        {
            Log.d(TAG, "deleteUser: deleting the user.");
            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Log.d(TAG, "onComplete: user deleted successfully.");
                    }
                    else
                    {
                        Log.d(TAG, "onComplete: unsuccessful to delete user.");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: user deletion failure Exception: "+e.getLocalizedMessage());
                }
            });
        }
        else
        {
            Log.d(TAG, "deleteUser: there is not user.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        deleteUser();
    }
}
