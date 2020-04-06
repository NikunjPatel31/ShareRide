package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    boolean searchFound = false;
    private ArrayList<SearchRideResultDetails> searchRideResultDetails = new ArrayList<>();
    private ArrayList<String> userID = new ArrayList<>();
    private Map<Integer,String> priority = new HashMap<>();
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride_result);
        getIntentData();
        initializeWidgets();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initializeFirebaseObjects();
        progressDialog = CommanClass.progressBar(this,new ProgressDialog(this),"Searching ride...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: inside the onResume method.");
        if(searchFound)
        {
            progressDialog.dismiss();
        }
        else
        {
            progressDialog.show();
        }
        fun();
    }

    private void dialog()
    {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Ride...")
                .setMessage("Sorry, There is no available rides...")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: button clicked on alert dialog.");
                        Intent intent = new Intent(SearchRideResultActivity.this, SearchActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //finish();
                    }
                });
        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        alertDialog.show();
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

    private void fun()
    {
        DatabaseReference mChild = mDatabaseReference;
        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: database size: "+dataSnapshot.getChildrenCount());
                if(dataSnapshot.exists())
                {
                    int i = 0;
                    if(dataSnapshot.hasChild("Notification"))
                    {
                        i = 1;
                    }
                    getUIDForOfferedRide(i);
                }
                else
                {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUIDForOfferedRide(final int difference)
    {
        Log.d(TAG, "getUIDForOfferedRide: getting UID for offered ride.");
        DatabaseReference mChild = mDatabaseReference;
        mChild.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: mDatabase dataSnapshot size: "+dataSnapshot.getChildrenCount());
                if(dataSnapshot.exists())
                {
                    Counter++;
                    if(Counter == 2+difference)
                    {
                        Log.d(TAG, "onChildAdded: counter value: "+dataSnapshot.getChildrenCount());
                        Log.d(TAG, "onChildAdded: value of counter: "+Counter);
                        getUIDofRide((int) dataSnapshot.getChildrenCount());
                    }
                    else
                    {
                        Log.d(TAG, "onChildAdded: value of counter: "+Counter);
                    }
                }
                else
                {
                    progressDialog.dismiss();
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
    private void getUIDofRide(final int counter)
    {
        final DatabaseReference mChildDB = mDatabaseReference.child("Offer_Ride");
        mChildDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    map.put(dataSnapshot.getKey(), (int) dataSnapshot.getChildrenCount());
                    Log.d(TAG, "onChildAdded: UID for offered Ride: "+dataSnapshot.getKey());
                    Log.d(TAG, "onChildAdded: size of dataSnapshot: "+dataSnapshot.getChildrenCount());
                    UIDForOfferedRide.add(dataSnapshot.getKey());
                    UIDsum += dataSnapshot.getChildrenCount();
                    Log.d(TAG, "onChildAdded: UIDsum: "+UIDsum);
                    localCounter++;
                    priority.put(localCounter,dataSnapshot.getKey());
                    Log.d(TAG, "onChildAdded: localCounter: "+localCounter);

                    if(localCounter == counter)
                    {
                        Log.d(TAG, "onChildAdded: localCounter == counter-1"+counter);
                        getAllRides(mChildDB,UIDForOfferedRide);
                    }
                    else
                    {
                        Log.d(TAG, "onChildAdded: localCounter != Counter: "+Counter);
                    }
                }
                else
                {
                    progressDialog.dismiss();
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
                    if(dataSnapshot.exists())
                    {
                        RideID.add(dataSnapshot.getKey());
                        localCounter++;
                        Log.d(TAG, "onChildAdded: RideUID size outside the if block: "+RideID.size());
                        if(localCounter == UIDsum)
                        {
                            Log.d(TAG, "onChildAdded: RideUID Size: "+RideID.size());
                            Log.d(TAG, "onChildAdded: Counter Size: "+UIDsum);
                            Log.d(TAG, "onChildAdded: localCounter: "+localCounter);
                            getEachRideDetails(superChildDB, map, RideID);
                        }
                    }
                    else
                    {
                        progressDialog.dismiss();
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
        int j = 0, temJ = 1;
        Log.d(TAG, "getEachRideDetails: getting each ride details.");
        Log.d(TAG, "getEachRideDetails: map value: "+map.toString());
        Set< Map.Entry< String,Integer> > st = map.entrySet();
        for (Map.Entry< String,Integer> me:st)
        {
            UIDForOfferedRide.add(me.getKey());
            Log.d(TAG, "getEachRideDetails: String: "+UIDForOfferedRide.get(j));
            j++;
        }
        j--;
        int innerCounter = 0;
        Log.d(TAG, "getEachRideDetails: total number of rides in app: "+rideUID.size());
        for(int i = 0; i < rideUID.size();i++)
        {
            Log.d(TAG, "getEachRideDetails: new idea: "+map.get(priority.get(temJ)));
            if(innerCounter != (map.get(priority.get(temJ))))
            {
                Log.d(TAG, "getEachRideDetails: map Child: "+map.get(priority.get(temJ)));
                Log.d(TAG, "getEachRideDetails: userID of the rider: "+UIDForOfferedRide.get(j));
                Log.d(TAG, "getEachRideDetails: rideID of the ride: "+rideUID.get(i));
                DatabaseReference mChildDB = databaseReference.child(priority.get(temJ)).child(rideUID.get(i));
                final int finalI = i;
                final int finalJ = j;
                final int finalTemJ = temJ;
                mChildDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.toString());
                        if(dataSnapshot.exists())
                        {
                            matchRide(dataSnapshot,priority.get(finalTemJ));
                            if(finalI == rideUID.size()-1)
                            {
                                progressDialog.dismiss();
                                Log.d(TAG, "getEachRideDetails: this is the correct place to call function.");
                                if(searchRideResultDetails.isEmpty())
                                {
                                    dialog();
                                }
                                else
                                {
                                    searchFound = true;
                                    SearchRideResultRecyclerViewAdapter adapter = new SearchRideResultRecyclerViewAdapter(searchRideResultDetails,getApplicationContext(),mAuth);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            else
                            {
                                progressDialog.dismiss();
                            }
                            progressDialog.dismiss();
                        }
                        else
                        {
                            progressDialog.dismiss();
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
                temJ++;
                i--;
                if(j > 0)
                {
                    j--;
                }
                innerCounter = 0;
            }
        }
    }
    private void matchRide(DataSnapshot dataSnapshot, String userID)
    {
        if(sourceLocation.equals(dataSnapshot.child("Source_Location_Name").getValue())
                && destinationLocation.equals(dataSnapshot.child("Destination_Location_Name").getValue())
                && date.equals(dataSnapshot.child("Date").getValue())
                && time.equals(dataSnapshot.child("Time").getValue()))
        {
            SearchRideResultDetails rideDetails = new SearchRideResultDetails();
            rideDetails.setCost_Per_Seat(dataSnapshot.child("Cost_Per_Seat").getValue().toString());
            rideDetails.setDate(dataSnapshot.child("Date").getValue().toString());
            rideDetails.setDestination_Location_Name(dataSnapshot.child("Destination_Location_Name").getValue().toString());
            rideDetails.setNum_Seats(dataSnapshot.child("Num_Seats").getValue().toString());
            rideDetails.setSource_Location_Name(dataSnapshot.child("Source_Location_Name").getValue().toString());
            rideDetails.setTime(dataSnapshot.child("Time").getValue().toString());
            rideDetails.setUserID(userID);
            rideDetails.setCar_id(dataSnapshot.child("Car_id").getValue().toString());
            rideDetails.setRideID(dataSnapshot.getKey());
            rideDetails.setSource_Location(dataSnapshot.child("Source_Location").getValue().toString());
            rideDetails.setDestination_Location(dataSnapshot.child("Destination_Location").getValue().toString());
            searchRideResultDetails.add(rideDetails);
        }
    }
}
