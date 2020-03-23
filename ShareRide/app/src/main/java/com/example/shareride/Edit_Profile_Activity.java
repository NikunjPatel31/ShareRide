package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accounts.Account;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_Profile_Activity extends AppCompatActivity {

    private static final String TAG = "Edit_Profile_Activity";

    private CircleImageView profileImageIV;
    private EditText firstNameET, lastNameET, contactET, yearOfBirthET, cityET, pincodeET;
    private Spinner genderSpinner;
    private TextView genderTV, editProfileTV;
    private ArrayList<String> genderArrayList;
    private ArrayAdapter<String> genderArrayAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String UID;
    private Uri imageUri,uploadUri;

    private String imageUriString, gender;
    private String firstName, lastName, DOB, contact, city, pincode;

    private int GALLERY_REQUEST_CODE = 1, READ_EXTERNAL_STORAGE_REQUSET_CODE = 2;
    private boolean storagePermission = false;

    public void apply(View view)
    {
        if(fieldsValidation())
        {
            sendUserData();
            Intent intent = new Intent(Edit_Profile_Activity.this, Account_Activity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //finish();
        }
    }

    public void cancel(View view)
    {
        Intent intent = new Intent(Edit_Profile_Activity.this, Account_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__profile_);
        getWindow().setBackgroundDrawableResource(R.drawable.background5);

        storagePermissions();
        initailizeWidgets();
        animateWidgets();
        initailizeFirebaseInstances();
        spinnerArrayAdapter();
        spinnerListener();
        gettingUserDetails();
        imageSelect();
    }

    private void initailizeWidgets()
    {
        Log.d(TAG, "initailizeWidgets: initializing widgets.");
        profileImageIV = (CircleImageView) findViewById(R.id.profile_image);
        firstNameET = (EditText) findViewById(R.id.first_name_edittext);
        lastNameET = (EditText) findViewById(R.id.last_name_edittext);
        contactET = (EditText) findViewById(R.id.contact_edittext);
        yearOfBirthET = (EditText) findViewById(R.id.year_of_birth_edittext);
        cityET = (EditText) findViewById(R.id.city_edittext);
        pincodeET = (EditText) findViewById(R.id.pincode_edittext);
        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        genderTV = (TextView) findViewById(R.id.gender_textview);
        editProfileTV = (TextView) findViewById(R.id.edit_profile_title_textview);
    }

    private void animateWidgets()
    {
        Log.d(TAG, "animateWidgets: animating widgets.");
        profileImageIV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        firstNameET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        lastNameET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        contactET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        yearOfBirthET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        cityET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        pincodeET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        genderSpinner.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        genderTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        editProfileTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_image));
    }

    private void initailizeFirebaseInstances()
    {
        Log.d(TAG, "initailizeFirebaseInstances: initializing firebase instances.");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        UID = mAuth.getUid();
    }

    private void spinnerArrayAdapter()
    {
        genderArrayList = new ArrayList<>();
        genderArrayList.add("Male");
        genderArrayList.add("Female");
        genderArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,genderArrayList);
        genderSpinner.setAdapter(genderArrayAdapter);
    }

    private void spinnerListener()
    {
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderTV.setText(genderSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void gettingUserDetails()
    {
        DatabaseReference mChildDB = databaseReference.child("Users").child(UID);
        mChildDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: retrieving data from the database.");
                firstName = dataSnapshot.child("First Name").getValue().toString();
                lastName = dataSnapshot.child("Last Name").getValue().toString();
                DOB = dataSnapshot.child("DOB").getValue().toString();
                contact = dataSnapshot.child("Contact").getValue().toString();
                city = dataSnapshot.child("City").getValue().toString();
                pincode = dataSnapshot.child("Pincode").getValue().toString();
                gender = dataSnapshot.child("Gender").getValue().toString();
                if(gender.equals("Male"))
                {
                    genderTV.setText("Male");
                }
                else
                {
                    genderTV.setText("Female");
                }
                imageUriString = dataSnapshot.child("Profile Picture").getValue().toString();
                populatingFields();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populatingFields()
    {
        Log.d(TAG, "populatingFields: population fields.");
        if(!imageUriString.equals("null"))
        {
            Uri uri = Uri.parse(imageUriString);
            Picasso.get().load(uri).into(profileImageIV);
        }
        else
        {
            Log.d(TAG, "populatingFields: there is no image.");
        }
        firstNameET.setText(firstName);
        lastNameET.setText(lastName);
        int position = genderArrayAdapter.getPosition(gender);
        genderSpinner.setSelection(position);
        yearOfBirthET.setText(DOB);
        contactET.setText(contact);
        cityET.setText(city);
        pincodeET.setText(pincode);
    }

    private boolean fieldsValidation()
    {
        firstName = firstNameET.getText().toString();
        lastName = lastNameET.getText().toString();
        pincode = pincodeET.getText().toString();
        city = cityET.getText().toString();
        contact = contactET.getText().toString();
        DOB = yearOfBirthET.getText().toString();

        if(TextUtils.isEmpty(firstName)
                && TextUtils.isEmpty(lastName)
                && TextUtils.isEmpty(pincode)
                && TextUtils.isEmpty(city)
                && TextUtils.isEmpty(contact)
                && TextUtils.isEmpty(DOB))
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

    private void imageSelect()
    {

            profileImageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        storagePermission = true;
                    }
                    else
                    {
                        storagePermission = false;
                    }

                    if(storagePermission)
                    {
                        Log.d(TAG, "onClick: profile image clicked.");
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent,GALLERY_REQUEST_CODE);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Storage permissions are disable.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Log.d(TAG, "onActivityResult: image from the device is selected.");
            imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                profileImageIV.setImageURI(resultUri);
                Log.d(TAG, "onActivityResult: sending selected image to database.");


                uploadUri = resultUri;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void sendUserData()
    {
        Log.d(TAG, "sendUserData: sending user details to database.");
        String UID = mAuth.getUid();
        final DatabaseReference mChildDB = databaseReference.child("Users").child(UID);
        mChildDB.child("First Name").setValue(firstName);
        mChildDB.child("Last Name").setValue(lastName);
        mChildDB.child("DOB").setValue(DOB);
        mChildDB.child("Contact").setValue(contact);
        mChildDB.child("City").setValue(city);
        mChildDB.child("Pincode").setValue(pincode);
        if(genderSpinner.getSelectedItem().equals("Male"))
        {
            mChildDB.child("Gender").setValue("Male");
        }
        else
        {
            mChildDB.child("Gender").setValue("Female");
        }
        if(uploadUri != null)
        {
            StorageReference mChildST = storageReference.child("Profile Picture").child(UID);
            mChildST.putFile(uploadUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String path = uri.toString();

                            Log.d(TAG, "sendUserData: sending profile uri URI: "+path);
                            mChildDB.child("Profile Picture").setValue(path);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Exception: "+e.getLocalizedMessage());
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Exception: "+e.getLocalizedMessage());
                }
            });
        }
        else
        {
            Log.d(TAG, "sendUserData: upload uri is empty.");
            mChildDB.child("Profile Picture").setValue("null");
        }
        Log.d(TAG, "sendUserData: image uri send successfully.");
    }

    private void storagePermissions()
    {
        Log.d(TAG, "storagePermissions: requesting storage permission.");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE_REQUSET_CODE);
        }
        else
        {
            storagePermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if(requestCode == READ_EXTERNAL_STORAGE_REQUSET_CODE)
            {
                Log.d(TAG, "onRequestPermissionsResult: storage permission are given.");
                storagePermission = true;
            }
        }
    }
}
