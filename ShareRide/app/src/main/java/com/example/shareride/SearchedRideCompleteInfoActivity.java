package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchedRideCompleteInfoActivity extends AppCompatActivity {

    private static final String TAG = "SearchedRideCompleteInf";
    private SearchRideResultDetails searchRideResultDetails;
    private UserDetails riderDetails;
    private TextView riderNameTV, riderGenderTV, riderAgeTV, riderCityTV;
    private TextView sourceLocationTV, destinationLocationTV, availabelSeatsTV, costPerSeatTV;
    private TextView carNameTV, carModelValueTV, carFuelTV, carAirConditionerTV, carVehicleNumberTV;
    private Button requestBtn;
    private CircleImageView riderProfilePicture, riderCarPhoto;
    private String carID;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private boolean requestFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_ride_complete_info);
        getWindow().setBackgroundDrawableResource(R.drawable.background5);
        getIntentData();
        initializeFirebaseInstance();
        initializeWidgets();
        getCarDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();
        populatingWidgets();
    }

    private void getIntentData()
    {
        Log.d(TAG, "getIntentData: getting intent data.");
        searchRideResultDetails = getIntent().getParcelableExtra("Ride_Details");
        riderDetails = getIntent().getParcelableExtra("Rider_Details");
        carID = searchRideResultDetails.getCar_id();
        requestFlag = getIntent().getBooleanExtra("Request_Flag",false);
    }
    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing Widgets.");
        riderNameTV = (TextView) findViewById(R.id.rider_name_textview);
        riderGenderTV = (TextView) findViewById(R.id.rider_gender_textview);
        riderAgeTV = (TextView) findViewById(R.id.rider_age_textview);
        riderCityTV = (TextView) findViewById(R.id.rider_city_value_textview);
        sourceLocationTV = (TextView) findViewById(R.id.ride_source_location_textview);
        destinationLocationTV = (TextView) findViewById(R.id.ride_destination_location_textview);
        availabelSeatsTV = (TextView) findViewById(R.id.ride_available_seat_value_textview);
        costPerSeatTV = (TextView) findViewById(R.id.ride_cost_per_seat_textview);
        carNameTV = (TextView) findViewById(R.id.rider_car_name_textview);
        carModelValueTV = (TextView) findViewById(R.id.rider_car_model_value_textview);
        carFuelTV = (TextView) findViewById(R.id.rider_car_fuel_value_textview);
        carAirConditionerTV = (TextView) findViewById(R.id.rider_car_air_conditioner_textview);
        carVehicleNumberTV = (TextView) findViewById(R.id.rider_car_vehicle_number_value_textview);
        riderProfilePicture = (CircleImageView) findViewById(R.id.rider_photo);
        riderCarPhoto = (CircleImageView) findViewById(R.id.rider_car_photo);
        requestBtn = (Button) findViewById(R.id.request_button);
    }
    private void initializeFirebaseInstance()
    {
        Log.d(TAG, "initializeFirebaseInstance: initializing firebase instance.");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }
    private void getCarDetails()
    {
        Log.d(TAG, "getCarDetails: getting car details.");

        try
        {
            Log.d(TAG, "getCarDetails: userID: "+carID);
            DatabaseReference mChildDB = databaseReference.child("Cars").child(riderDetails.getUserID()).child(carID);
            mChildDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try
                    {
                        carNameTV.setText(dataSnapshot.child("Car_Name").getValue().toString());
                        carAirConditionerTV.setText(dataSnapshot.child("Air_Conditioner").getValue().toString());
                        carFuelTV.setText(dataSnapshot.child("Fuel").getValue().toString());
                        carModelValueTV.setText(dataSnapshot.child("Model_Year").getValue().toString());
                        carVehicleNumberTV.setText(dataSnapshot.child("Vehicle_Number").getValue().toString());
                        Picasso.get().load(dataSnapshot.child("Car_Image").getValue().toString()).into(riderCarPhoto);
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "onDataChange: Exception: "+e.getLocalizedMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            Log.d(TAG, "getCarDetails: Exception: "+e.getLocalizedMessage());
        }
    }
    private void populatingWidgets()
    {
        Log.d(TAG, "populatingWidgets: populating widgets.");
        String firstName = riderDetails.getFirstName();
        String lastName = riderDetails.getLastName();
        String fullName = firstName + " " + lastName;
        riderNameTV.setText(fullName);
        riderCityTV.setText(riderDetails.getCity());
        riderGenderTV.setText(riderDetails.getGender());
        sourceLocationTV.setText(searchRideResultDetails.getSource_Location_Name());
        destinationLocationTV.setText(searchRideResultDetails.getDestination_Location_Name());
        Picasso.get().load(riderDetails.getProfilePicture()).into(riderProfilePicture);
        // this value will change on the basis of the passenger of the ride... so i will write the code for that later.
        availabelSeatsTV.setText(searchRideResultDetails.getNum_Seats());
        costPerSeatTV.setText(searchRideResultDetails.getCost_Per_Seat());
        if(requestFlag)
        {
            requestBtn.setText("Requested");
        }
        else
        {
            requestRide(searchRideResultDetails);
        }
    }
    private void requestRide(SearchRideResultDetails searchRideResultDetails)
    {
        Log.d(TAG, "requestRide: requesting ride.");
        DatabaseReference mChildDB = databaseReference.child("Registration").child(mAuth.getUid()).push();
        mChildDB.child("Offer_Ride_ID").setValue(searchRideResultDetails.getRideID());
        mChildDB.child("Status").setValue("Not Accepted");
    }
}
