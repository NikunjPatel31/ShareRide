package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
    private LatLng sourceLatln, destinationLatlng = null;

    private int counter = 0;

    String activity = null;
    private LatLng editSourceLatln = null;
    private LatLng editDestinationLatln = null;

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
        textViewOnClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        activity = getIntent().getStringExtra("Activity");
        Log.d(TAG, "getIntentData: activity: "+activity);
        if(activity.equals("Edit_Source_Location"))
        {
            editSourceLatln = getIntent().getExtras().getParcelable("SourceLocationFinal");
            Log.d(TAG, "getIntentData: edit Source Location Lat: "+editSourceLatln.latitude);
            sourceLocation = geocode(this, Double.toString(editSourceLatln.latitude), Double.toString(editSourceLatln.longitude));
            Log.d(TAG, "getIntentData: edit Source location name: "+sourceLocation);
        }
        else if(activity.equals("Info_offered_ride"))
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

    }

    private void populatingWidgets()
    {
        Log.d(TAG, "populatingWidgets: populating widgets.");
        Log.d(TAG, "populatingWidgets: we got data from info_offered_ride activity");
        dateTV.setText(date);
        timeTV.setText(time);
        seatValueTV.setText(availableSeats);
        costPerSeatET.setText(costPerSeat);
        carNameTV.setText(carName);
        sourceLocationTV.setText(sourceLocation);
        destinationLocationTV.setText(destinationLocation);
    }

    public void textViewOnClickListener()
    {
        sourceLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Edit_Ride_Info_Activity.this, Source_Location_Activity.class);
                intent.putExtra("Activity","SourceLocationActivity");
                intent.putExtra("Source_Location",sourceLatln);
                intent.putExtra("Destination_Location",destinationLatlng);
                intent.putExtra("Date",date);
                intent.putExtra("Time",time);
                intent.putExtra("Car_Name",carName);
                intent.putExtra("Car_ID",car_id);
                intent.putExtra("Total_Seats",availableSeats);
                intent.putExtra("Cost_Per_Seat",costPerSeat);
                startActivity(intent);
            }
        });
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
