package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchedRideCompleteInfoActivity extends AppCompatActivity {

    private static final String TAG = "SearchedRideCompleteInf";
    private SearchRideResultDetails searchRideResultDetails;
    private UserDetails riderDetails;
    private TextView riderNameTV, riderGenderTV, riderAgeTV, riderCityTV;
    private TextView sourceLocationTV, destinationLocationTV, availabelSeatsTV, costPerSeatTV;
    private TextView carNameTV, carModelValueTV, carFuelTV, carAirConditionerTV, carVehicleNumberTV;
    private Button requestBtn, cancelBtn, cancelRideBtn;
    private CircleImageView riderProfilePicture, riderCarPhoto;
    private String carID;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private int requestIDCounter = 1;
    ArrayList<String> requestID = new ArrayList<>();
    int requestIDChildrenCount = 0;
    String requestKey="";
    String adapterPosition;

    public void cancel(View view)
    {
        Intent intent = new Intent(SearchedRideCompleteInfoActivity.this, SearchRideResultActivity.class);
        startActivity(intent);
    }
    public void cancel_ride(View view)
    {
        Log.d(TAG, "onComplete: UID: "+mAuth.getUid());
        String UID = mAuth.getUid();
        Log.d(TAG, "onComplete: getRequestKey: "+getRequestKey());
        Log.d(TAG, "onComplete: adapter position: "+adapterPosition);
        DatabaseReference mChildDB = databaseReference.child("Registration").child(UID);
        DatabaseReference tem = mChildDB.child(getRequestKey());
        try {
            tem.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    try {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: node from the registration is also removed.");
//                                Notification_Passenger_Fragment.notificationPassengerRecyclerView
//                                        .getAdapter()
//                                        .notifyItemRemoved(Integer.parseInt(adapterPosition));
//
//                                Notification_Passenger_Fragment.notificationPassengerRecyclerView
//                                        .getAdapter()
//                                        .notifyItemRangeRemoved(Integer.parseInt(adapterPosition),1);
//
//                                Notification_Passenger_Fragment.notificationPassengerRecyclerView
//                                        .getAdapter()
//                                        .notifyDataSetChanged();
                        }
                        else {
                            Log.d(TAG, "onComplete: task Exception: "+task.getException());
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "onComplete: registration Task's Exception: "+e.getLocalizedMessage());
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.d(TAG, "onComplete: registration Exception: "+e.getLocalizedMessage());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_ride_complete_info);
        getWindow().setBackgroundDrawableResource(R.drawable.background5);
        initializeFirebaseInstance();
        initializeWidgets();
        getIntentData();
        getCarDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();
        populatingWidgets();
        try
        {
            checkRequestRide();
            if(!(getIntent().getStringExtra("Activity").equals("Passenger_Notification")))
            {
                requestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference mChildDB = databaseReference.child("Registration").child(mAuth.getUid());
                        DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference().child("Notification").child("Rider");
                        if(requestBtn.getText().equals("Request"))
                        {
                            DatabaseReference tem = mChildDB.push();
                            requestKey = tem.getKey();
                            setRequestKey(tem.getKey());
                            Log.d(TAG, "onClick: request key: "+requestKey);
                            requestRide(searchRideResultDetails);
                            requestBtn.setText("Requested");
//                        requestFlag = true;
                            SearchRideResultRecyclerViewAdapter.SearchRideResultDetailsViewHolder.requestViewValue = true;
                            Intent intent = new Intent(SearchedRideCompleteInfoActivity.this, SearchRideResultActivity.class);
                            startActivity(intent);
                        }
                        else if(requestBtn.getText().equals("Requested"))
                        {
                            Log.d(TAG, "onClick: requestID: "+getRequestKey());
                            DatabaseReference tem = mChildDB.child(getRequestKey());
                            tem.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Log.d(TAG, "onComplete: request canceled.");
                                        requestBtn.setText("Request");
//                                    requestFlag = false;
                                        SearchRideResultRecyclerViewAdapter.SearchRideResultDetailsViewHolder.requestViewValue = false;
                                    }
                                    else
                                    {
                                        Log.d(TAG, "onComplete: request not canceled. Exception: "+task.getException());
//                                    requestFlag = true;
                                    }
                                }
                            });
                            SearchRideResultDetails details = searchRideResultDetails;
                            Log.d(TAG, "onClick: rider_id: "+details.getUserID());
                            Log.d(TAG, "onClick: ride_id: "+details.getRideID());
                            DatabaseReference temNotification = notificationDB.child(details.getUserID()).child(details.getRideID());
                            temNotification.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Log.d(TAG, "onComplete: request Canceled from the notification");
                                        requestBtn.setText("Request");
                                        SearchRideResultRecyclerViewAdapter.SearchRideResultDetailsViewHolder.requestViewValue = false;
//                                    requestFlag = false;
                                    }
                                }
                            });
                            Intent intent = new Intent(SearchedRideCompleteInfoActivity.this, SearchRideResultActivity.class);
                            startActivity(intent);
                        }
                        else if(requestBtn.getText().equals("Accepted"))
                        {
                            Toast.makeText(SearchedRideCompleteInfoActivity.this, "Your ride is already requested.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "onStart: Exception: "+e.getLocalizedMessage());
        }

    }

    private void getIntentData()
    {
        Log.d(TAG, "getIntentData: getting intent data.");
        searchRideResultDetails = getIntent().getParcelableExtra("Ride_Details");
        riderDetails = getIntent().getParcelableExtra("Rider_Details");
        carID = searchRideResultDetails.getCar_id();
        try {
            if(getIntent().getStringExtra("Activity").equals("Passenger_Notification"))
            {
                cancelRideBtn.setVisibility(View.VISIBLE);
                requestBtn.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
                adapterPosition = getIntent().getStringExtra("Adapter_Position");
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "getIntentData: Exception: "+e.getLocalizedMessage());
        }
    }
    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing Widgets.");
        riderNameTV = (TextView) findViewById(R.id.rider_name_textview);
        riderGenderTV = (TextView) findViewById(R.id.rider_gender_textview);
        riderAgeTV = (TextView) findViewById(R.id.rider_age_textview);
        riderCityTV = (TextView) findViewById(R.id.rider_city_value_textview);
        sourceLocationTV = (TextView) findViewById(R.id.ride_source_location_textview);
        destinationLocationTV = (TextView) findViewById(R.id.ride_destination_location_textview);
        availabelSeatsTV = (TextView) findViewById(R.id.ride_available_seat_value_textview);
        costPerSeatTV = (TextView) findViewById(R.id.ride_cost_per_seat_textview);
        carNameTV = (TextView) findViewById(R.id.rider_car_name_textview);
        carModelValueTV = (TextView) findViewById(R.id.rider_car_model_value_textview);
        carFuelTV = (TextView) findViewById(R.id.rider_car_fuel_value_textview);
        carAirConditionerTV = (TextView) findViewById(R.id.rider_car_air_conditioner_textview);
        carVehicleNumberTV = (TextView) findViewById(R.id.rider_car_vehicle_number_value_textview);
        riderProfilePicture = (CircleImageView) findViewById(R.id.rider_photo);
        riderCarPhoto = (CircleImageView) findViewById(R.id.rider_car_photo);
        requestBtn = (Button) findViewById(R.id.request_button);
        cancelBtn = findViewById(R.id.cancel_button);
        cancelRideBtn = findViewById(R.id.cancel_ride_button);
    }
    private void initializeFirebaseInstance()
    {
        Log.d(TAG, "initializeFirebaseInstance: initializing firebase instance.");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }
    private void getCarDetails()
    {
        Log.d(TAG, "getCarDetails: getting car details.");

        try
        {
            Log.d(TAG, "getCarDetails: carID: "+carID);
            Log.d(TAG, "getCarDetails: userID: "+riderDetails.getUserID());
            DatabaseReference mChildDB = databaseReference.child("Cars").child(riderDetails.getUserID()).child(carID);
            mChildDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try
                    {
                        carNameTV.setText(dataSnapshot.child("Car_Name").getValue().toString());
                        carAirConditionerTV.setText(dataSnapshot.child("Air_Conditioner").getValue().toString());
                        carFuelTV.setText(dataSnapshot.child("Fuel").getValue().toString());
                        carModelValueTV.setText(dataSnapshot.child("Model_Year").getValue().toString());
                        carVehicleNumberTV.setText(dataSnapshot.child("Vehicle_Number").getValue().toString());
                        Picasso.get().load(dataSnapshot.child("Car_Image").getValue().toString()).into(riderCarPhoto);
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "onDataChange: Exception: "+e.getLocalizedMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {
            Log.d(TAG, "getCarDetails: Exception: "+e.getLocalizedMessage());
        }
    }
    private void populatingWidgets()
    {
        Log.d(TAG, "populatingWidgets: populating widgets.");
        String firstName = riderDetails.getFirstName();
        String lastName = riderDetails.getLastName();
        String fullName = firstName + " " + lastName;
        riderNameTV.setText(fullName);
        riderCityTV.setText(riderDetails.getCity());
        riderGenderTV.setText("( "+riderDetails.getGender()+" )");
        sourceLocationTV.setText(searchRideResultDetails.getSource_Location_Name());
        destinationLocationTV.setText(searchRideResultDetails.getDestination_Location_Name());
        Picasso.get().load(riderDetails.getProfilePicture()).into(riderProfilePicture);
        // this value will change on the basis of the passenger of the ride... so i will write the code for that later.
        availabelSeatsTV.setText(searchRideResultDetails.getNum_Seats());
        costPerSeatTV.setText(searchRideResultDetails.getCost_Per_Seat()+" Rs");
    }
    private void requestRide(SearchRideResultDetails searchRideResultDetails)
    {
        Log.d(TAG, "requestRide: requesting ride.");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Notification")
                .child("Rider")
                .child(searchRideResultDetails.getUserID())
                .child(searchRideResultDetails.getRideID());
        databaseReference1.child("Passenger_ID").setValue(mAuth.getUid());
        DatabaseReference mChildDB = databaseReference.child("Registration").child(mAuth.getUid()).push();
        mChildDB.child("Offer_Ride_ID").setValue(searchRideResultDetails.getRideID());
        mChildDB.child("Status").setValue("Not Accepted");
    }

    private void checkRequestRide()
    {
        try
        {
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            Log.d(TAG, "checkRequestRide: checking if the ride is already requested or not.");
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG, "onChildAdded: All Passenger UID: "+dataSnapshot.getKey());
                    Log.d(TAG, "onChildAdded: children count: "+dataSnapshot.getChildrenCount());
                    if(dataSnapshot.getKey().equals(firebaseAuth.getUid()))
                    {
                        requestIDChildrenCount = (int) dataSnapshot.getChildrenCount();
                        Log.d(TAG, "onChildAdded: user has already requested for some ride.");
                        final DatabaseReference childDB = databaseReference.child(firebaseAuth.getUid());
                        childDB.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                Log.d(TAG, "onChildAdded: this child listener will get the request id.");
                                Log.d(TAG, "onChildAdded: all request ID: "+dataSnapshot.getKey());
                                requestID.add(dataSnapshot.getKey());
                                if(requestIDCounter == requestIDChildrenCount)
                                {
                                    Log.d(TAG, "onChildAdded: requestID size: "+requestID.size());
                                    getEachRequestIDDetails(childDB,requestID);
                                }
                                else
                                {
                                    Log.d(TAG, "onChildAdded: else value: "+requestIDCounter);
                                }
                                requestIDCounter++;

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
                    else
                    {
                        Log.d(TAG, "onChildAdded: user have not requested for any ride till now.");
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
            Log.d(TAG, "checkRequestRide: Exception: "+e.getLocalizedMessage());
        }
    }
    private void getEachRequestIDDetails(DatabaseReference childDB, ArrayList<String> requestID)
    {
        Log.d(TAG, "getEachRequestIDDetails: getting each request ID details"+requestID.size());
        for(int i = 0;i < requestID.size();i++)
        {
            DatabaseReference mChildDB = childDB.child(requestID.get(i));
            mChildDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: offered_RideID: "+dataSnapshot.child("Offer_Ride_ID").getValue());
                    Log.d(TAG, "onDataChange: status: "+dataSnapshot.child("Status").getValue());
                    SearchRideResultDetails tem = searchRideResultDetails;
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    Log.d(TAG, "onDataChange: ride ID: "+tem.getRideID());
                    if(tem.getRideID().equals(dataSnapshot.child("Offer_Ride_ID").getValue()))
                    {
                        Log.d(TAG, "onDataChange: user has requested the ride.");
                        setRequestKey(dataSnapshot.getKey());
                        if(dataSnapshot.child("Status").getValue().equals("Accepted"))
                        {
                            Log.d(TAG, "onDataChange: rider has accepted the request.");
                            requestBtn.setText("Accepted");
                        }
                        else
                        {
                            requestBtn.setText("Requested");
                        }
                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: user has not requested the ride");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setRequestKey(String requestKey)
    {
        this.requestKey = requestKey;
    }
    private String getRequestKey()
    {
        return requestKey;
    }
}
