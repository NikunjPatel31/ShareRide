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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    String activity;

    public void moreInformation(View view)
    {
        Intent intent = new Intent(SearchRideResultInfoActivity.this, SearchedRideCompleteInfoActivity.class);
        intent.putExtra("Activity",activity);
        intent.putExtra("Ride_Details",searchRideResultDetails);
        intent.putExtra("Rider_Details",riderDetails);
        intent.putExtra("Request_Flag",requestFlag);
        startActivity(intent);
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
}
