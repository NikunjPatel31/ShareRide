package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Edit_Ride_Info_Activity extends AppCompatActivity {

    private static final String TAG = "Edit_Ride_Info_Activity";

    private TextView sourceLocationTV, destinationLocationTV, dateTV, timeTV, carNameTV, seatValueTV;
    private EditText costPerSeatET;
    private String date, time, car_id, ride_id, availableSeats, costPerSeat, carName, sourceLocation, destinationLocation;
    private LatLng sourceLatln, destinationLatlng;

    private int counter = 0;

    public void increaseNumber(View view)
    {
        Log.d(TAG, "increaseNumber: incrementing counter");
        counter++;
        seatValueTV.setText(Integer.toString(counter));
    }

    public void decreaseNumber(View view)
    {
        Log.d(TAG, "decreaseNumber: decrementing counter");
        if(counter > 0)
        {
            counter--;
        }
        seatValueTV.setText(Integer.toString(counter));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__ride__info_);
        getWindow().setBackgroundDrawableResource(R.drawable.background5);

        initializeWidgets();
        getIntentData();
        populatingWidgets();
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        sourceLocationTV = (TextView) findViewById(R.id.source_location_textview);
        destinationLocationTV = (TextView) findViewById(R.id.destination_location_textview);
        dateTV = (TextView) findViewById(R.id.date_textview);
        timeTV = (TextView) findViewById(R.id.time_textview);
        carNameTV = (TextView) findViewById(R.id.car_name_textview);
        seatValueTV = (TextView) findViewById(R.id.number_of_seats_textview);
        costPerSeatET = (EditText) findViewById(R.id.cost_per_seat_edittext);
    }

    private void getIntentData()
    {
        Log.d(TAG, "getIntentData: getting data from intent.");
        date = getIntent().getStringExtra("Date");
        time = getIntent().getStringExtra("Time");
        car_id = getIntent().getStringExtra("Car_id");
        ride_id = getIntent().getStringExtra("Ride_id");
        availableSeats = getIntent().getStringExtra("Available_seats");
        costPerSeat = getIntent().getStringExtra("Cost_Per_Seats");
        Log.d(TAG, "getIntentData: cost per seat: "+costPerSeat);
        carName = getIntent().getStringExtra("Car_name");
        sourceLatln = getIntent().getExtras().getParcelable("Source_Latlng");
        destinationLatlng = getIntent().getExtras().getParcelable("Destination_Latlng");

        sourceLocation = geocode(getApplicationContext(), Double.toString(sourceLatln.latitude), Double.toString(sourceLatln.longitude));
        destinationLocation = geocode(getApplicationContext(), Double.toString(destinationLatlng.latitude), Double.toString(destinationLatlng.longitude));
    }

    private void populatingWidgets()
    {
        Log.d(TAG, "populatingWidgets: populating widgets.");
        dateTV.setText(date);
        timeTV.setText(time);
        seatValueTV.setText(availableSeats);
        costPerSeatET.setText(costPerSeat);
        carNameTV.setText(carName);
        sourceLocationTV.setText(sourceLocation);
        destinationLocationTV.setText(destinationLocation);
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
