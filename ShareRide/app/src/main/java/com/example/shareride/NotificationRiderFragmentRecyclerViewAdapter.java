package com.example.shareride;

import android.content.Context;
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
    public static ArrayList<SearchRideResultDetails> searchRideResultDetails;
    public static ArrayList<UserDetails> passengerDetails;
    public static ArrayList<String> requestID;
    public Context context;

    public NotificationRiderFragmentRecyclerViewAdapter(ArrayList<SearchRideResultDetails> searchRideResultDetails, ArrayList<UserDetails> passengerDetails, ArrayList<String> requestID, Context context)
    {
        NotificationRiderFragmentRecyclerViewAdapter.searchRideResultDetails = searchRideResultDetails;
        NotificationRiderFragmentRecyclerViewAdapter.passengerDetails = passengerDetails;
        NotificationRiderFragmentRecyclerViewAdapter.requestID = requestID;
        this.context = context;
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
                if(dataSnapshot.child("Status").getValue().equals("Accepted"))
                {
                    holder.requestBtn.setText("Accepted");
                }
                else
                {
                    holder.requestBtn.setText("Accept");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        holder.requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String status = "Accepted";
                Log.d(TAG, "onBindViewHolder: onClick: passenger_id: "+passengerDeatilsTem.getUserID());
                Log.d(TAG, "onBindViewHolder: onClick: request_id: "+NotificationRiderFragmentRecyclerViewAdapter.requestID.get(position));
                Log.d(TAG, "onBindViewHolder: onClick: offer_Ride_id: "+searchRideResultDetailsTem.getRideID());
                DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Registration")
                        .child(passengerDeatilsTem.getUserID())
                        .child(NotificationRiderFragmentRecyclerViewAdapter.requestID.get(position));
                mChild.child("Status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "onBindViewHolder: onComplete: status updated.");
                            holder.requestBtn.setText(status);
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
        });
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
        Button requestBtn, cancelBtn;
        public RequestRideNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            requestBtn = (Button) view.findViewById(R.id.request_button);
            cancelBtn = (Button) view.findViewById(R.id.cancel_button);
        }
        public void setPassengerName(String passengerName)
        {
            TextView passengerNameTV = (TextView) view.findViewById(R.id.passenger_name_textview);
            passengerNameTV.setText(passengerName);
        }
        public void setPassengerPhoto(String image)
        {
            CircleImageView passengerCircleImageView = (CircleImageView) view.findViewById(R.id.passenger_photo);
            if(image != null)
            {
                Picasso.get().load(image).into(passengerCircleImageView);
            }
        }
        public void setSourceLocationName(String sourceLocationName)
        {
            TextView sourceLocationNameTV = (TextView) view.findViewById(R.id.source_location_textview);
            sourceLocationNameTV.setText(sourceLocationName);
        }
        public void setDestinationLocationName(String destinationLocationName)
        {
            TextView destinationLocationNameTV = (TextView) view.findViewById(R.id.destination_location_textview);
            destinationLocationNameTV.setText(destinationLocationName);
        }
        public void setTime(String time)
        {
            TextView timeTV = (TextView) view.findViewById(R.id.time_value_textview);
            timeTV.setText(time);
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
    }
}
