package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class riderNotificationPassengerCompleteInfo extends AppCompatActivity {
    private static final String TAG = "riderNotificationPassen";
    private TextView genderTV, ageTV, contactTV, cityTV;
    private ImageView passengerIV;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private UserDetails passengerDetails;
    private final int CALL_PHONE_REQUEST_CODE = 4;
    private Boolean callPermission = false;

    public void call(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + passengerDetails.getContact()));
        if(callPermission)
        {
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Call Permission is not given.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_notification_passenger_complete_info);
        getIntentData();
        initializeWidgets();
        getCallPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateWidgets();
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        collapsingToolbarLayout = findViewById(R.id.collasping_toolbar);
        genderTV = findViewById(R.id.gender_value_textview);
        ageTV = findViewById(R.id.age_value_textview);
        contactTV = findViewById(R.id.contact_value_textview);
        cityTV = findViewById(R.id.city_value_textview);
        passengerIV = findViewById(R.id.passenger_imageView);
    }
    private void getIntentData()
    {
        Log.d(TAG, "getIntentData: getting intent data.");
        passengerDetails = getIntent().getParcelableExtra("Passenger_details");
    }
    private void populateWidgets()
    {
        Log.d(TAG, "populateWidgets: populating widgets.");
        genderTV.setText(passengerDetails.getGender());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int DOB = Integer.parseInt(passengerDetails.getDOB());
        Log.d(TAG, "populateWidgets: year: "+year);
        Log.d(TAG, "populateWidgets: DOB: "+DOB);
        ageTV.setText(Integer.toString(year - DOB));
        contactTV.setText(passengerDetails.getContact());
        cityTV.setText(passengerDetails.getCity());
        String firstName = passengerDetails.getFirstName();
        String lastName = passengerDetails.getLastName();
        String fullName = firstName + " " + lastName;
        collapsingToolbarLayout.setTitle(fullName);
        Picasso.get().load(passengerDetails.getProfilePicture()).placeholder(R.drawable.ic_account_circle_black_24dp).into(passengerIV);
    }
    private void getCallPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST_CODE);
        }
        else
        {
            callPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            callPermission = true;
        }
        else
        {
            callPermission = false;
        }
    }
}
