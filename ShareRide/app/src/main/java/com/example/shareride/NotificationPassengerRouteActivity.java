package com.example.shareride;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NotificationPassengerRouteActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "NotificationPassengerRo";
    private GoogleMap mMap;
    private SearchRideResultDetails rideDetails;
    private UserDetails riderDetails;
    private String adpaterPosition = "afa";

    public void moreInformation(View view)
    {
        Intent intent = new Intent(NotificationPassengerRouteActivity.this,SearchedRideCompleteInfoActivity.class);
        intent.putExtra("Ride_Details",rideDetails);
        intent.putExtra("Rider_Details",riderDetails);
        intent.putExtra("Activity","Passenger_Notification");
        intent.putExtra("Adapter_Position",adpaterPosition);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_passenger_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getIntentData();
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
        rideDetails = getIntent().getParcelableExtra("Ride_Details");
        riderDetails = getIntent().getParcelableExtra("Rider_Details");
        adpaterPosition = getIntent().getStringExtra("Adapter_Position");
        Log.d(TAG, "getIntentData: adapter position: "+adpaterPosition);
    }
}
