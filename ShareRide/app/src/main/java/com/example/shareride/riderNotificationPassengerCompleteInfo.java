package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class riderNotificationPassengerCompleteInfo extends AppCompatActivity {
    private static final String TAG = "riderNotificationPassen";
    private TextView genderTV, ageTV, contactTV, cityTV;
    private ImageView passengerIV;
    private Button acceptBtn;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private UserDetails passengerDetails;
    private final int CALL_PHONE_REQUEST_CODE = 4;
    private Boolean callPermission = false;
    private FirebaseAuth mAuth;
    private String request_id;
    private String position;
    private String size;
    private SearchRideResultDetails searchRideResultDetailsTem;

    public void call(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + passengerDetails.getContact()));
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

    public void accept(View view)
    {
        if(acceptBtn.getText().toString().equals("Accept"))
        {
            final String status = "Accepted";
            DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Registration")
                    .child(passengerDetails.getUserID())
                    .child(request_id);
            mChild.child("Status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Log.d(TAG, "onBindViewHolder: onComplete: status updated.");
                        final DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Offer_Ride")
                                .child(mAuth.getUid())
                                .child(searchRideResultDetailsTem.getRideID());
                        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int availableSeats = Integer.parseInt(dataSnapshot.child("Num_Seats").getValue().toString());
                                availableSeats--;
                                mChild.child("Num_Seats").setValue(availableSeats);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        acceptBtn.setText(status);
                        startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                    }
                    else
                    {
                        Log.d(TAG, "onBindViewHolder: onComplete: status not updated: Exception: "+task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onBindViewHolder: onFailure: Exception: "+e.getLocalizedMessage());
                }
            });
        }
        else
        {
            Toast.makeText(this, "Ride is already accepted.", Toast.LENGTH_SHORT).show();
        }
    }
    public void reject(View view)
    {
        try
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Notification").child("Rider")
                    .child(mAuth.getUid())
                    .child(request_id);
            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(), "Unable to reject the request.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        DatabaseReference mChild = FirebaseDatabase.getInstance().getReference()
                                .child("Registration")
                                .child(passengerDetails.getUserID())
                                .child(request_id);
                        mChild.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(), "Unable to reject the request.", Toast.LENGTH_SHORT).show();
                                }else
                                {
                                    try
                                    {
                                        try
                                        {
                                            Notification_Rider_Fragment.requestID.remove(position);
                                            Log.d(TAG, "onComplete: inside requestID.size: "+Notification_Rider_Fragment.requestID.size());
//                                            Notification_Rider_Fragment.notificationRiderRecyclerView.getAdapter().notifyItemRemoved(Integer.parseInt(position));
                                        }
                                        catch (Exception e)
                                        {
                                            Log.d(TAG, "onComplete: first line Exception: "+e.getLocalizedMessage());
                                        }
                                        final DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Offer_Ride")
                                                .child(mAuth.getUid())
                                                .child(searchRideResultDetailsTem.getRideID());
                                        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int availableSeats = Integer.parseInt(dataSnapshot.child("Num_Seats").getValue().toString());
                                                availableSeats++;
                                                mChild.child("Num_Seats").setValue(availableSeats);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        Notification_Rider_Fragment.notificationRiderRecyclerView.getAdapter().notifyItemRangeChanged(Integer.parseInt(position), Integer.parseInt(size) - 1);
                                        Notification_Rider_Fragment.notificationRiderRecyclerView.getAdapter().notifyItemRangeRemoved(Integer.parseInt(position),1);
                                        Notification_Rider_Fragment.notificationRiderRecyclerView.getAdapter().notifyDataSetChanged();
                                        startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
                                    }
                                    catch (Exception e)
                                    {
                                        Log.d(TAG, "onComplete: notify data set change: Exception: "+e.getLocalizedMessage());
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: onReject: Exception: "+e.getLocalizedMessage());
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: onReject: Exception: "+e.getLocalizedMessage());
                }
            });

        }
        catch (Exception e)
        {
            Log.d(TAG, "onClick: onReject: Exception: "+e.getLocalizedMessage());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_notification_passenger_complete_info);
        getIntentData();
        initializeWidgets();
        initializeFirebaseInstances();
        getCallPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkStatus();
        populateWidgets();
    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        collapsingToolbarLayout = findViewById(R.id.collasping_toolbar);
        genderTV = findViewById(R.id.gender_value_textview);
        ageTV = findViewById(R.id.age_value_textview);
        contactTV = findViewById(R.id.contact_value_textview);
        cityTV = findViewById(R.id.city_value_textview);
        passengerIV = findViewById(R.id.passenger_imageView);
        acceptBtn = findViewById(R.id.accept_button);
    }
    private void initializeFirebaseInstances()
    {
        Log.d(TAG, "initializeFirebaseInstances: initializing firebase instances.");
        mAuth = FirebaseAuth.getInstance();
    }
    private void getIntentData()
    {
        Log.d(TAG, "getIntentData: getting intent data.");
        passengerDetails = getIntent().getParcelableExtra("Passenger_details");
        request_id = getIntent().getStringExtra("Request_id");
        position = getIntent().getStringExtra("Position");
        size = getIntent().getStringExtra("size");
        searchRideResultDetailsTem = getIntent().getParcelableExtra("Ride_Details");
    }
    private void populateWidgets()
    {
        Log.d(TAG, "populateWidgets: populating widgets.");
        genderTV.setText(passengerDetails.getGender());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int DOB = Integer.parseInt(passengerDetails.getDOB());
        Log.d(TAG, "populateWidgets: year: "+year);
        Log.d(TAG, "populateWidgets: DOB: "+DOB);
        ageTV.setText(Integer.toString(year - DOB));
        contactTV.setText(passengerDetails.getContact());
        cityTV.setText(passengerDetails.getCity());
        String firstName = passengerDetails.getFirstName();
        String lastName = passengerDetails.getLastName();
        String fullName = firstName + " " + lastName;
        collapsingToolbarLayout.setTitle(fullName);
        Picasso.get().load(passengerDetails.getProfilePicture()).placeholder(R.drawable.ic_account_circle_black_24dp).into(passengerIV);
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
    private void checkStatus()
    {
        Log.d(TAG, "checkStatus: checking status.");
        DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Registration")
                .child(passengerDetails.getUserID())
                .child(request_id);

        mChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try
                {
                    if(dataSnapshot.child("Status").getValue().equals("Accepted"))
                    {
                        acceptBtn.setText("Accepted");
                    }
                    else
                    {
                        acceptBtn.setText("Accept");
                    }
                }
                catch (Exception e)
                {
                    Log.d(TAG, "onDataChange: Status: Exception: "+e.getLocalizedMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
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
}
