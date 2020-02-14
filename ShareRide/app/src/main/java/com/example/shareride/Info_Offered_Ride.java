package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Info_Offered_Ride extends AppCompatActivity {

    private static final String TAG = "Info_Offered_Ride";

    private TextView sourceLocationTV, destinationLocationTV, availableSeatsValueTV, costPerSeatTV
            , dateValueTV, timeValueTV;
    private TextView carNameTV, modelTV, fuelTV, airConditionerTV, vechileNumberTV;
    private CircleImageView carImageCV;

    private FirebaseAuth mAuth;
    private DatabaseReference mdatabaseReference;
    private StorageReference mStorageReference;
    private String UID, ride_id, car_id;

    private String sourceLocation, destinationLocation, availableSeats, costPerSeats, date, time;
    private String carName, model, fuel, airConditioner, vechileNumber, carImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info__offered__ride);
        getWindow().setBackgroundDrawableResource(R.drawable.background5);

        initializeWidgets();
        initializeFirebaseInstances();
        getRideDetailsFromDatabase();
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        sourceLocationTV = (TextView) findViewById(R.id.source_location_textview);
        destinationLocationTV = (TextView) findViewById(R.id.destination_location_textview);
        availableSeatsValueTV = (TextView) findViewById(R.id.available_seats_value_textview);
        costPerSeatTV = (TextView) findViewById(R.id.cost_per_seat_value_textview);
        dateValueTV = (TextView) findViewById(R.id.date_value_textview);
        timeValueTV = (TextView) findViewById(R.id.time_value_textview);
        carNameTV = (TextView) findViewById(R.id.car_name_textview);
        modelTV = (TextView) findViewById(R.id.model_year_textview);
        fuelTV = (TextView) findViewById(R.id.fuel_textview);
        airConditionerTV = (TextView) findViewById(R.id.air_conditioner_textview);
        vechileNumberTV = (TextView) findViewById(R.id.vehicle_number_textview);
        carImageCV = (CircleImageView) findViewById(R.id.car_image);
    }

    private void initializeFirebaseInstances()
    {
        Log.d(TAG, "initializeFirebaseInstances: initializing firebase instances.");
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getUid();
        ride_id = getIntent().getStringExtra("Ride_id");
        mdatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        Log.d(TAG, "initializeFirebaseInstances: UID: "+UID);
    }

    private void getRideDetailsFromDatabase()
    {
        DatabaseReference mChildDB = mdatabaseReference.child("Offer_Ride").child(UID).child(ride_id);;
        mChildDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getDataForCardview1(dataSnapshot);
                getDataForCardview2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDataForCardview1(DataSnapshot dataSnapshot)
    {
        Log.d(TAG, "getDataForCardview1: getting data for cardview 1.");
        String tem = dataSnapshot.child("Source_Location").getValue().toString();
        sourceLocation = geocode(getApplicationContext(), tem);
        tem = dataSnapshot.child("Destination_Location").getValue().toString();
        destinationLocation = geocode(getApplicationContext(), tem);
        availableSeats = dataSnapshot.child("Num_Seats").getValue().toString();
        costPerSeats = dataSnapshot.child("Cost_Per_Seat").getValue().toString();
        date = dataSnapshot.child("Date").getValue().toString();
        time = dataSnapshot.child("Time").getValue().toString();
        car_id = dataSnapshot.child("Car_id").getValue().toString();
        populateWidgetsForCardview1();
    }

    private void getDataForCardview2()
    {
        Log.d(TAG, "getDataForCardview2: getting data for cardview 2.");
        DatabaseReference mChildDB = mdatabaseReference.child("Cars").child(UID).child(car_id);
        mChildDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: getting car details for the ride.");
                carName = dataSnapshot.child("Car_Name").getValue().toString();
                model = dataSnapshot.child("Model_Year").getValue().toString();
                fuel = dataSnapshot.child("Fuel").getValue().toString();
                airConditioner = dataSnapshot.child("Air_Conditioner").getValue().toString();
                vechileNumber = dataSnapshot.child("Vehicle_Number").getValue().toString();
                carImage = dataSnapshot.child("Car_Image").getValue().toString();
                populateWidgetsForCardview2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void populateWidgetsForCardview1()
    {
        Log.d(TAG, "populateWidgetsForCardview1: populating widgets.");
        sourceLocationTV.setText(sourceLocation);
        destinationLocationTV.setText(destinationLocation);
        availableSeatsValueTV.setText(availableSeats);
        costPerSeatTV.setText(costPerSeats);
        dateValueTV.setText(date);
        timeValueTV.setText(time);
    }

    private void populateWidgetsForCardview2()
    {
        Log.d(TAG, "populateWidgetsForCardview2: populating widgets.");
        carNameTV.setText(carName);
        modelTV.setText(model);
        fuelTV.setText(fuel);
        airConditionerTV.setText(airConditioner);
        vechileNumberTV.setText(vechileNumber);
        Picasso.get().load(carImage).into(carImageCV);
    }
    public static String geocode(Context context, String location)
    {
        String locationName = null;
        String[] str = location.split(",");
        Log.d(TAG, "geocode: String1: "+str[0]+" String2: "+str[1]);

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Address address = null;
        try {
            List<Address> list = geocoder.getFromLocation(Double.parseDouble(str[0]), Double.parseDouble(str[1]),1);

            if(list.size() > 0)
            {
                address = list.get(0);
                Log.d(TAG, "geocode: sub_admin_Area: "+address.getLocality());
                locationName = address.getLocality();
                return locationName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
