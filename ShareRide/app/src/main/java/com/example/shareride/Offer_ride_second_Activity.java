package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Offer_ride_second_Activity extends AppCompatActivity {

    private static final String TAG = "Offer_ride_second_Activ";

    private TextView numOfSeats;
    private EditText costPerSeatET;

    private int counter = 0;
    private LatLng sourceLocation, destinationLocation;
    private String car_id, time, date;
    private String costPerSeat, totalSeats;

    private FirebaseAuth mAtuh;
    private DatabaseReference mdatabaseReference;
    private String UID;

    public void offer(View view)
    {
        if(validatingFields())
        {
            Log.d(TAG, "offer: fields are validated.");
            sendingDataToDatabase();
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
        mChildDB.child("Source_Location").setValue(sourceLocationCord);
        mChildDB.child("Destination_Location").setValue(destinationLocationCord);
        mChildDB.child("Car_id").setValue(car_id);
        mChildDB.child("Time").setValue(time);
        mChildDB.child("Date").setValue(date);
        mChildDB.child("Num_Seats").setValue(totalSeats);
        mChildDB.child("Cost_Per_Seat").setValue(costPerSeat);
    }
}
