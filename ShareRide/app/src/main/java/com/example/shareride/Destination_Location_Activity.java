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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Destination_Location_Activity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Destination_Location_Ac";

    private EditText searchSourceET;

    private LatLng centerScreenLatlng;

    private GoogleMap mMap;

    private FusedLocationProviderClient mfusedLocationProviderClient;

    private final int ACCESS_FINE_LOCATION = 1;
    private final float DEFAULT_ZOOM = 15f;
    private boolean location_permission = false;

    public void next(View view)
    {
        Toast.makeText(this, "Under development.", Toast.LENGTH_SHORT).show();
        centerScreenLatlng = mMap.getCameraPosition().target;
        Log.d(TAG, "next: lat: "+centerScreenLatlng.latitude+" long: "+centerScreenLatlng.longitude);
        //startActivity(new Intent(Destination_Location_Activity.this, Destination_Location_Activity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination__location_);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializingWidgets();
        getLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(location_permission)
        {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            searchLocation();
        }
        else
        {
            getLocationPermission();
        }
    }

    private void initializingWidgets()
    {
        Log.d(TAG, "initializingWidgets: initializing widgets.");
        searchSourceET = (EditText) findViewById(R.id.search_source_location_edittext);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            location_permission = true;
        }
        else
        {
            location_permission = false;
        }
    }

    private void getDeviceLocation()
    {
        Log.d(TAG, "getDeviceLocation: getting device location.");
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = mfusedLocationProviderClient.getLastLocation();

        task.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful())
                {
                    Location location = task.getResult();
                    if(location != null)
                    {
                        Log.d(TAG, "onComplete: lat: "+location.getLatitude()+" Long: "+location.getLongitude());
                    }
                    else
                    {
                        Log.d(TAG, "onComplete: unable to access location.");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Exception: "+e.getLocalizedMessage());
                Toast.makeText(Destination_Location_Activity.this, "Problem in getting location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarker(LatLng latLng,float zoom)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    private void searchLocation()
    {
        searchSourceET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    geocode();
                }
                return false;
            }
        });
    }

    private void geocode()
    {
        Log.d(TAG, "geocode: geolocation...");
        String searchString = searchSourceET.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();

        try
        {
            list = geocoder.getFromLocationName(searchString,1);
        }
        catch (Exception e)
        {
            Log.d(TAG, "geocode: Exception: "+e.getLocalizedMessage());
        }
        Address address = list.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        addMarker(latLng, DEFAULT_ZOOM);
    }
}
