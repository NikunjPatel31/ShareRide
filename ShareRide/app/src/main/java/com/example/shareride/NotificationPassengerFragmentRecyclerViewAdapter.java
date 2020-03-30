package com.example.shareride;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationPassengerFragmentRecyclerViewAdapter extends RecyclerView.Adapter<NotificationPassengerFragmentRecyclerViewAdapter.AcceptRideNotificationViewHolder>{
    @NonNull
    ArrayList<SearchRideResultDetails> searchRideResultDetails;
    ArrayList<UserDetails> riderDetails;
    Context context;

    public NotificationPassengerFragmentRecyclerViewAdapter(@NonNull ArrayList<SearchRideResultDetails> searchRideResultDetails, ArrayList<UserDetails> riderDetails, Context context) {
        this.searchRideResultDetails = searchRideResultDetails;
        this.riderDetails = riderDetails;
        this.context = context;
    }

    @Override
    public AcceptRideNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.requested_ride_row,parent,false);
        return new AcceptRideNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AcceptRideNotificationViewHolder holder, int position) {

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
    }

    @Override
    public int getItemCount() {
        return searchRideResultDetails.size();
    }

    public static class AcceptRideNotificationViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        public AcceptRideNotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }
        public void setRiderPhoto(String image)
        {
            CircleImageView riderPhoto = view.findViewById(R.id.rider_photo);
            Picasso.get().load(image).placeholder(R.drawable.ic_account_circle_black_24dp).into(riderPhoto);
        }
        public void setRiderName(String riderName)
        {
            TextView riderNameTV = view.findViewById(R.id.rider_name_textview);
            riderNameTV.setText(riderName);
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
        public void setDate(String date) {
            TextView dateTV = (TextView) view.findViewById(R.id.date_value_textview);
            dateTV.setText(date);
        }
    }
}
