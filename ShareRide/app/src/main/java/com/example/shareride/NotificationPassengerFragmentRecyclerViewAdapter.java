package com.example.shareride;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class NotificationPassengerFragmentRecyclerViewAdapter extends RecyclerView.Adapter<NotificationPassengerFragmentRecyclerViewAdapter.AcceptRideNotificationViewHolder>{
    @NonNull
    ArrayList<SearchRideResultDetails> searchRideResultDetails;
    ArrayList<UserDetails> riderDetails;
    ArrayList<String> requestID;
    Context context;
    FirebaseAuth mAuth;

    private static final String TAG = "NotificationPassengerFr";

    public NotificationPassengerFragmentRecyclerViewAdapter(@NonNull ArrayList<SearchRideResultDetails> searchRideResultDetails, ArrayList<UserDetails> riderDetails, ArrayList<String> requestID, Context context) {
        this.searchRideResultDetails = searchRideResultDetails;
        this.riderDetails = riderDetails;
        this.requestID = requestID;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public AcceptRideNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.requested_ride_row,parent,false);
        return new AcceptRideNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AcceptRideNotificationViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: position: "+position);
        UserDetails temRiderDetails = riderDetails.get(position);
        SearchRideResultDetails temSearchRideResultDetails = searchRideResultDetails.get(position);
        holder.setRiderPhoto(temRiderDetails.getProfilePicture());
        String firstName = temRiderDetails.getFirstName();
        String lastName = temRiderDetails.getLastName();
        String fullName = firstName+" "+lastName;
        holder.setRiderName(fullName);
        holder.setSourceLocationName(temSearchRideResultDetails.getSource_Location_Name());
        holder.setDestinationLocationName(temSearchRideResultDetails.getDestination_Location_Name());
        holder.setTime(temSearchRideResultDetails.getTime());
        holder.setCostPerSeat(temSearchRideResultDetails.getCost_Per_Seat());
        holder.setDate(temSearchRideResultDetails.getDate());

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final UserDetails temRiderDetails = riderDetails.get(position);
                final SearchRideResultDetails temRideDetails = searchRideResultDetails.get(position);
                final DatabaseReference notificationDB = FirebaseDatabase.getInstance().getReference().child("Notification").child("Rider");
                try {
                    DatabaseReference temNotification = notificationDB.child(temRiderDetails.getUserID()).child(requestID.get(position)).child(temRideDetails.getRideID());
                    temNotification.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            try {
                                if(task.isSuccessful())
                                {
                                    Log.d(TAG, "onComplete: request Canceled from the notification");
                                    final DatabaseReference mChild = FirebaseDatabase.getInstance().getReference().child("Offer_Ride")
                                            .child(temRiderDetails.getUserID())
                                            .child(temRideDetails.getRideID());
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
                                }
                                else
                                {
                                    Log.d(TAG, "onComplete: task Exception: "+task.getException());
                                }
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: TASK Exception: "+e.getLocalizedMessage());
                            }
                        }
                    });
                    Log.d(TAG, "onComplete: UID: "+mAuth.getUid());
                    String UID = mAuth.getUid();
                    Log.d(TAG, "onComplete: getRequestKey: "+requestID.get(position));
                    DatabaseReference mChildDB = FirebaseDatabase.getInstance().getReference().child("Registration").child(UID);
                    DatabaseReference tem = mChildDB.child(requestID.get(position));
                    try {
                        tem.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                try {
                                    if(task.isSuccessful())
                                    {
                                        Log.d(TAG, "onComplete: node from the registration is also removed.");
                                        searchRideResultDetails.remove(position);
                                        notifyItemChanged(position);
                                        notifyItemRangeRemoved(position,1);
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
                catch (Exception e)
                {
                    Log.d(TAG, "cancel_ride: Exception: "+e.getLocalizedMessage());
                }
            }
        });
        holder.infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,NotificationPassengerRouteActivity.class);
                intent.putExtra("Ride_Details",searchRideResultDetails.get(position));
                intent.putExtra("Rider_Details",riderDetails.get(position));
                Log.d(TAG, "onClick: adapter position: "+holder.getAdapterPosition());
                intent.putExtra("Adapter_Position",Integer.toString(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchRideResultDetails.size();
    }

    public static class AcceptRideNotificationViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        Button cancelBtn, infoBtn;
        AcceptRideNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            cancelBtn = view.findViewById(R.id.cancel_button);
            infoBtn = view.findViewById(R.id.info_button);
        }
        void setRiderPhoto(String image)
        {
            CircleImageView riderPhoto = view.findViewById(R.id.rider_photo);
            Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(riderPhoto);
        }
        void setRiderName(String riderName)
        {
            TextView riderNameTV = view.findViewById(R.id.rider_name_textview);
            riderNameTV.setText(riderName);
        }
        void setSourceLocationName(String sourceLocationName)
        {
            TextView sourceLocationNameTV = (TextView) view.findViewById(R.id.source_location_textview);
            sourceLocationNameTV.setText(sourceLocationName);
        }
        void setDestinationLocationName(String destinationLocationName)
        {
            TextView destinationLocationNameTV = (TextView) view.findViewById(R.id.destination_location_textview);
            destinationLocationNameTV.setText(destinationLocationName);
        }
        public void setTime(String time)
        {
            TextView timeTV = (TextView) view.findViewById(R.id.time_value_textview);
            timeTV.setText(time);
        }
        void setCostPerSeat(String costPerSeat)
        {
            TextView costPerSeatTV = (TextView) view.findViewById(R.id.cost_per_seat_textview);
            costPerSeatTV.setText(costPerSeat);
        }
        public void setDate(String date) {
            TextView dateTV = (TextView) view.findViewById(R.id.date_value_textview);
            dateTV.setText(date);
        }
    }
}
