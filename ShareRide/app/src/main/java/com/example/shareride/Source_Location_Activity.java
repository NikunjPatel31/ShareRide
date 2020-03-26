package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.transform.Source;

public class Source_Location_Activity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Source_Location_Activit";

    private EditText searchSourceET;
    private FloatingActionButton nextFAB;
    private GoogleMap mMap;

    private FusedLocationProviderClient mfusedLocationProviderClient;
    private LatLng centerScreenLatlng;

    private boolean location_permission = false;
    private final float DEFAULT_ZOOM = 15f;
    private final int ACCESS_FINE_LOCATION = 1;

    private boolean sourceActivity = false;

    private LatLng destinationLocation = null;
    private String time, date, car_name, totalSeats, costPerSeat, carID = null;

    public void next(View view)
    {
        nextFAB.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_click_animation));
        centerScreenLatlng = mMap.getCameraPosition().target;
        Log.d(TAG, "next: lat: "+centerScreenLatlng.latitude+" long: "+centerScreenLatlng.longitude);
        if(location_permission)
        {
            if(sourceActivity)
            {
                Log.d(TAG, "next: location set...");
                Intent intent = new Intent(Source_Location_Activity.this, Edit_Ride_Info_Activity.class);
                intent.putExtra("Activity","Edit_Source_Location");
                intent.putExtra("SourceLocationFinal",centerScreenLatlng);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(Source_Location_Activity.this, Destination_Location_Activity.class);
                intent.putExtra("Source Location",centerScreenLatlng);
                startActivity(intent);
            }
        }
    }

    public void centerOnMyLocation(View view)
    {
        //Toast.makeText(this, "Under development.", Toast.LENGTH_SHORT).show();
        getDeviceLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source__location_);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializingWidgets();
        animateWidgets();
        getLocationPermission();
        searchLocation();
    }

    private void initializingWidgets()
    {
        Log.d(TAG, "initializingWidgets: initializing widgets.");
        searchSourceET = (EditText) findViewById(R.id.search_source_location_edittext);
        nextFAB = (FloatingActionButton) findViewById(R.id.next_FAB);
    }
    private void animateWidgets()
    {
        Log.d(TAG, "animateWidgets: animating widgets.");
        searchSourceET.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
        nextFAB.setAnimation(AnimationUtils.loadAnimation(this,R.anim.recycler_view_animation));
    }

    private void getDeviceLocation()
    {
        if(location_permission)
        {
            Log.d(TAG, "getDeviceLocation: getting device location.");
            mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            Task<Location> task = mfusedLocationProviderClient.getLastLocation();
            task.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null)
                    {
                        Log.d(TAG, "getDeviceLocation: location: " + location.getLongitude() + " Lat: " + location.getLatitude());
                        mMap.setMyLocationEnabled(true);
                        addMarker(new LatLng(location.getLatitude(), location.getLongitude()),DEFAULT_ZOOM);
                    }
                    else
                    {
                        Log.d(TAG, "onComplete: unable to access location.");
                    }
                }
            });
        }
    }

    private void getLocationPermission()
    {
        Log.d(TAG, "getLocationPermission: getting location permission.");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "getLocationPermission: permissions are not given.");
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION);
        }
        else
        {
            Log.d(TAG, "getLocationPermission: permissions are given.");
            location_permission = true;
        }
    }

    private void addMarker(LatLng latLng,float zoom)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    private void addMarkerToPosition()
    {
        Log.d(TAG, "addMarkerToPosition: adding marker to the source location.");
        LatLng sourceLocation = getIntent().getExtras().getParcelable("Source_Location");
        addMarker(sourceLocation, DEFAULT_ZOOM);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: we are in onMapReady.");
        String activity = null;
        activity = getIntent().getStringExtra("Activity");
        if(location_permission)
        {
            Log.d(TAG, "onMapReady: if block...");
            if(activity.equals("Search_Ride"))
            {
                Toast.makeText(this, "We are comming from search activity.", Toast.LENGTH_SHORT).show();
            }
            else if(activity.equals("SourceLocationActivity"))
            {
                getIntentData();
                sourceActivity = true;
                Log.d(TAG, "onMapReady: we got source location coordinates");
                addMarkerToPosition();
            }
            else if(activity.equals("SourceLocation"))
            {
                Log.d(TAG, "onMapReady: if na if na else ma...");
                getDeviceLocation();
            }
            Log.d(TAG, "onMapReady: permission: "+location_permission);
            mMap.setMyLocationEnabled(true);
        }
        else
        {
            Log.d(TAG, "onMapReady: we are in else part.");
            getLocationPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                location_permission = true;
                getDeviceLocation();
            }
        }
    }

    private void searchLocation()
    {
        searchSourceET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate()
    {
        Log.d(TAG, "geoLocate: geolocating to the specified location.");

        String searchString = searchSourceET.getText().toString();

        Geocoder geocoder = new Geocoder(Source_Location_Activity.this);
        List<Address> list = new ArrayList<>();

        try
        {
            list = geocoder.getFromLocationName(searchString,1);
        }
        catch (Exception e)
        {
            Log.d(TAG, "geoLocate: Exception: "+e.getLocalizedMessage());
        }
        Address address = list.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        addMarker(latLng, DEFAULT_ZOOM);
    }
    private void getIntentData()
    {
        Log.d(TAG, "getIntentData: getting intent data from Edit_Ride_Info_Activity.");
        destinationLocation = getIntent().getExtras().getParcelable("Destination_Location");
        date = getIntent().getStringExtra("Date");
        time = getIntent().getStringExtra("Time");
        car_name = getIntent().getStringExtra("Car_Name");
    }
}
