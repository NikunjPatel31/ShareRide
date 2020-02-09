package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class Offer_ride_second_Activity extends AppCompatActivity {

    private static final String TAG = "Offer_ride_second_Activ";

    private TextView numOfSeats;
    private EditText costPerSeatET;

    private int counter = 0;
    private LatLng sourceLocation, destinationLocation;
    private String car_id, time, date;

    public void offer(View view)
    {
        String costPerSeat = costPerSeatET.getText().toString();
        String totalSeats = numOfSeats.getText().toString();
        Log.d(TAG, "offer: Source Lat: "+sourceLocation.latitude+" Long: "+sourceLocation.longitude);
        Log.d(TAG, "offer: Destination Lat: "+destinationLocation.latitude+" Long: "+destinationLocation.longitude);
        Log.d(TAG, "offer: time: "+time);
        Log.d(TAG, "offer: date: "+date);
        Log.d(TAG, "offer: car_id: "+car_id);
        Log.d(TAG, "offer: seat: "+totalSeats);
        Log.d(TAG, "offer: cost: "+costPerSeat);
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

        sourceLocation = getIntent().getExtras().getParcelable("Source Location");
        destinationLocation = getIntent().getExtras().getParcelable("Destination Location");
        car_id = getIntent().getStringExtra("Car_id");
        time = getIntent().getStringExtra("Time");
        date = getIntent().getStringExtra("Date");
        initializeWidgets();
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets");
        numOfSeats = (TextView) findViewById(R.id.number_of_seats_textview);
        costPerSeatET = (EditText) findViewById(R.id.cost_per_seat_edittext);
    }
}
