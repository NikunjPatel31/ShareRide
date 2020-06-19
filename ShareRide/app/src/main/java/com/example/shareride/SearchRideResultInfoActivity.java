package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;

import java.time.Year;
import java.util.Calendar;

public class SearchRideResultInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "SearchRideResultInfoAct";

    private GoogleMap mMap;
    private SearchRideResultDetails searchRideResultDetails;
    private TextView riderNameTV, ratingTV, sourceLocationTV, destinationLocationTV, timeTV, dateTV, seatTV, costPerSeatTV, genderTV;
    private ImageView ratingStar;
    private TextView ratingValueTV;
    private String riderUID=null;
    private UserDetails riderDetails=null;
    private boolean requestFlag = false;
    private CardView cardView;
    private String activity;
    private String request_id;
    private String position;
    private String size;

    private String source_location = null;
    private String destination_location = null;

    private GeoApiContext geoApiContext = null;

    public void moreInformation(View view)
    {
        if(activity.equals("Rider_Notification"))
        {
            Log.d(TAG, "moreInformation: if block");
            Intent intent = new Intent(SearchRideResultInfoActivity.this, riderNotificationPassengerCompleteInfo.class);
            intent.putExtra("Passenger_details",riderDetails);
            intent.putExtra("Request_id",request_id);
            intent.putExtra("Position",position);
            intent.putExtra("Ride_Details",searchRideResultDetails);
            intent.putExtra("size",size);
            startActivity(intent);
        }
        else
        {
            Log.d(TAG, "moreInformation: else block");
            Intent intent = new Intent(SearchRideResultInfoActivity.this, SearchedRideCompleteInfoActivity.class);
            intent.putExtra("Ride_Details",searchRideResultDetails);
            intent.putExtra("Rider_Details",riderDetails);
            intent.putExtra("Activity","search");
            intent.putExtra("Request_Flag",requestFlag);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride_result_info);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializeWidgets();
        if(geoApiContext == null)
        {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_api_key)).build();
        }
        cardView.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));

        if(getIntent().getStringExtra("Activity").equals("Rider_Notification"))
        {
            ratingStar.setVisibility(View.INVISIBLE);
            ratingValueTV.setVisibility(View.INVISIBLE);
            activity = "Rider_Notification";
        }
        else
        {
            activity = "Search";
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getIntentData();
        populatingWidgets();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        calculateDirections();
    }

    private void getIntentData()
    {
        Log.d(TAG, "getIntentData: getting intent data.");
        try
        {
            searchRideResultDetails = getIntent().getParcelableExtra("Ride_details");
            riderDetails = getIntent().getParcelableExtra("Rider_Details");
            requestFlag = getIntent().getBooleanExtra("Request_Flag",false);
            riderUID = getIntent().getStringExtra("Rider_UID");
            request_id = getIntent().getStringExtra("Request_id");
            position = getIntent().getStringExtra("Position");
            size = getIntent().getStringExtra("size");

            source_location = searchRideResultDetails.getSource_Location();
            destination_location = searchRideResultDetails.getDestination_Location();
            //Log.d(TAG, "getIntentData: source_location: "+source_location);
        }
        catch (Exception e)
        {
            Log.d(TAG, "getIntentData: Exception: "+e.getLocalizedMessage());
        }
    }
    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        cardView = (CardView) findViewById(R.id.cardview);
        riderNameTV = (TextView) findViewById(R.id.rider_name_textview);
        sourceLocationTV = (TextView) findViewById(R.id.source_location_textview);
        destinationLocationTV = (TextView) findViewById(R.id.destination_location_textview);
        timeTV = (TextView) findViewById(R.id.time_value_textview);
        dateTV = (TextView) findViewById(R.id.date_value_textview);
        seatTV = (TextView) findViewById(R.id.seats_value_textview);
        costPerSeatTV = (TextView) findViewById(R.id.cost_per_seat_textview);
        genderTV = (TextView) findViewById(R.id.gender_value_textview);
        ratingStar = (ImageView) findViewById(R.id.rating_star);
        ratingValueTV = (TextView) findViewById(R.id.rating_value);

    }
    private void populatingWidgets()
    {
        Log.d(TAG, "populatingWidgets: populating widgets.");

        try
        {
            String firstName = riderDetails.getFirstName();
            String lastName = riderDetails.getLastName();
            String fullName = firstName+" "+lastName;
            riderNameTV.setText(fullName);
            sourceLocationTV.setText(searchRideResultDetails.getSource_Location_Name());
            destinationLocationTV.setText(searchRideResultDetails.getDestination_Location_Name());
            timeTV.setText(searchRideResultDetails.getTime());
            dateTV.setText(searchRideResultDetails.getDate());
            seatTV.setText(searchRideResultDetails.getNum_Seats());
            if(getIntent().getStringExtra("Activity").equals("Rider_Notification"))
            {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int DOB = Integer.parseInt(riderDetails.getDOB());
                costPerSeatTV.setText("Age : "+(year-DOB));
            }
            else if (getIntent().getStringExtra("Activity").equals("Search"))
            {
                costPerSeatTV.setText(searchRideResultDetails.getCost_Per_Seat());
            }
            genderTV.setText(riderDetails.getGender());
        }
        catch (Exception e)
        {
            Log.d(TAG, "populatingWidgets: Exception: "+e.getLocalizedMessage());
        }
    }



    private void calculateDirections(){
        Log.d(TAG, "calculateDirections: calculating directions.");

        String[] source_loc = source_location.split(",");
        String[] destination_loc = destination_location.split(",");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                Double.parseDouble(source_loc[0]),
                Double.parseDouble(source_loc[1])
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        Double.parseDouble(destination_loc[0]),
                        Double.parseDouble(destination_loc[1])
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }
}
