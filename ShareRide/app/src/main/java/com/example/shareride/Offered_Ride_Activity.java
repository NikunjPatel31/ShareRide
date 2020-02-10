package com.example.shareride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Offered_Ride_Activity extends AppCompatActivity {

    private static final String TAG = "Offered_Ride_Activity";

    private RecyclerView recyclerView;

    private FirebaseAuth mAtuh;
    private DatabaseReference mdatabaseReference;
    private FirebaseRecyclerOptions<CarDetails> options;

    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offered__ride_);

        initializeWidgets();
        intializeFirebaseInstances();
        initiaizingOption();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_offered_ride);
    }

    private void intializeFirebaseInstances()
    {
        Log.d(TAG, "intializeFirebaseInstances: initializing Firebase instances.");
        mAtuh = FirebaseAuth.getInstance();
        UID = mAtuh.getUid();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Offer_Ride").child(UID);
    }

    private void initiaizingOption()
    {
        Log.d(TAG, "initiaizingOption: initializing firebase recyclerview option.");
        options = new FirebaseRecyclerOptions.Builder<CarDetails>().
                setQuery(mdatabaseReference, CarDetails.class)
                .build();
    }
}
