package com.example.shareride;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationRiderFragmentRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRiderFragmentRecyclerViewAdapter.RequestRideNotificationViewHolder>{

    private static final String TAG = "NotificationRiderFragme";
    private static ArrayList<SearchRideResultDetails> searchRideResultDetails;
    private static ArrayList<UserDetails> passengerDetails;
    private static ArrayList<String> requestID;
    public Context context;
    public FirebaseAuth mAuth;

    NotificationRiderFragmentRecyclerViewAdapter(ArrayList<SearchRideResultDetails> searchRideResultDetails, ArrayList<UserDetails> passengerDetails, ArrayList<String> requestID, Context context)
    {
        NotificationRiderFragmentRecyclerViewAdapter.searchRideResultDetails = searchRideResultDetails;
        NotificationRiderFragmentRecyclerViewAdapter.passengerDetails = passengerDetails;
        NotificationRiderFragmentRecyclerViewAdapter.requestID = requestID;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public RequestRideNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ride_request_row,parent,false);
        return new RequestRideNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestRideNotificationViewHolder holder, final int position) {
        try {
            Log.d(TAG, "onBindViewHolder: binding the data to the recycler view.");
            final UserDetails passengerDeatilsTem = NotificationRiderFragmentRecyclerViewAdapter.passengerDetails.get(position);
            final SearchRideResultDetails searchRideResultDetailsTem = NotificationRiderFragmentRecyclerViewAdapter.searchRideResultDetails.get(position);
            holder.setPassengerPhoto(passengerDeatilsTem.getProfilePicture());
            String firstName = passengerDeatilsTem.getFirstName();
            String lastName = passengerDeatilsTem.getLastName();
            String fullName = firstName + " " + lastName;
            holder.setPassengerName(fullName);
            holder.setSourceLocationName(searchRideResultDetailsTem.getSource_Location_Name());
            holder.setDestinationLocationName(searchRideResultDetailsTem.getDestination_Location_Name());
            holder.setTime(searchRideResultDetailsTem.getTime());
            holder.setCostPerSeat(searchRideResultDetailsTem.getCost_Per_Seat());
            holder.setDate(searchRideResultDetailsTem.getDate());

            DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Registration")
                    .child(passengerDeatilsTem.getUserID())
                    .child(NotificationRiderFragmentRecyclerViewAdapter.requestID.get(position));

            mChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try
                    {
                        if(dataSnapshot.child("Status").getValue().equals("Accepted"))
                        {
                            holder.acceptBtn.setText("Accepted");
                        }
                        else
                        {
                            holder.acceptBtn.setText("Accept");
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

            holder.infoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,SearchRideResultInfoActivity.class);
                    intent.putExtra("Activity","Rider_Notification");
                    intent.putExtra("Rider_Details",passengerDeatilsTem);
                    //remember to change the key value to passenger_details instead of rider_details..
                    intent.putExtra("Request_id",requestID.get(position));
                    intent.putExtra("Ride_details",searchRideResultDetailsTem);
                    intent.putExtra("Position",Integer.toString(position));
                    intent.putExtra("size",Integer.toString(searchRideResultDetails.size()));

                    context.startActivity(intent);
                }
            });

            holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Notification").child("Rider")
                                .child(mAuth.getUid())
                                .child(NotificationRiderFragmentRecyclerViewAdapter.requestID.get(position));
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(context, "Unable to reject the request.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
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
                                    searchRideResultDetails.remove(position);
                                    notifyItemChanged(position);
                                    notifyItemRangeRemoved(position,1);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: onReject: Exception: "+e.getLocalizedMessage());
                            }
                        });
                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Registration")
                                .child(passengerDeatilsTem.getUserID())
                                .child(NotificationRiderFragmentRecyclerViewAdapter.requestID.get(position));
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(context, "Unable to reject the request.", Toast.LENGTH_SHORT).show();
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
            });

            holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.acceptBtn.getText().equals("Not Accepted"))
                    {
                        final String status = "Accepted";
                        DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Registration")
                                .child(passengerDeatilsTem.getUserID())
                                .child(NotificationRiderFragmentRecyclerViewAdapter.requestID.get(position));
                        mChild.child("Status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Log.d(TAG, "onBindViewHolder: onComplete: status updated.");
                                    holder.acceptBtn.setText(status);
                                    // we will decrement available seat by 1...

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
                }
            });
        }
        catch (Exception e)
        {
            Log.d(TAG, "onBindViewHolder: Exception: "+e.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: size: "+NotificationRiderFragmentRecyclerViewAdapter.searchRideResultDetails.size());
        Log.d(TAG, "getItemCount: passengerSize: "+NotificationRiderFragmentRecyclerViewAdapter.passengerDetails.size());
        return searchRideResultDetails.size();
    }

    public static class RequestRideNotificationViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        Button acceptBtn, rejectBtn, infoBtn;
        RequestRideNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            acceptBtn = view.findViewById(R.id.accept_button);
            rejectBtn = view.findViewById(R.id.reject_button);
            infoBtn = view.findViewById(R.id.info_button);
        }
        void setPassengerName(String passengerName)
        {
            TextView passengerNameTV = view.findViewById(R.id.passenger_name_textview);
            passengerNameTV.setText(passengerName);
        }
        void setPassengerPhoto(String image)
        {
            CircleImageView passengerCircleImageView = view.findViewById(R.id.passenger_photo);
            Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(passengerCircleImageView);
        }
        void setSourceLocationName(String sourceLocationName)
        {
            TextView sourceLocationNameTV = view.findViewById(R.id.source_location_textview);
            sourceLocationNameTV.setText(sourceLocationName);
        }
        void setDestinationLocationName(String destinationLocationName)
        {
            TextView destinationLocationNameTV = view.findViewById(R.id.destination_location_textview);
            destinationLocationNameTV.setText(destinationLocationName);
        }
        public void setTime(String time)
        {
            TextView timeTV = view.findViewById(R.id.time_value_textview);
            timeTV.setText(time);
        }
        void setCostPerSeat(String costPerSeat)
        {
            TextView costPerSeatTV = view.findViewById(R.id.cost_per_seat_textview);
            costPerSeatTV.setText(costPerSeat);
        }
        public void setDate(String date)
        {
            TextView dateTV = view.findViewById(R.id.date_value_textview);
            dateTV.setText(date);
        }
    }
}
