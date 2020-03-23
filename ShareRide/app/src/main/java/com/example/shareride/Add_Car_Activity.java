package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_Car_Activity extends AppCompatActivity {

    private static final String TAG = "Add_Car_Activity";

    private EditText carNameET, modelYearET, vechileNumberET;
    private Spinner airConditionerSpinner, fuelSpinner;
    private TextView airConditionerTV, fuelTV, addCarTitleTV, addCarDescriptionTV;
    private CircleImageView carIV;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri,uploadUri;
    private String UID;

    private String carName, modelYear, vechicleNumber;

    private ArrayList<String> airConditionerList, fuelList;
    private ArrayAdapter<String> airConfitionerAdapter, fuelAdapter;

    private int GALLERY_REQUEST_CODE = 1, READ_EXTERNAL_STORAGE_REQUSET_CODE = 2;
    private boolean storagePermission = false;
    private String car_id="";
    private LatLng sourceLocation, destinationLocation;
    private String time, date;

    public void add(View view)
    {
        if(fieldsValidation())
        {
            Log.d(TAG, "apply: fields validation returned true.");
            sendCarData();
            Intent intent = new Intent(Add_Car_Activity.this, Account_Activity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //finish();
        }
        else
        {
            Log.d(TAG, "apply: fields validation returned false.");
        }
    }


    public void cancel(View view)
    {
        Intent intent = new Intent(Add_Car_Activity.this, Account_Activity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__car_);
        getWindow().setBackgroundDrawableResource(R.drawable.background5);

        storagePermissions();
        initializWidgets();
        animateWidgets();
        initailizeFirebaseInstances();
        spinnerArrayAdapter();
        spinnerListener();
        imageSelect();
    }

    private void initializWidgets()
    {
        Log.d(TAG, "initializWidgets: initializing widgets.");
        airConditionerSpinner = (Spinner) findViewById(R.id.air_conditioner_spinner);
        fuelSpinner = (Spinner) findViewById(R.id.fuel_spinner);
        airConditionerTV = (TextView) findViewById(R.id.air_conditioner_textview);
        fuelTV = (TextView) findViewById(R.id.fuel_textview);
        carIV = (CircleImageView) findViewById(R.id.car_image);
        carNameET = (EditText) findViewById(R.id.car_name_textview);
        modelYearET = (EditText) findViewById(R.id.model_year_edittext);
        vechileNumberET = (EditText) findViewById(R.id.vehicle_number_edittext);
        addCarTitleTV = (TextView) findViewById(R.id.add_car_title_textview);
        addCarDescriptionTV = (TextView) findViewById(R.id.add_car_description_textview);
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
        addCarTitleTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_image));
        addCarDescriptionTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_image));
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

    private void imageSelect()
    {

        carIV.setOnClickListener(new View.OnClickListener() {
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
                carIV.setImageURI(resultUri);
                Log.d(TAG, "onActivityResult: sending selected image to database.");


                uploadUri = resultUri;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private boolean fieldsValidation()
    {
        carName = carNameET.getText().toString();
        modelYear = modelYearET.getText().toString();
        vechicleNumber = vechileNumberET.getText().toString();

        if(TextUtils.isEmpty(carName)
                && TextUtils.isEmpty(modelYear)
                && TextUtils.isEmpty(vechicleNumber))
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
        UID = mAuth.getUid();
        Log.d(TAG, "sendCarData: UId: "+UID);
        final DatabaseReference mChildDB = databaseReference.child("Cars").child(UID).push();
        try
        {
            Log.d(TAG, "sendCarData: key: "+mChildDB.getKey());
            car_id = mChildDB.getKey();
        }
        catch (Exception e)
        {
            Log.d(TAG, "sendCarData: Key Exception: "+e.getLocalizedMessage());
        }
        mChildDB.child("Car_Name").setValue(carName);
        mChildDB.child("Model_Year").setValue(modelYear);
        mChildDB.child("Vehicle_Number").setValue(vechicleNumber);
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

    public void apply(View view) {
    }
}
