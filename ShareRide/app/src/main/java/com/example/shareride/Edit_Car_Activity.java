package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

public class Edit_Car_Activity extends AppCompatActivity {

    private static final String TAG = "Edit_Car_Activity";

    private EditText carNameET, modelYearET, vechileNumberET;
    private Spinner airConditionerSpinner, fuelSpinner;
    private TextView airConditionerTV, fuelTV, editCarTitleTV;
    private CircleImageView carIV;
    private ScrollView scrollViewRoot;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri = null,uploadUri = null;

    private ArrayList<String> airConditionerList, fuelList;
    private ArrayAdapter<String> airConfitionerAdapter, fuelAdapter;

    private int GALLERY_REQUEST_CODE = 1;
    private String UID, CAR_ID;

    private String carName, modelYear, vehicleNumber, airConditioner, fuel, carImage;

    public void apply(View view)
    {
        if(CommanClass.isNetworkAvailable(this))
        {
            if(fieldsValidation())
            {
                sendCarData();
                Intent intent = new Intent(Edit_Car_Activity.this, View_My_Cars_Activity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finish();
            }
        }
        else
        {
            Snackbar snackbar = Snackbar
                    .make(scrollViewRoot, "No internet is available", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void cancel(View view)
    {
        Intent intent = new Intent(Edit_Car_Activity.this, View_My_Cars_Activity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__car_);
        getWindow().setBackgroundDrawableResource(R.drawable.background5);
        initializWidgets();
        animateWidgets();
        initailizeFirebaseInstances();
        spinnerArrayAdapter();
        spinnerListener();
        imageSelect();
        getCarDetails();
    }

    private void initializWidgets()
    {
        Log.d(TAG, "initializWidgets: initializing widgets.");
        airConditionerSpinner = (Spinner) findViewById(R.id.air_conditioner_spinner);
        fuelSpinner = (Spinner) findViewById(R.id.fuel_spinner);
        airConditionerTV = (TextView) findViewById(R.id.air_conditioner_textview);
        fuelTV = (TextView) findViewById(R.id.fuel_textview);
        carIV = (CircleImageView) findViewById(R.id.car_image);
        carNameET = (EditText) findViewById(R.id.car_name_edittext);
        modelYearET = (EditText) findViewById(R.id.model_year_edittext);
        vechileNumberET = (EditText) findViewById(R.id.vehicle_number_edittext);
        editCarTitleTV = (TextView) findViewById(R.id.edit_car_in_title_textview);
        scrollViewRoot = findViewById(R.id.scroll_view_root);
    }

    private void animateWidgets()
    {
        Log.d(TAG, "animateWidgets: animating widgets.");
        airConditionerSpinner.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        airConditionerTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        fuelSpinner.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        fuelTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        carIV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        carNameET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        modelYearET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        vechileNumberET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        editCarTitleTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_image));
    }

    private void initailizeFirebaseInstances()
    {
        Log.d(TAG, "initailizeFirebaseInstances: initializing firebase instances.");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        UID = mAuth.getUid();
        CAR_ID = getIntent().getStringExtra("Car_id");
    }

    private void spinnerArrayAdapter()
    {
        airConditionerList = new ArrayList<>();
        airConditionerList.add("AC");
        airConditionerList.add("NON - AC");
        airConfitionerAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,airConditionerList);
        airConditionerSpinner.setAdapter(airConfitionerAdapter);

        fuelList = new ArrayList<>();
        //adding data in ArrayList for fuel spinner.
        fuelList.add("Petrol");
        fuelList.add("Diesel");
        fuelList.add("CNG");
        fuelAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,fuelList);
        fuelSpinner.setAdapter(fuelAdapter);
    }

    private void spinnerListener()
    {
        airConditionerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                airConditionerTV.setText(airConditionerSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fuelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: Item selected: "+fuelSpinner.getSelectedItem());
                fuelTV.setText(fuelSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void imageSelect()
    {
        carIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Log.d(TAG, "onClick: profile image clicked.");
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent,GALLERY_REQUEST_CODE);

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
                carIV.setImageURI(resultUri);
                Log.d(TAG, "onActivityResult: sending selected image to database.");


                uploadUri = resultUri;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void getCarDetails()
    {
        Log.d(TAG, "getCarDetails: getting car details.");
        DatabaseReference mChildDB = databaseReference.child("Cars").child(UID).child(CAR_ID);
        Log.d(TAG, "getCarDetails: car id: "+CAR_ID);

        mChildDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carName = dataSnapshot.child("Car_Name").getValue().toString();
                modelYear = dataSnapshot.child("Model_Year").getValue().toString();
                vehicleNumber = dataSnapshot.child("Vehicle_Number").getValue().toString();
                airConditioner = dataSnapshot.child("Air_Conditioner").getValue().toString();
                fuel = dataSnapshot.child("Fuel").getValue().toString();
                carImage = dataSnapshot.child("Car_Image").getValue().toString();
                uploadUri = Uri.parse(carImage);
                populatingFields();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populatingFields()
    {
        Log.d(TAG, "populatingFields: populating fields.");
        carNameET.setText(carName);
        modelYearET.setText(modelYear);
        int position = airConfitionerAdapter.getPosition(airConditioner);
        airConditionerSpinner.setSelection(position);
        position = fuelAdapter.getPosition(fuel);
        fuelSpinner.setSelection(position);
        vechileNumberET.setText(vehicleNumber);
        Picasso.get().load(Uri.parse(carImage)).into(carIV);
    }

    private boolean fieldsValidation()
    {
        carName = carNameET.getText().toString();
        modelYear = modelYearET.getText().toString();
        vehicleNumber = vechileNumberET.getText().toString();

        if(TextUtils.isEmpty(carName)
                && TextUtils.isEmpty(modelYear)
                && TextUtils.isEmpty(vehicleNumber))
        {
            Toast.makeText(this, "Fields are empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if(uploadUri == null)
            {
                Toast.makeText(this, "Please select car image.", Toast.LENGTH_SHORT).show();
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    private void sendCarData()
    {
        Log.d(TAG, "sendUserData: sending user details to database.");
        String UID = mAuth.getUid();
        final DatabaseReference mChildDB = databaseReference.child("Cars").child(UID).child(CAR_ID);
        mChildDB.child("Car_Name").setValue(carName);
        mChildDB.child("Model_Year").setValue(modelYear);
        mChildDB.child("Vehicle_Number").setValue(vehicleNumber);
        if(airConditionerSpinner.getSelectedItem().equals("AC"))
        {
            mChildDB.child("Air_Conditioner").setValue("AC");
        }
        else
        {
            mChildDB.child("Air_Conditioner").setValue("NON - AC");
        }
        if(fuelSpinner.getSelectedItem().equals("Petrol"))
        {
            mChildDB.child("Fuel").setValue("Petrol");
        }
        else if(fuelSpinner.getSelectedItem().equals("Diesel"))
        {
            mChildDB.child("Fuel").setValue("Diesel");
        }
        else
        {
            mChildDB.child("Fuel").setValue("CNG");
        }
        DatabaseReference d = mChildDB;
        if(uploadUri != null)
        {
            StorageReference mChildST = storageReference.child("Car_Image").child(UID).child(d.getKey());
            mChildST.putFile(uploadUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String path = uri.toString();

                            Log.d(TAG, "sendUserData: sending car image uri URI: "+path);
                            mChildDB.child("Car_Image").setValue(path);
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
            mChildDB.child("Car_Image").setValue("null");
        }

    }
}
