package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchedRideCompleteInfoActivity extends AppCompatActivity {

    private static final String TAG = "SearchedRideCompleteInf";
    private SearchRideResultDetails searchRideResultDetails;
    private UserDetails riderDetails;
    private TextView riderNameTV, riderGenderTV, riderAgeTV, riderCityTV;
    private TextView sourceLocationTV, destinationLocationTV, availabelSeatsTV, costPerSeatTV;
    private TextView carNameTV, carModelValueTV, carFuelTV, carAirConditionerTV, carVehicleNumberTV;
    private Button requestBtn, cancelBtn;
    private RecyclerView passengerListRecyclerView;
    private CircleImageView riderProfilePicture, riderCarPhoto;
    private String carID;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private int requestIDCounter = 1;
    private ArrayList<String> requestID = new ArrayList<>();
    private int requestIDChildrenCount = 0;
    private String requestKey="";
    private Boolean callPermission = false;
    private final int CALL_PHONE_REQUEST_CODE = 4;
    private Map<String, String> mapPassengerID = new HashMap<>();

    private ArrayList<String> passengerID = new ArrayList<>();
    private ArrayList<String> passengerRequestID = new ArrayList<>();
    private ArrayList<Integer> passengerChildrenCount = new ArrayList<>();
    private ArrayList<String> acceptedPassengerID = new ArrayList<>();
    private ArrayList<UserDetails> passengerDetails = new ArrayList<>();

    public void cancel(View view)
    {
        Intent intent = new Intent(SearchedRideCompleteInfoActivity.this, SearchRideResultActivity.class);
        startActivity(intent);
    }

    public void call(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + riderDetails.getContact()));
        if (callPermission) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Call Permission is not given.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCallPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST_CODE);
        }
        else
        {
            callPermission = true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            callPermission = true;
        }
        else
        {
            callPermission = false;
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
        passengerListRecyclerView.setHasFixedSize(true);
        passengerListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getCallPermission();
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
                            requestRide(searchRideResultDetails, tem);
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
            else {
                Log.d(TAG, "onStart: we are here");
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "onStart: Exception: "+e.getLocalizedMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPassengerList();
    }

    private void getIntentData()
    {
        Log.d(TAG, "getIntentData: getting intent data.");
        try {
            searchRideResultDetails = getIntent().getParcelableExtra("Ride_details");
            riderDetails = getIntent().getParcelableExtra("Rider_Details");
            carID = searchRideResultDetails.getCar_id();
            try {
                if(getIntent().getStringExtra("Activity").equals("Passenger_Notification"))
                {
                    requestBtn.setVisibility(View.INVISIBLE);
                    cancelBtn.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "getIntentData: we are here");
                }
            }
            catch (Exception e)
            {
                Log.d(TAG, "getIntentData: Exception: "+e.getLocalizedMessage());
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception: getIntentData: "+e.getLocalizedMessage());
        }

    }
    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing Widgets.");
        riderNameTV = findViewById(R.id.rider_name_textview);
        riderGenderTV = findViewById(R.id.rider_gender_textview);
        riderAgeTV = findViewById(R.id.rider_age_textview);
        riderCityTV = findViewById(R.id.rider_city_value_textview);
        sourceLocationTV = findViewById(R.id.ride_source_location_textview);
        destinationLocationTV = findViewById(R.id.ride_destination_location_textview);
        availabelSeatsTV = findViewById(R.id.ride_available_seat_value_textview);
        costPerSeatTV = findViewById(R.id.ride_cost_per_seat_textview);
        carNameTV = findViewById(R.id.rider_car_name_textview);
        carModelValueTV = findViewById(R.id.rider_car_model_value_textview);
        carFuelTV = findViewById(R.id.rider_car_fuel_value_textview);
        carAirConditionerTV = findViewById(R.id.rider_car_air_conditioner_textview);
        carVehicleNumberTV = findViewById(R.id.rider_car_vehicle_number_value_textview);
        riderProfilePicture = findViewById(R.id.rider_photo);
        riderCarPhoto = findViewById(R.id.rider_car_photo);
        requestBtn = findViewById(R.id.request_button);
        cancelBtn = findViewById(R.id.cancel_button);
        passengerListRecyclerView = findViewById(R.id.passenger_list_recycler_view);
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
            Log.d(TAG, "getCarDetails: uid: "+riderDetails.getUserID());
            Log.d(TAG, "getCarDetails: carID: "+carID);
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
                        Picasso.get().load(dataSnapshot.child("Car_Image").getValue().toString()).placeholder(R.drawable.ic_car_black_24dp).into(riderCarPhoto);
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
        try {
            String firstName = riderDetails.getFirstName();
            String lastName = riderDetails.getLastName();
            String fullName = firstName + " " + lastName;
            riderNameTV.setText(fullName);
            riderCityTV.setText(riderDetails.getCity());
            riderGenderTV.setText("( "+riderDetails.getGender()+" )");
            Log.d(TAG, "populatingWidgets: Source: "+searchRideResultDetails.getSource_Location_Name());
            sourceLocationTV.setText(searchRideResultDetails.getSource_Location_Name());
            destinationLocationTV.setText(searchRideResultDetails.getDestination_Location_Name());
            Picasso.get().load(riderDetails.getProfilePicture()).placeholder(R.drawable.ic_account_circle_black_24dp).into(riderProfilePicture);
            // this value will change on the basis of the passenger of the ride... so i will write the code for that later.
            availabelSeatsTV.setText(searchRideResultDetails.getNum_Seats());
            costPerSeatTV.setText(searchRideResultDetails.getCost_Per_Seat()+" Rs");
        } catch (Exception e) {
            Log.d(TAG, "Exception: populatingWidgets: "+e.getLocalizedMessage());
        }
    }
    private void requestRide(SearchRideResultDetails searchRideResultDetails, DatabaseReference databaseReference)
    {
        Log.d(TAG, "requestRide: requesting ride.");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Notification")
                .child("Rider")
                .child(searchRideResultDetails.getUserID())
                .child(databaseReference.getKey())
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
                    if(dataSnapshot.getKey().equals(firebaseAuth.getUid()))
                    {
                        requestIDChildrenCount = (int) dataSnapshot.getChildrenCount();
                        Log.d(TAG, "onChildAdded: user has already requested for some ride.");
                        final DatabaseReference childDB = databaseReference.child(firebaseAuth.getUid());
                        childDB.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                requestID.add(dataSnapshot.getKey());
                                if(requestIDCounter == requestIDChildrenCount)
                                {
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
                    SearchRideResultDetails tem = searchRideResultDetails;
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

    private void getPassengerList()
    {
        Log.d(TAG, "getPassengerList: this function will get the passenger list of the ride.");
        getPassengerID();

    }
    private void getPassengerID()
    {
        Log.d(TAG, "getPassengerID: this function will get the passengerID.");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //here we will get the total number of children in the registration node.
                // children count will be useful to call the getRequest( ) at the particular time.
                final int childrenCount = (int) dataSnapshot.getChildrenCount();
                DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Registration");
                mChild.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //here will we store the passengerId in the arraylist.
                        passengerID.add(dataSnapshot.getKey());
                        if(passengerID.size() == childrenCount)
                        {
                            getRequestID();
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
    private void getRequestID()
    {
        Log.d(TAG, "getRequestID: this function will get the request Id of the particular passengerID.");
        final int[] counter = {0};
        for (int i = 0; i < passengerID.size(); i++)
        {
            DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Registration").child(passengerID.get(i));
            mChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    counter[0] += (int) dataSnapshot.getChildrenCount();
                    passengerChildrenCount.add((int) dataSnapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final int finalI = i;
            final int[] localCounter = {0};
            mChild.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    localCounter[0]++;
                    mapPassengerID.put(dataSnapshot.getKey(),passengerID.get(finalI));
                    Log.d(TAG, "getRequestID: onChildAdded: map: "+mapPassengerID.toString());
                    passengerRequestID.add(dataSnapshot.getKey());
                    if (localCounter[0] == (counter[0] - 1))
                    {
                        //now we have the requestID of all passenger.;
                        Log.d(TAG, "getRequestID: onChildAdded: finalI: "+finalI);
                        getfinalPassengerList();
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
    private void getfinalPassengerList()
    {
        Log.d(TAG, "getfinalPassengerList: this function will get the passenger details.");
        final int[] j = {0};
        int k = 0;
        final int[] counter = {0};
        for (final int[] i = {0}; i[0] < passengerID.size(); i[0]++,k++)
        {
            DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Registration")
                    .child(passengerID.get(i[0])).child(passengerRequestID.get(k));

            mChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        counter[0]++;
                        if (dataSnapshot.child("Status").getValue().equals("Accepted"))
                        {
                            if(dataSnapshot.child("Offer_Ride_ID").getValue().equals(searchRideResultDetails.getRideID()))
                            {
                                acceptedPassengerID.add(mapPassengerID.get(dataSnapshot.getKey()));
                            }
                        }
                    }
                    if(counter[0] == passengerRequestID.size())
                    {
                        // we will have the passengerId list who are going in the ride...
                        getPassengerDetails();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            try {
                if (j[0] == (passengerChildrenCount.get(i[0]) - 1))
                {
                    j[0] = 0;
                }
                else {
                    j[0]++;
                    i[0]--;
                }
            }
            catch (Exception e)
            {
                Log.d(TAG, "getPassengerDetails: onDataChange: Exception: "+e.getLocalizedMessage());
            }
        }
    }
    private void getPassengerDetails()
    {
        Log.d(TAG, "getPassengerDetails: this function will get the details of the passenger.");
        for (int i = 0;i < acceptedPassengerID.size(); i++)
        {
            DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Users").child(acceptedPassengerID.get(i));
            final int finalI = i;
            mChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserDetails tem = new UserDetails();
                    tem.setUserID(dataSnapshot.getKey());
                    tem.setCity(dataSnapshot.child("City").getValue().toString());
                    tem.setDOB(dataSnapshot.child("DOB").getValue().toString());
                    tem.setFirstName(dataSnapshot.child("First Name").getValue().toString());
                    tem.setLastName(dataSnapshot.child("Last Name").getValue().toString());
                    tem.setGender(dataSnapshot.child("Gender").getValue().toString());
                    tem.setPincode(dataSnapshot.child("Pincode").getValue().toString());
                    tem.setProfilePicture(dataSnapshot.child("Profile Picture").getValue().toString());
                    tem.setContact(dataSnapshot.child("Contact").getValue().toString());

                    passengerDetails.add(tem);

                    if(finalI == (acceptedPassengerID.size() - 1))
                    {
                        Log.d(TAG, "getPassengerDetails: onDataChange: passengerDetails.size: "+passengerDetails.size());
                        try {
                            PassengerListRecyclerViewAdapter adapter = new PassengerListRecyclerViewAdapter(passengerDetails);
                            passengerListRecyclerView.setAdapter(adapter);
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG, "getPassengerDetails: onDataChange: Exception: "+e.getLocalizedMessage());
                        }
//
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
