package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class Source_Location_Activity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Source_Location_Activit";

    private EditText searchSourceET;
    private GoogleMap mMap;

    private FusedLocationProviderClient mfusedLocationProviderClient;

    private boolean location_permission = false;
    private final float DEFAULT_ZOOM = 15f;
    private final int ACCESS_FINE_LOCATION = 1;

    public void next(View view)
    {
        Toast.makeText(this, "Under development.", Toast.LENGTH_SHORT).show();
    }

    public void centerOnMyLocation(View view)
    {
        Toast.makeText(this, "Under development.", Toast.LENGTH_SHORT).show();
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
        getLocationPermission();
    }

    private void initializingWidgets()
    {
        Log.d(TAG, "initializingWidgets: initializing widgets.");
        searchSourceET = (EditText) findViewById(R.id.search_source_location_edittext);
    }

    private void getDeviceLocation()
    {
        Log.d(TAG, "getDeviceLocation: getting device location.");
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = mfusedLocationProviderClient.getLastLocation();
        task.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                Log.d(TAG, "getDeviceLocation: location: "+location.getLongitude()+" Lat: "+location.getLatitude());
                addMarker(new LatLng(location.getLatitude(), location.getLongitude()),DEFAULT_ZOOM);
            }
        });
    }

    private void getLocationPermission()
    {
        Log.d(TAG, "getLocationPermission: getting location permission.");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_FINE_LOCATION);
            location_permission = true;
        }
        else
        {
            location_permission = true;
        }
    }

    private void addMarker(LatLng latLng,float zoom)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        mMap.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(location_permission)
        {
            getDeviceLocation();
        }
    }
}
