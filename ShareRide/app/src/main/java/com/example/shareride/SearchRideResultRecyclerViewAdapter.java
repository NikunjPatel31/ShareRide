package com.example.shareride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchRideResultRecyclerViewAdapter extends RecyclerView.Adapter<SearchRideResultRecyclerViewAdapter.SearchRideResultDetailsViewHolder> {

    private static final String TAG = "SearchRideResultRecycle";
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Context context;

    public static ArrayList<SearchRideResultDetails> searchRideResultDetails;
    public SearchRideResultRecyclerViewAdapter(ArrayList<SearchRideResultDetails> searchRideResultDetails, Context context)
    {
        SearchRideResultRecyclerViewAdapter.searchRideResultDetails = searchRideResultDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchRideResultDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_ride_row,parent,false);
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
        holder.getRideDetails(rideDetails,rideDetails.getUserID(), position);

        holder.infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,SearchRideResultInfoActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchRideResultDetails.size();
    }

    public static class SearchRideResultDetailsViewHolder extends RecyclerView.ViewHolder
    {
        public View view;
        Button infoBtn, requestBtn;
        public SearchRideResultDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            infoBtn = (Button) view.findViewById(R.id.info_button);
            requestBtn = (Button) view.findViewById(R.id.request_button);
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
            CircleImageView riderPhoto = (CircleImageView) view.findViewById(R.id.rider_photo);
            if(!(image.equals("null")))
            {
                Picasso.get().load(Uri.parse(image)).into(riderPhoto);
            }
        }
        public void setRideName(String rideName)
        {
            TextView riderNameTV = (TextView) view.findViewById(R.id.rider_name_textview);
            riderNameTV.setText(rideName);
        }
        public void getRideDetails(final SearchRideResultDetails temSearchRideResultDetails, String userID, final int position)
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserDetails userDetails = new UserDetails();
                    userDetails.setCity(dataSnapshot.child("City").getValue().toString());
                    userDetails.setContact(dataSnapshot.child("Contact").getValue().toString());
                    userDetails.setDOB(dataSnapshot.child("DOB").getValue().toString());
                    userDetails.setFirstName(dataSnapshot.child("First Name").getValue().toString());
                    userDetails.setGender(dataSnapshot.child("Gender").getValue().toString());
                    userDetails.setLastName(dataSnapshot.child("Last Name").getValue().toString());
                    userDetails.setPincode(dataSnapshot.child("Pincode").getValue().toString());
                    userDetails.setProfilePicture(dataSnapshot.child("Profile Picture").getValue().toString());
                    userDetails.setUserID(dataSnapshot.getKey());

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
    }
}
