package com.example.shareride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchRideResultRecyclerViewAdapter extends RecyclerView.Adapter<SearchRideResultRecyclerViewAdapter.SearchRideResultDetailsViewHolder> {

    ArrayList<SearchRideResultDetails> searchRideResultDetails;
    public SearchRideResultRecyclerViewAdapter(ArrayList<SearchRideResultDetails> searchRideResultDetails)
    {
        this.searchRideResultDetails = searchRideResultDetails;
    }

    @NonNull
    @Override
    public SearchRideResultDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.offered_ride_row,parent,false);
        return new SearchRideResultDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRideResultDetailsViewHolder holder, int position) {

        SearchRideResultDetails rideDetails = searchRideResultDetails.get(position);

        holder.setCostPerSeat(rideDetails.getCost_Per_Seat());
        holder.setDate(rideDetails.getDate());
        holder.setDestinationLocationName(rideDetails.getDestination_Location_Name());
        holder.setNumSeats(rideDetails.getNum_Seats());
        holder.setSourceLocationName(rideDetails.getSource_Location_Name());
        holder.setTime(rideDetails.getTime());
    }

    @Override
    public int getItemCount() {
        return searchRideResultDetails.size();
    }

    public static class SearchRideResultDetailsViewHolder extends RecyclerView.ViewHolder
    {
        public View view;
        public SearchRideResultDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
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
    }
}
