package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SearchRideResultActivity extends AppCompatActivity {

    private static final String TAG = "SearchRideResultActivit";
    private RecyclerView recyclerView;
    private String sourceLocation, destinationLocation, date, time;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerOptions<SearchRideResultDetails> options;
    private ArrayList<String> UIDForOfferedRide = new ArrayList<>();
    private int Counter = 0, localCounter = 0, UIDsum = 0;
    private Map<String, Integer> map = new HashMap<>();
    boolean flag = true;
    private ArrayList<SearchRideResultDetails> searchRideResultDetails = new ArrayList<>();
    private int arrayIndex = 0;
    private ArrayList<String> userID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride_result);
        getIntentData();
        initializeWidgets();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initializeFirebaseObjects();
        getUIDForOfferedRide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: inside the onStart method.");
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_search_ride_result);
    }
    private void initializeFirebaseObjects()
    {
        Log.d(TAG, "initializeFirebaseObjects: initializing firebase objects.");
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }
    private void getIntentData()
    {
        Log.d(TAG, "getIntentData: getting intent data.");
        sourceLocation = getIntent().getStringExtra("Source_Location");
        destinationLocation = getIntent().getStringExtra("Destination_Location");
        date = getIntent().getStringExtra("Date");
        time = getIntent().getStringExtra("Time");
    }
    private void getUIDForOfferedRide()
    {
        Log.d(TAG, "getUIDForOfferedRide: getting UID for offered ride.");

        DatabaseReference mChild = mDatabaseReference;
        mChild.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Counter++;
                if(Counter == 2)
                {
                    Log.d(TAG, "onChildAdded: mDatabase dataSnapshot size: "+dataSnapshot.getChildrenCount());
                    getUIDofRide();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getUIDofRide()
    {
        final DatabaseReference mChildDB = mDatabaseReference.child("Offer_Ride");
        mChildDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                map.put(dataSnapshot.getKey(), (int) dataSnapshot.getChildrenCount());
                Log.d(TAG, "onChildAdded: UID for offered Ride: "+dataSnapshot.getKey());
                Log.d(TAG, "onChildAdded: size of dataSnapshot: "+dataSnapshot.getChildrenCount());
                UIDForOfferedRide.add(dataSnapshot.getKey());
                UIDsum += dataSnapshot.getChildrenCount();
                Log.d(TAG, "onChildAdded: UIDsum: "+UIDsum);
                localCounter++;
                Log.d(TAG, "onChildAdded: localCounter: "+localCounter);

                if(localCounter == Counter-1)
                {
                    getAllRides(mChildDB,UIDForOfferedRide);
                }
                else
                {
                    Log.d(TAG, "onChildAdded: localCounter != Counter: "+Counter);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getAllRides(DatabaseReference databaseReference,ArrayList<String> UIDForOfferedRide)
    {
        localCounter = 0;
        final DatabaseReference superChildDB = databaseReference;
        final ArrayList<String> RideID = new ArrayList<>();
        Log.d(TAG, "getAllRides: getting details of all rides of UID: ");
        for(int i = 0; i < UIDForOfferedRide.size();i++)
        {
            final DatabaseReference mChildDB = databaseReference.child(UIDForOfferedRide.get(i));
            mChildDB.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "onChildAdded: RideID: "+dataSnapshot.getKey());
                    RideID.add(dataSnapshot.getKey());
                    localCounter++;
                    if(localCounter == UIDsum)
                    {
                        getEachRideDetails(superChildDB, map, RideID);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
    private void getEachRideDetails(DatabaseReference databaseReference, Map<String, Integer> map, final ArrayList<String> rideUID)
    {
        final ArrayList<String> UIDForOfferedRide = new ArrayList<>();
        int j = 0;
        Log.d(TAG, "getEachRideDetails: getting each ride details.");
        Set< Map.Entry< String,Integer> > st = map.entrySet();
        for (Map.Entry< String,Integer> me:st)
        {
            UIDForOfferedRide.add(me.getKey());
            Log.d(TAG, "getEachRideDetails: String: "+UIDForOfferedRide.get(j));
            j++;
        }
        j--;
        Log.d(TAG, "getEachRideDetails: map Child: "+map.get(UIDForOfferedRide.get(j)));
        int innerCounter = 0;
        for(int i = 0; i < rideUID.size();i++)
        {
            if(innerCounter != (map.get(UIDForOfferedRide.get(j))))
            {
                DatabaseReference mChildDB = databaseReference.child(UIDForOfferedRide.get(j)).child(rideUID.get(i));
                final int finalI = i;
                final int finalJ = j;
                mChildDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.toString());
                        matchRide(dataSnapshot,arrayIndex,UIDForOfferedRide.get(finalJ));
                        arrayIndex++;
                        if(finalI == rideUID.size()-1)
                        {
                            Log.d(TAG, "getEachRideDetails: this is the correct place to call function.");
                            recyclerView.setAdapter(new SearchRideResultRecyclerViewAdapter(searchRideResultDetails,getApplicationContext()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                innerCounter++;
            }
            else
            {
                i--;
                if(j > 0)
                {
                    j--;
                }
                innerCounter = 0;
            }
        }
    }
    private void matchRide(DataSnapshot dataSnapshot, int arrayIndex, String userID)
    {
        if(sourceLocation.equals(dataSnapshot.child("Source_Location_Name").getValue())
                && destinationLocation.equals(dataSnapshot.child("Destination_Location_Name").getValue())
                && date.equals(dataSnapshot.child("Date").getValue())
                && time.equals(dataSnapshot.child("Time").getValue()))
        {
            Toast.makeText(this, "Match Found..."+dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
            SearchRideResultDetails rideDetails = new SearchRideResultDetails();
            rideDetails.setCost_Per_Seat(dataSnapshot.child("Cost_Per_Seat").getValue().toString());
            rideDetails.setDate(dataSnapshot.child("Date").getValue().toString());
            rideDetails.setDestination_Location_Name(dataSnapshot.child("Destination_Location_Name").getValue().toString());
            rideDetails.setNum_Seats(dataSnapshot.child("Num_Seats").getValue().toString());
            rideDetails.setSource_Location_Name(dataSnapshot.child("Source_Location_Name").getValue().toString());
            rideDetails.setTime(dataSnapshot.child("Time").getValue().toString());
            rideDetails.setUserID(userID);
            searchRideResultDetails.add(rideDetails);
        }
    }
}
