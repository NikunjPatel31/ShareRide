package com.example.shareride;

import android.content.Intent;
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
import android.widget.Button;
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
public class Notification_Rider_Fragment extends Fragment {

    private static final String TAG = "Notification_Rider_Frag";
    private Button myRidesBtn, rideProgressBtn;
    private RecyclerView notificationRiderRecyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ArrayList<String> offeredRideID = new ArrayList<>();
    private ArrayList<String> passengerID = new ArrayList<>();
    private ArrayList<String> requestID = new ArrayList<>();
    private ArrayList<SearchRideResultDetails> searchRideResultDetails = new ArrayList<>();
    private ArrayList<UserDetails> passengerDetails = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView progressBarTextView;

    public Notification_Rider_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification__rider_, container, false);
        initializeWidgets(view);
        notificationRiderRecyclerView.setHasFixedSize(true);
        notificationRiderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        progressBar = view.findViewById(R.id.rider_notification_progress_bar);
        progressBarTextView = view.findViewById(R.id.progress_bar_textview);
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
                if(dataSnapshot.exists())
                {
                    getRequestRideID((int) dataSnapshot.getChildrenCount());
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
    private void getRequestRideID(final int childrenCount)
    {
        Log.d(TAG, "getRequestRideID: this function will get the request_id: ");
        final DatabaseReference mChild = databaseReference.child("Notification").child("Rider").child(mAuth.getCurrentUser().getUid());
        mChild.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    requestID.add(dataSnapshot.getKey());
                    if(requestID.size() == childrenCount)
                    {
                        getOfferedRideID(requestID,mChild);
                    }
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBarTextView.setText("Looks like you don't have any notification.");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "getRequestRideID: onChildRemoved: request_id removed: "+dataSnapshot.getKey());
                requestID.remove(dataSnapshot.getKey());
                getOfferedRideID(requestID,mChild);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getOfferedRideID(final ArrayList<String> requestID, DatabaseReference databaseReference)
    {
        Log.d(TAG, "getOfferedRideID: getting offered ride id.");
        for(int i = 0; i < requestID.size();i++)
        {
            final DatabaseReference mChild = databaseReference.child(requestID.get(i));
            final int finalI = i;
            mChild.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "getOfferedRideID: requestID: "+requestID.get(finalI));
                    Log.d(TAG, "getOfferedRideID: onChildAdded: key: "+dataSnapshot.getKey());
                    offeredRideID.add(dataSnapshot.getKey());
                    if(offeredRideID.size() == requestID.size())
                    {
                        Log.d(TAG, "getOfferedRideID: onChildAdded: offeredRideID size: "+offeredRideID.size());
                        getPassengerID(offeredRideID,mChild);
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
    private void getPassengerID(final ArrayList<String> offeredRideID, DatabaseReference databaseReference)
    {
        Log.d(TAG, "getPassengerID: this function will get the passenger id");
        Log.d(TAG, "getPassengerID: offered_Ride_Size: "+offeredRideID.size());
        for(int i = 0;i < offeredRideID.size(); i++)
        {
            Log.d(TAG, "getPassengerID: requestID: "+requestID.get(i));
            Log.d(TAG, "getPassengerID: offereID: "+offeredRideID.get(i));
            Log.d(TAG, "getPassengerID: uid: "+mAuth.getCurrentUser().getUid());
            DatabaseReference mChild = FirebaseDatabase.getInstance().getReference()
                                                        .child("Notification")
                                                        .child("Rider")
                                                        .child(mAuth.getCurrentUser().getUid())
                                                        .child(requestID.get(i))
                                                        .child(offeredRideID.get(i));
            final int finalI = i;
            mChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        passengerID.add(dataSnapshot.child("Passenger_ID").getValue().toString());
                        if(passengerID.size() == offeredRideID.size())
                        {
                            Log.d(TAG, "getPassengerID onDataChange: passengerID: "+passengerDetails.size());
                            // now we have request id, riderID, PassengerID,
                            getRideDetails();
//                            getPassengerDetails();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void getRideDetails()
    {
        Log.d(TAG, "getRideDetails: getting ride details.");
        int i = 0;
        for (i = 0;i < offeredRideID.size();i++)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Offer_Ride")
                    .child(mAuth.getCurrentUser().getUid())
                    .child(offeredRideID.get(i));
            final int finalI = i;
            final int finalI1 = i;
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        Log.d(TAG, "getRideDetails onDataChange: we are fetching details of the searchRideDetails.");
                       SearchRideResultDetails tem = new SearchRideResultDetails();
                       tem.setCar_id(dataSnapshot.child("Car_id").getValue().toString());
                       tem.setCost_Per_Seat(dataSnapshot.child("Cost_Per_Seat").getValue().toString());
                       tem.setDate(dataSnapshot.child("Date").getValue().toString());
                       tem.setDestination_Location(dataSnapshot.child("Destination_Location").getValue().toString());
                       tem.setDestination_Location_Name(dataSnapshot.child("Destination_Location_Name").getValue().toString());
                       tem.setNum_Seats(dataSnapshot.child("Num_Seats").getValue().toString());
                       tem.setSource_Location(dataSnapshot.child("Source_Location").getValue().toString());
                       tem.setSource_Location_Name(dataSnapshot.child("Source_Location_Name").getValue().toString());
                       tem.setTime(dataSnapshot.child("Time").getValue().toString());
                       tem.setRideID(offeredRideID.get(finalI1));

                       searchRideResultDetails.add(tem);

                       if(finalI == (offeredRideID.size() - 1))
                       {
                           getPassengerDetails();
                       }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void getPassengerDetails()
    {
        int i = 0;
        Log.d(TAG, "getPassengerDetails: getting passenger details.");
        Log.d(TAG, "getPassengerDetails: passengerSize: "+passengerID.size());
        for (i = 0;i < passengerID.size();i++)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                                .child("Users")
                                                .child(passengerID.get(i));
            final int finalI = i;
            final int finalI1 = i;
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        Log.d(TAG, "getPassengerDetails onDataChange: we are fetching the details of the userDetails. ");
                        UserDetails tem = new UserDetails();
                        tem.setUserID(passengerID.get(finalI));
                        tem.setCity(dataSnapshot.child("City").getValue().toString());
                        tem.setContact(dataSnapshot.child("Contact").getValue().toString());
                        tem.setDOB(dataSnapshot.child("DOB").getValue().toString());
                        tem.setFirstName(dataSnapshot.child("First Name").getValue().toString());
                        tem.setGender(dataSnapshot.child("Gender").getValue().toString());
                        tem.setLastName(dataSnapshot.child("Last Name").getValue().toString());
                        tem.setPincode(dataSnapshot.child("Pincode").getValue().toString());
                        tem.setProfilePicture(dataSnapshot.child("Profile Picture").getValue().toString());
                        passengerDetails.add(tem);

                        if(finalI1 == (passengerID.size() - 1))
                        {
                            try {
                                NotificationRiderFragmentRecyclerViewAdapter adapter = new NotificationRiderFragmentRecyclerViewAdapter(
                                        searchRideResultDetails,passengerDetails,requestID,getContext());
                                notificationRiderRecyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onDataChange: Exception: "+e.getLocalizedMessage());
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            progressBarTextView.setVisibility(View.INVISIBLE);
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
}
