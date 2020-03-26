package com.example.shareride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchRideResultRecyclerViewAdapter extends RecyclerView.Adapter<SearchRideResultRecyclerViewAdapter.SearchRideResultDetailsViewHolder> {

    private static final String TAG = "SearchRideResultRecycle";
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Context context;
    public static UserDetails userDetails=null;
    public static String riderUID=null;
    private boolean requestFlag = false;
    private String requestKey = "";

    public static ArrayList<SearchRideResultDetails> searchRideResultDetails;
    public SearchRideResultRecyclerViewAdapter(ArrayList<SearchRideResultDetails> searchRideResultDetails, Context context, FirebaseAuth mAuth)
    {
        SearchRideResultRecyclerViewAdapter.searchRideResultDetails = searchRideResultDetails;
        this.context = context;
        this.mAuth = mAuth;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public SearchRideResultDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_ride_row,parent,false);
        return new SearchRideResultDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchRideResultDetailsViewHolder holder, final int position) {
        SearchRideResultDetails rideDetails = searchRideResultDetails.get(position);
        Log.d(TAG, "onBindViewHolder: userID: "+rideDetails.getUserID());
        holder.setCostPerSeat(rideDetails.getCost_Per_Seat());
        holder.setDate(rideDetails.getDate());
        holder.setDestinationLocationName(rideDetails.getDestination_Location_Name());
        holder.setNumSeats(rideDetails.getNum_Seats());
        holder.setSourceLocationName(rideDetails.getSource_Location_Name());
        holder.setTime(rideDetails.getTime());
        holder.getRideDetails(rideDetails,rideDetails.getUserID(), position);
        holder.riderPhoto.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recycler_view_image));
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recycler_view_animation));
        holder.infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,SearchRideResultInfoActivity.class);
                try {
                    SearchRideResultDetails tem = searchRideResultDetails.get(position);
                    Log.d(TAG, "onClick: Ride_ID: "+tem.getRideID());
                    intent.putExtra("Ride_details",searchRideResultDetails.get(position));
                    userDetails = tem.riderDetails;
                    intent.putExtra("Rider_Details",userDetails);
                    intent.putExtra("Request_Flag",requestFlag);
                    intent.putExtra("Rider_UID",riderUID);
                    context.startActivity(intent);
                }
                catch (Exception e)
                {
                    Log.d(TAG, "onClick: Exception: "+e.getLocalizedMessage());
                }

            }
        });
        holder.checkRequestRide(position);
        holder.requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mChildDB = databaseReference.child("Registration").child(mAuth.getUid());
                final DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference().child("Notification").child("Rider");
                if(holder.requestBtn.getText().equals("Request"))
                {
                    DatabaseReference tem = mChildDB.push();
                    requestKey = tem.getKey();
                    holder.setRequestKey(tem.getKey());
                    Log.d(TAG, "onClick: request key: "+requestKey);
                    requestRide(searchRideResultDetails.get(position), tem);
                    holder.requestBtn.setText("Requested");
                    requestFlag = true;
                }
                else if(holder.requestBtn.getText().equals("Requested"))
                {
                    Log.d(TAG, "onClick: requestID: "+holder.getRequestKey());
                    DatabaseReference tem = mChildDB.child(holder.getRequestKey());
                    tem.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Log.d(TAG, "onComplete: request canceled from the registration.");
                                SearchRideResultDetails details = searchRideResultDetails.get(position);
                                Log.d(TAG, "onComplete: rider_id: "+details.getUserID());
                                DatabaseReference temNotification = notificationDB.child(details.getUserID()).child(holder.getRequestKey());
                                temNotification.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Log.d(TAG, "onComplete: request Canceled from the notification");
                                            holder.requestBtn.setText("Request");
                                            requestFlag = false;
                                        }
                                    }
                                });
                                //holder.requestBtn.setText("Request");
                                requestFlag = false;
                            }
                            else
                            {
                                Log.d(TAG, "onComplete: request not canceled. Exception: "+task.getException());
                                requestFlag = true;
                            }
                        }
                    });
                }
                else if(holder.requestBtn.getText().equals("Accepted"))
                {
                    Toast.makeText(context, "Your ride is already requested.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestRide(SearchRideResultDetails searchRideResultDetails,DatabaseReference databaseReference)
    {
        Log.d(TAG, "requestRide: requesting ride.");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Notification")
                                                                                            .child("Rider")
                                                                                            .child(searchRideResultDetails.getUserID())
                                                                                            .child(databaseReference.getKey())
                                                                                            .child(searchRideResultDetails.getRideID());
        databaseReference1.child("Passenger_ID").setValue(mAuth.getUid());
        Log.d(TAG, "requestRide: ride_ID: "+searchRideResultDetails.getRideID());
        databaseReference.child("Offer_Ride_ID").setValue(searchRideResultDetails.getRideID());
        databaseReference.child("Status").setValue("Not Accepted");
    }

    @Override
    public int getItemCount() {
        return searchRideResultDetails.size();
    }

    public static class SearchRideResultDetailsViewHolder extends RecyclerView.ViewHolder
    {
        public View view;
        Button infoBtn, requestBtn;
        private int requestIDCounter = 1;
        ArrayList<String> requestID = new ArrayList<>();
        int requestIDChildrenCount = 0;
        String requestKey="";
        DatabaseReference databaseReference;
        private boolean flag = false;
        static boolean requestViewValue;
        private CardView cardView;
        private CircleImageView riderPhoto;

        public SearchRideResultDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            infoBtn = (Button) view.findViewById(R.id.info_button);
            requestBtn = (Button) view.findViewById(R.id.request_button);
            cardView = (CardView) view.findViewById(R.id.inner_cardview);
            riderPhoto = (CircleImageView) view.findViewById(R.id.rider_photo);
        }

        public void setCostPerSeat(String costPerSeat)
        {
            TextView costPerSeatTV = (TextView) view.findViewById(R.id.cost_per_seat_textview);
            costPerSeatTV.setText(costPerSeat);
        }
        public void setDate(String date)
        {
            TextView dateTV = (TextView) view.findViewById(R.id.date_value_textview);
            dateTV.setText(date);
        }
        public void setDestinationLocationName(String destinationLocationName)
        {
            TextView destinationLocationNameTV =(TextView) view.findViewById(R.id.destination_location_textview);
            destinationLocationNameTV.setText(destinationLocationName);
        }
        public void setNumSeats(String numSeats)
        {
            TextView numSeatsTV = (TextView) view.findViewById(R.id.seats_value_textview);
            numSeatsTV.setText(numSeats);
        }
        public void setSourceLocationName(String sourceLocationName)
        {
            TextView sourceLocationNameTV = (TextView) view.findViewById(R.id.source_location_textview);
            sourceLocationNameTV.setText(sourceLocationName);
        }
        public void setTime(String time)
        {
            TextView timeTV = (TextView) view.findViewById(R.id.time_value_textview);
            timeTV.setText(time);
        }
        public void setRiderPhoto(String image)
        {
            if(!(image.equals("null")))
            {
                Picasso.get().load(Uri.parse(image)).placeholder(R.drawable.ic_account_circle_black_24dp).into(riderPhoto);
            }
            else
            {
                Log.d(TAG, "setRiderPhoto: rider profile picture is null");
            }
        }
        public void setRideName(String rideName)
        {
            TextView riderNameTV = (TextView) view.findViewById(R.id.rider_name_textview);
            riderNameTV.setText(rideName);
        }
        public void getRideDetails(final SearchRideResultDetails temSearchRideResultDetails, String userID, final int position)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(temSearchRideResultDetails.getUserID());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userDetails = new UserDetails();
                    userDetails.setCity(dataSnapshot.child("City").getValue().toString());
                    userDetails.setContact(dataSnapshot.child("Contact").getValue().toString());
                    userDetails.setDOB(dataSnapshot.child("DOB").getValue().toString());
                    userDetails.setFirstName(dataSnapshot.child("First Name").getValue().toString());
                    userDetails.setGender(dataSnapshot.child("Gender").getValue().toString());
                    userDetails.setLastName(dataSnapshot.child("Last Name").getValue().toString());
                    userDetails.setPincode(dataSnapshot.child("Pincode").getValue().toString());
                    userDetails.setProfilePicture(dataSnapshot.child("Profile Picture").getValue().toString());
                    //userDetails.setUserID(dataSnapshot.getKey());
                    riderUID = dataSnapshot.getKey();
                    temSearchRideResultDetails.riderDetails = userDetails;

                    searchRideResultDetails.set(position, temSearchRideResultDetails);

                    String firstName = userDetails.getFirstName();
                    String lastName = userDetails.getLastName();
                    setRideName(firstName+" "+lastName);
                    setRiderPhoto(userDetails.getProfilePicture());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void checkRequestRide(final int position)
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
                                Log.d(TAG, "onChildAdded: request_ID Children Count: "+dataSnapshot.getChildrenCount());
                                requestID.add(dataSnapshot.getKey());
                                if(requestIDCounter == requestIDChildrenCount)
                                {
                                    Log.d(TAG, "onChildAdded: requestID size: "+requestID.size());
                                    getEachRequestIDDetails(childDB,requestID,position);
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
                                Log.d(TAG, "onChildRemoved: dataSnapshot children: "+dataSnapshot.getChildrenCount());
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
                    Log.d(TAG, "onChildChanged: inner dataSnapshot children: "+dataSnapshot.toString());
                    requestBtn.setText("Requested");
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved: inner dataSnapshot children: "+dataSnapshot.toString());
                    requestBtn.setText("Request");
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        private void getEachRequestIDDetails(DatabaseReference childDB, final ArrayList<String> requestID, final int position)
        {
            Log.d(TAG, "getEachRequestIDDetails: getting each request ID details"+requestID.size());
            setDataSnapshot(childDB);
            for(int i = 0;i < requestID.size();i++)
            {
                DatabaseReference mChildDB = childDB.child(requestID.get(i));
                mChildDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, " 384 onDataChange: offered_RideID: "+dataSnapshot.child("Offer_Ride_ID").getValue());
                        Log.d(TAG, " 384 onDataChange: status: "+dataSnapshot.child("Status").getValue());
                        SearchRideResultDetails tem = searchRideResultDetails.get(position);
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        Log.d(TAG, " 384 onDataChange: ride ID: "+tem.getRideID());
                        if(tem.getRideID().equals(dataSnapshot.child("Offer_Ride_ID").getValue()))
                        {
                            Log.d(TAG, " 384 onDataChange: user has requested the ride.");
                            Log.d(TAG, " 384 onDataChange: user has database Ride_ID: "+dataSnapshot.child("Offer_Ride_ID").getValue());
                            Log.d(TAG, " 384 onDataChange: user has Ride_ID: "+tem.getRideID());
                            Log.d(TAG, "onDataChange: passenger id: "+dataSnapshot.getKey());
                            setRequestKey(dataSnapshot.getKey());
                            if(dataSnapshot.child("Status").getValue().equals("Accepted"))
                            {
                                Log.d(TAG, "onDataChange: rider has accepted the request.");
                                requestBtn.setText("Accepted");
                            }
                            else
                            {
                                Log.d(TAG, "onDataChange: rider has not accepted the request");
                                requestBtn.setText("Requested");
                                flag = true;
                            }
                        }
                        else
                        {
                            if(!requestBtn.getText().equals("Accepted"))
                            {
                                if(!flag)
                                {
                                    requestBtn.setText("Request");
                                    Log.d(TAG, " 384 onDataChange: user has not requested the ride");
                                    Log.d(TAG, " 384 onDataChange: user has database Ride_ID: "+dataSnapshot.child("Offer_Ride_ID").getValue());
                                    Log.d(TAG, " 384 onDataChange: user has Ride_ID: "+tem.getRideID());
                                    Log.d(TAG, "onDataChange: passenger id: "+dataSnapshot.getKey());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        private void setDataSnapshot(DatabaseReference databaseReference)
        {
            this.databaseReference = databaseReference;
        }
        private DatabaseReference getDataSnapshot()
        {
            return databaseReference;
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
}
