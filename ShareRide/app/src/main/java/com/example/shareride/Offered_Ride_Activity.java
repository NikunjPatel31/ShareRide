package com.example.shareride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Offered_Ride_Activity extends AppCompatActivity {

    private static final String TAG = "Offered_Ride_Activity";

    private RecyclerView recyclerView;

    private FirebaseAuth mAtuh;
    private DatabaseReference mdatabaseReference;
    private FirebaseRecyclerOptions<OfferedRideDetails> options;
    private FirebaseRecyclerAdapter<OfferedRideDetails, OfferedRideDetailsViewHolder> firebaseRecyclerAdapter;

    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offered__ride_);

        initializeWidgets();
        intializeFirebaseInstances();
        initiaizingOption();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initalizingFirebaseRecyclerAdapter();
    }

//    protected void onStart() {
//        super.onStart();
//
//        initalizingFirebaseRecyclerAdapter();
//
//    }

    private void initializeWidgets()
    {
        Log.d(TAG, "initializeWidgets: initializing widgets.");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_offered_ride);
    }

    private void intializeFirebaseInstances()
    {
        Log.d(TAG, "intializeFirebaseInstances: initializing Firebase instances.");
        mAtuh = FirebaseAuth.getInstance();
        UID = mAtuh.getUid();
        Log.d(TAG, "intializeFirebaseInstances: UID: "+UID);
        mdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Offer_Ride").child(UID);
    }

    private void initiaizingOption()
    {
        Log.d(TAG, "initiaizingOption: initializing firebase recyclerview option.");
        options = new FirebaseRecyclerOptions.Builder<OfferedRideDetails>().
                setQuery(mdatabaseReference, OfferedRideDetails.class)
                .build();
    }

    private void initalizingFirebaseRecyclerAdapter()
    {
        Log.d(TAG, "initalizingFirebaseRecyclerAdapter: initializing firebase recycler adapter.");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<OfferedRideDetails, OfferedRideDetailsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OfferedRideDetailsViewHolder holder, int position, @NonNull OfferedRideDetails model) {
                Log.d(TAG, "onBindViewHolder: binding data into the recycler view.");
                holder.setSourceLocation(getApplicationContext(),model.getSource_Location());
                holder.setDestinationLocation(getApplicationContext(),model.getDestination_Location());
                holder.setTime(model.getTime());
                holder.setDate(model.getDate());
                holder.setCost(model.getCost_Per_Seat());
                holder.setSeats(model.getNum_Seats());
            }

            @NonNull
            @Override
            public OfferedRideDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.offered_ride_row,parent,false);
                return new OfferedRideDetailsViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class OfferedRideDetailsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public OfferedRideDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setSourceLocation(Context context,String sourceLocation)
        {
            TextView sourceLocationTV = (TextView) mView.findViewById(R.id.source_location_textview);
            sourceLocationTV.setText(geocode(context,sourceLocation));
        }
        public void setDestinationLocation(Context context,String destinationLocation)
        {
            TextView destinationLocaitonTV = (TextView) mView.findViewById(R.id.destination_location_textview);
            destinationLocaitonTV.setText(geocode(context,destinationLocation));
        }
        public void setTime(String time)
        {
            TextView timeTV = (TextView) mView.findViewById(R.id.time_value_textview);
            timeTV.setText(time);
        }
        public void setDate(String date)
        {
            TextView dateTV = (TextView) mView.findViewById(R.id.date_value_textview);
            dateTV.setText(date);
        }
        public void setCost(String cost)
        {
            TextView costTV = (TextView) mView.findViewById(R.id.cost_per_seat_textview);
            costTV.setText(cost);
        }
        public void setSeats(String seats)
        {
            TextView seatsTV = (TextView) mView.findViewById(R.id.seats_value_textview);
            seatsTV.setText(seats);
        }
    }

    public static String geocode(Context context, String location)
    {
        String locationName = null;
        String[] str = location.split(",");
        Log.d(TAG, "geocode: String1: "+str[0]+" String2: "+str[1]);

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Address address = null;
        try {
            List<Address> list = geocoder.getFromLocation(Double.parseDouble(str[0]), Double.parseDouble(str[1]),1);

            if(list.size() > 0)
            {
                address = list.get(0);
                Log.d(TAG, "geocode: sub_admin_Area: "+address.getLocality());
                locationName = address.getLocality();
                return locationName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Offered_Ride_Activity.this, HomeScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
