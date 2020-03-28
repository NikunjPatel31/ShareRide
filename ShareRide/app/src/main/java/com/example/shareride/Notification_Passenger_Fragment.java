package com.example.shareride;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notification_Passenger_Fragment extends Fragment {

    private static final String TAG = "Notification_Passenger_";
    private RecyclerView notificationPassengerRecyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ArrayList<String> passengerID = new ArrayList<>();
    private ArrayList<String> requestID = new ArrayList<>();
    private ArrayList<SearchRideResultDetails> searchRideResultDetails;
    private ArrayList<String> offerRideID = new ArrayList<>();

    public Notification_Passenger_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification__passenger_, container,false);
        initializeFirebaseInstances();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeWidgets(view);
        notificationPassengerRecyclerView.setHasFixedSize(true);;
        notificationPassengerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPassengerID();
    }

    private void initializeWidgets(View view)
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        notificationPassengerRecyclerView = view.findViewById(R.id.notification_passenger_recyclerView);
    }
    private void initializeFirebaseInstances()
    {
        Log.d(TAG, "initializeFirebaseInstances: initializing firebase instances.");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    private void getPassengerID()
    {
        Log.d(TAG, "getPassengerID: this function will get the passenger_id.");
        DatabaseReference mChild = databaseReference.child("Registration").child(mAuth.getUid());
        mChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getRequestID((int) dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getRequestID(final int childrenCount)
    {
        Log.d(TAG, "getRequestID: this function will get the request_id.");
        DatabaseReference mChild = databaseReference.child("Registration").child(mAuth.getUid());
        mChild.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                requestID.add(dataSnapshot.getKey());
                if(requestID.size() == childrenCount)
                {
                    getOfferedRideID();
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

    private void getOfferedRideID()
    {
        Log.d(TAG, "chechStatus: this function will check if the status of the ride.");
        for(int i = 0;i<requestID.size();i++)
        {
            DatabaseReference mChild = databaseReference.child("Registration").child(mAuth.getUid()).child(requestID.get(i));
            final int finalI = i;
            mChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("Status").getValue().equals("Accepted"))
                    {
                        Log.d(TAG, "onDataChange: offered_ride_id: "+dataSnapshot.child("Offer_Ride_ID").getValue());
                        offerRideID.add(dataSnapshot.child("Offer_Ride_ID").getValue().toString());
                    }
                    if(finalI == requestID.size() - 1)
                    {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void getOfferedRideDetail()
    {
        Log.d(TAG, "getOfferedRideDetail: this function will get the details of the offered rides.");
        for(int i = 0;i < offerRideID.size();i++)
        {
        }
    }
}
