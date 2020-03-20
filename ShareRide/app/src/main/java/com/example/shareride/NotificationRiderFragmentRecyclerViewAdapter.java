package com.example.shareride;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationRiderFragmentRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRiderFragmentRecyclerViewAdapter.RequestRideNotificationViewHolder>{

    private static final String TAG = "NotificationRiderFragme";
    public static ArrayList<SearchRideResultDetails> searchRideResultDetails;
    public static ArrayList<UserDetails> passengerDetails;
    public Context context;

    public NotificationRiderFragmentRecyclerViewAdapter(ArrayList<SearchRideResultDetails> searchRideResultDetails, ArrayList<UserDetails> passengerDetails, Context context)
    {
        NotificationRiderFragmentRecyclerViewAdapter.searchRideResultDetails = searchRideResultDetails;
        NotificationRiderFragmentRecyclerViewAdapter.passengerDetails = passengerDetails;
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
    public void onBindViewHolder(@NonNull RequestRideNotificationViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: binding the data to the recycler view.");
        UserDetails passengerDeatilsTem = NotificationRiderFragmentRecyclerViewAdapter.passengerDetails.get(position);
        SearchRideResultDetails searchRideResultDetailsTem = NotificationRiderFragmentRecyclerViewAdapter.searchRideResultDetails.get(position);
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
        public RequestRideNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
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
