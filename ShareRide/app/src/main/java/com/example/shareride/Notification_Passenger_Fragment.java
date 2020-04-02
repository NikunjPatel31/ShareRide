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
import android.widget.ProgressBar;
import android.widget.TextView;

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
    public static RecyclerView notificationPassengerRecyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ArrayList<String> passengerID = new ArrayList<>();
    private ArrayList<String> requestID = new ArrayList<>();
    private ArrayList<String> finalRequestID = new ArrayList<>();
    private ArrayList<String> offerRideID = new ArrayList<>();
    private ArrayList<String> riderID = new ArrayList<>();
    private ArrayList<String> offeredRideRiderID = new ArrayList<>();
    private ArrayList<UserDetails> offeredRideRiderDetails = new ArrayList<>();
    private ArrayList<SearchRideResultDetails> searchRideResultDetails = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView progressBarTextView;

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
        progressBar = view.findViewById(R.id.passenger_notification_progress_bar);
        progressBarTextView = view.findViewById(R.id.progress_bar_textview);
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
                if(dataSnapshot.getChildrenCount() == 0)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBarTextView.setText("Looks like you don't have any notification.");
                }
                else
                {
                    getRequestID((int) dataSnapshot.getChildrenCount());
                }
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
                requestID.remove(dataSnapshot.getKey());
                getOfferedRideID();
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
        Log.d(TAG, "getOfferedRideID: this function will check if the status of the ride.");
        for(int i = 0;i<requestID.size();i++)
        {
            DatabaseReference mChild = databaseReference.child("Registration").child(mAuth.getUid()).child(requestID.get(i));
            final int finalI = i;
            mChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                       if(finalI < requestID.size())
                       {
                           if(dataSnapshot.child("Status").getValue().equals("Accepted"))
                           {
                               Log.d(TAG, "onDataChange: dataSnapshot: "+dataSnapshot.toString());
                               Log.d(TAG, "onDataChange: offered_ride_id: "+dataSnapshot.child("Offer_Ride_ID").getValue());
                               finalRequestID.add(requestID.get(finalI));
                               offerRideID.add(dataSnapshot.child("Offer_Ride_ID").getValue().toString());
                           }
                           if(finalI == requestID.size() - 1)
                           {
                               getOfferedRideDetail();
                           }
                       }
                    }
                    else
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        progressBarTextView.setText("Looks like you don't have any notification.");
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
        final int[] counter = {0};
        final DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Offer_Ride");
        mChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final int childrenCount = (int) dataSnapshot.getChildrenCount();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Offer_Ride");
                databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        counter[0]++;
                        riderID.add(dataSnapshot.getKey());
                        Log.d(TAG, "onChildAdded: key: "+dataSnapshot.getKey());
                        Log.d(TAG, "onChildAdded: children count: "+dataSnapshot.getChildrenCount());
                        if(counter[0] == childrenCount)
                        {
                            checkRide();
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkRide()
    {
        Log.d(TAG, "checkRide: this function will check is the offered_ride_id is there in the rider_id node.");
        final int[] j = {0};
        for (int i = 0; i < riderID.size(); i++)
        {
            try
            {
                Log.d(TAG, "checkRide: riderID: "+i+" : "+riderID.get(i));
                DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Offer_Ride").child(riderID.get(i));
                final int finalI = i;
                final int finalI1 = i;
                mChild.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        try {
                            if(j[0] <= offerRideID.size())
                            {
                                if (dataSnapshot.getKey().equals(offerRideID.get(j[0])))
                                {
                                    Log.d(TAG, "onChildAdded: match found: "+riderID.get(finalI));
                                    offeredRideRiderID.add(riderID.get(finalI));
                                    j[0]++;
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, "onChildAdded: value of I: "+ finalI1);
                            Log.d(TAG, "onChildAdded: size of riderID: "+riderID.size());
                            Log.d(TAG, "onChildAdded: Exception in if else: "+e.getLocalizedMessage());
                        }

                        if (j[0] == offerRideID.size())
                        {
                            getRiderDetails();
                            j[0]++;
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
            catch (Exception e)
            {
                Log.d(TAG, "checkRide: Exception: "+e.getLocalizedMessage());
            }
        }
    }
    private void getRiderDetails()
    {
        Log.d(TAG, "getRiderDetails: this function will get the details of the offered_Ride Rider");
        Log.d(TAG, "getRiderDetails: offeredRideID size: "+offeredRideRiderID.size());
        for (int i = 0;i < offeredRideRiderID.size();i++)
        {
            try {
                DatabaseReference riderDB = FirebaseDatabase.getInstance().getReference().child("Users").child(offeredRideRiderID.get(i));
                final int finalI = i;
                riderDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            UserDetails tem = new UserDetails();
                            tem.setProfilePicture(dataSnapshot.child("Profile Picture").getValue().toString());
                            tem.setPincode(dataSnapshot.child("Pincode").getValue().toString());
                            tem.setLastName(dataSnapshot.child("Last Name").getValue().toString());
                            tem.setGender(dataSnapshot.child("Gender").getValue().toString());
                            tem.setContact(dataSnapshot.child("Contact").getValue().toString());
                            tem.setFirstName(dataSnapshot.child("First Name").getValue().toString());
                            tem.setDOB(dataSnapshot.child("DOB").getValue().toString());
                            tem.setCity(dataSnapshot.child("City").getValue().toString());
                            tem.setUserID(dataSnapshot.getKey());

                            offeredRideRiderDetails.add(tem);
                        }
                        else
                        {
                            Log.d(TAG, "onDataChange: there is no data");
                        }

                        if(finalI == (offeredRideRiderID.size() - 1))
                        {
                            getRideDetails();
                        }
                        else
                        {
                            Log.d(TAG, "onDataChange: offeredRideRiderID size: "+offeredRideRiderID.size());
                            Log.d(TAG, "onDataChange: finalI: "+finalI);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            catch (Exception e)
            {
                Log.d(TAG, "getRiderDetails: Exception:             "+e.getLocalizedMessage());
            }
        }
    }
    private void getRideDetails()
    {
        Log.d(TAG, "getRideDetails: this function will get the ride details");
        UserDetails tem = offeredRideRiderDetails.get(0);
        Log.d(TAG, "getRideDetails: offeredRideRiderDetails size: "+tem.getUserID());
        for (int i = 0; i < offeredRideRiderID.size(); i++)
        {
            try {
                UserDetails riderTem = offeredRideRiderDetails.get(i);
                Log.d(TAG, "getRideDetails: rider: "+riderTem.getUserID());
                DatabaseReference rideDB = FirebaseDatabase.getInstance().getReference().child("Offer_Ride").child(riderTem.getUserID())
                        .child(offerRideID.get(i));
                final int finalI = i;
                rideDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SearchRideResultDetails tem = new SearchRideResultDetails();
                        tem.setRideID(dataSnapshot.getKey());
                        tem.setTime(dataSnapshot.child("Time").getValue().toString());
                        tem.setSource_Location_Name(dataSnapshot.child("Source_Location_Name").getValue().toString());
                        tem.setSource_Location(dataSnapshot.child("Source_Location").getValue().toString());
                        tem.setNum_Seats(dataSnapshot.child("Num_Seats").getValue().toString());
                        tem.setDestination_Location_Name(dataSnapshot.child("Destination_Location_Name").getValue().toString());
                        tem.setDestination_Location(dataSnapshot.child("Destination_Location").getValue().toString());
                        tem.setDate(dataSnapshot.child("Date").getValue().toString());
                        tem.setCost_Per_Seat(dataSnapshot.child("Cost_Per_Seat").getValue().toString());
                        tem.setCar_id(dataSnapshot.child("Car_id").getValue().toString());

                        searchRideResultDetails.add(tem);

                        if (finalI == (offeredRideRiderID.size() - 1))
                        {
                            Log.d(TAG, "onDataChange: now we have every thing that we want...");
                            NotificationPassengerFragmentRecyclerViewAdapter adapter =
                                    new NotificationPassengerFragmentRecyclerViewAdapter(searchRideResultDetails,offeredRideRiderDetails,finalRequestID,getContext());
                            notificationPassengerRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        progressBarTextView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            catch (Exception e)
            {
                Log.d(TAG, "getRideDetails: Exception: "+e.getLocalizedMessage());
            }
        }
    }
}
