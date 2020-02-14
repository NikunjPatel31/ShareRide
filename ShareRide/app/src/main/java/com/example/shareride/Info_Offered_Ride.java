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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Info_Offered_Ride extends AppCompatActivity {

    private static final String TAG = "Info_Offered_Ride";

    private TextView sourceLocationTV, destinationLocationTV, availableSeatsValueTV, costPerSeatTV
            , dateValueTV, timeValueTV;

    private FirebaseAuth mAuth;
    private DatabaseReference mdatabaseReference;
    private String UID, ride_id;

    private String sourceLocation, destinationLocation, availableSeats, costPerSeats, date, time;

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
    }

    private void initializeFirebaseInstances()
    {
        Log.d(TAG, "initializeFirebaseInstances: initializing firebase instances.");
        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getUid();
        ride_id = getIntent().getStringExtra("Ride_id");
        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Offer_Ride").child(UID).child(ride_id);
        Log.d(TAG, "initializeFirebaseInstances: UID: "+UID);
    }

    private void getRideDetailsFromDatabase()
    {
        DatabaseReference mChildDB = mdatabaseReference;
        mChildDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tem = dataSnapshot.child("Source_Location").getValue().toString();
                sourceLocation = geocode(getApplicationContext(), tem);
                tem = dataSnapshot.child("Destination_Location").getValue().toString();
                destinationLocation = geocode(getApplicationContext(), tem);
                availableSeats = dataSnapshot.child("Num_Seats").getValue().toString();
                costPerSeats = dataSnapshot.child("Cost_Per_Seat").getValue().toString();
                date = dataSnapshot.child("Date").getValue().toString();
                time = dataSnapshot.child("Time").getValue().toString();
                populateWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void populateWidgets()
    {
        Log.d(TAG, "populateWidgets: populating widgets.");
        sourceLocationTV.setText(sourceLocation);
        destinationLocationTV.setText(destinationLocation);
        availableSeatsValueTV.setText(availableSeats);
        costPerSeatTV.setText(costPerSeats);
        dateValueTV.setText(date);
        timeValueTV.setText(time);
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
