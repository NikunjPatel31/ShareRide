package com.example.shareride;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
public class Notification_Rider_Fragment extends Fragment {

    private static final String TAG = "Notification_Rider_Frag";
    private Button myRidesBtn, rideProgressBtn;
    private RecyclerView notificationRiderRecyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ArrayList<String> requestID = new ArrayList<>();
    private ArrayList<String> passengerID = new ArrayList<>();

    public Notification_Rider_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification__rider_, container, false);
        initializeWidgets(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFirebaseInstances();
    }

    @Override
    public void onStart() {
        super.onStart();
        buttonListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mainFunction();
    }

    private void initializeWidgets(View view)
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        myRidesBtn = (Button) view.findViewById(R.id.my_ride_button);
        rideProgressBtn = (Button) view.findViewById(R.id.ride_progress_button);
        notificationRiderRecyclerView = (RecyclerView) view.findViewById(R.id.notification_rider_recyclerView);
    }
    private void initializeFirebaseInstances()
    {
        Log.d(TAG, "initializeFirebaseInstances: initializing firebase instances");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    private void buttonListener()
    {
        myRidesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Offered_Ride_Activity.class);
                startActivity(intent);
            }
        });
        rideProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click event code....
            }
        });
    }
    private void mainFunction()
    {
        Log.d(TAG, "mainFunction: This function will check that if there is any node for the current user.");
        DatabaseReference mChild = databaseReference.child("Notification").child("Rider").child(mAuth.getCurrentUser().getUid());
        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: node exists: "+dataSnapshot.exists());
                if(dataSnapshot.exists())
                {
                    getRequestID();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getRequestID()
    {
        Log.d(TAG, "getRequestID: this function will get the request_id: ");
        final DatabaseReference mChild = databaseReference.child("Notification").child("Rider").child(mAuth.getCurrentUser().getUid());
        mChild.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    requestID.add(dataSnapshot.getKey());
                    if(requestID.size() == dataSnapshot.getChildrenCount())
                    {
                        Log.d(TAG, "onChildAdded: size of requestID array list: "+requestID.size());
                        getPassengerID(requestID,mChild);
                    }
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
    private void getPassengerID(ArrayList<String> requestID, DatabaseReference databaseReference)
    {
        Log.d(TAG, "getPassengerID: this function will get the passenger id");
        for(int i = 0;i < requestID.size(); i++)
        {
            Log.d(TAG, "getPassengerID: requestID: "+requestID.get(i));
            DatabaseReference mChild = databaseReference.child(requestID.get(i));
            mChild.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.exists())
                    {
                        passengerID.add(dataSnapshot.getKey());
                        if(passengerID.size() == dataSnapshot.getChildrenCount())
                        {
                            // now we have request id, riderID, PassengerID,
                        }
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
}
