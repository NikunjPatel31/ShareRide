package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Offer_ride_second_Activity extends AppCompatActivity {

    private static final String TAG = "Offer_ride_second_Activ";

    private TextView numOfSeats, seatTitleTV, offerRideTitleTV, offerRideDescTV;
    private EditText costPerSeatET;
    private FloatingActionButton decreaseFAB, increaseFAB;
    private ConstraintLayout rootLayout;

    private int counter = 0;
    private LatLng sourceLocation, destinationLocation;
    private String car_id, time, date;
    private String costPerSeat, totalSeats;

    private FirebaseAuth mAtuh;
    private DatabaseReference mdatabaseReference;
    private String UID;

    public void offer(View view)
    {
        if(CommanClass.isNetworkAvailable(this))
        {
            if(validatingFields())
            {
                Log.d(TAG, "offer: fields are validated.");
                sendingDataToDatabase();
            }
        }
        else
        {
            Snackbar snackbar = Snackbar
                    .make(rootLayout, "No internet is available", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void increaseNumber(View view)
    {
        Log.d(TAG, "increaseNumber: incrementing counter");
        counter++;
        numOfSeats.setText(Integer.toString(counter));
    }

    public void decreaseNumber(View view)
    {
        Log.d(TAG, "decreaseNumber: decrementing counter");
        if(counter > 0)
        {
            counter--;
        }
        numOfSeats.setText(Integer.toString(counter));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride_second_);

        gettingIntentData();
        initializeWidgets();
        animateWidgets();
        intializeFirebaseInstances();
    }

    private void gettingIntentData()
    {
        Log.d(TAG, "gettingIntentData: getting intent data.");
        sourceLocation = getIntent().getExtras().getParcelable("Source Location");
        destinationLocation = getIntent().getExtras().getParcelable("Destination Location");
        car_id = getIntent().getStringExtra("Car_id");
        time = getIntent().getStringExtra("Time");
        date = getIntent().getStringExtra("Date");
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets");
        numOfSeats = (TextView) findViewById(R.id.number_of_seats_textview);
        costPerSeatET = (EditText) findViewById(R.id.cost_per_seat_edittext);
        offerRideTitleTV = (TextView) findViewById(R.id.offer_ride_title_textview);
        offerRideDescTV = (TextView) findViewById(R.id.offer_ride_description_textview);
        seatTitleTV = (TextView) findViewById(R.id.seat_title_textview);
        decreaseFAB = (FloatingActionButton) findViewById(R.id.decrease_number_FAB);
        increaseFAB = (FloatingActionButton) findViewById(R.id.increase_number_FAB);
        rootLayout = findViewById(R.id.root_layout);
    }

    private void animateWidgets()
    {
        Log.d(TAG, "animateWidgets: animating widgets.");
        offerRideTitleTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_image));
        offerRideDescTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_image));
        seatTitleTV.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        decreaseFAB.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        numOfSeats.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        increaseFAB.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        costPerSeatET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
    }

    private void intializeFirebaseInstances()
    {
        Log.d(TAG, "intializeFirebaseInstances: initializing firebase instances. ");
        mAtuh = FirebaseAuth.getInstance();
        UID = mAtuh.getUid();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private boolean validatingFields()
    {
        costPerSeat = costPerSeatET.getText().toString();
        totalSeats = numOfSeats.getText().toString();

        if(TextUtils.isEmpty(costPerSeat))
        {
            Toast.makeText(this, "Field is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if(Integer.parseInt(totalSeats) > 0)
            {
                return true;
            }
            else
            {
                Toast.makeText(this, "Number of seats cannot be zero.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    private void sendingDataToDatabase()
    {
        Log.d(TAG, "sendingDataToDatabase: sending data to database.");
        Log.d(TAG, "sendingDataToDatabase: Source Lat: "+sourceLocation.latitude+" Long: "+sourceLocation.longitude);
        Log.d(TAG, "sendingDataToDatabase: Destination Lat: "+destinationLocation.latitude+" Long: "+destinationLocation.longitude);
        Log.d(TAG, "sendingDataToDatabase: time: "+time);
        Log.d(TAG, "sendingDataToDatabase: date: "+date);
        Log.d(TAG, "sendingDataToDatabase: car_id: "+car_id);
        Log.d(TAG, "sendingDataToDatabase: seat: "+totalSeats);
        Log.d(TAG, "sendingDataToDatabase: cost: "+costPerSeat);
        DatabaseReference mChildDB = mdatabaseReference.child("Offer_Ride").child(UID).push();
        String sourceLocationCord = sourceLocation.latitude + "," + sourceLocation.longitude;
        String destinationLocationCord = destinationLocation.latitude + "," + destinationLocation.longitude;
        String sourceLocationName = geocode(getApplicationContext(),Double.toString(sourceLocation.latitude), Double.toString(sourceLocation.longitude));
        String destinationLocationName = geocode(getApplicationContext(),Double.toString(destinationLocation.latitude), Double.toString(destinationLocation.longitude));
        mChildDB.child("Source_Location").setValue(sourceLocationCord);
        mChildDB.child("Source_Location_Name").setValue(sourceLocationName);
        mChildDB.child("Destination_Location_Name").setValue(destinationLocationName);
        mChildDB.child("Destination_Location").setValue(destinationLocationCord);
        mChildDB.child("Car_id").setValue(car_id);
        mChildDB.child("Time").setValue(time);
        mChildDB.child("Date").setValue(date);
        mChildDB.child("Num_Seats").setValue(totalSeats);
        mChildDB.child("Cost_Per_Seat").setValue(costPerSeat);

        Intent intent = new Intent(Offer_ride_second_Activity.this, Offered_Ride_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public static String geocode(Context context, String latitude, String longitude)
    {
        String locationName = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Address address = null;
        try {
            List<Address> list = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude),1);

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
