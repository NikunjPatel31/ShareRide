package com.example.shareride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PassengerListRecyclerViewAdapter extends RecyclerView.Adapter<PassengerListRecyclerViewAdapter.PassengerListRecyclerViewHolder>{
    @NonNull
    private static final String TAG = "PassengerListRecyclerVi";
    private ArrayList<UserDetails> passengerDetails;

    public PassengerListRecyclerViewAdapter(@NonNull ArrayList<UserDetails> passengerDetails) {
        this.passengerDetails = passengerDetails;
    }

    @Override
    public PassengerListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ride_passenger_details_row,parent,false);
        return new PassengerListRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerListRecyclerViewHolder holder, int position) {

        UserDetails temPassengerDetails = passengerDetails.get(position);
        holder.setPassengerPhoto(temPassengerDetails.getProfilePicture());
        String firstName = temPassengerDetails.getFirstName();
        String lastName = temPassengerDetails.getLastName();
        String fullName = firstName+" "+lastName;
        holder.setPassengerName(fullName);
        holder.setPassengerAge(temPassengerDetails.getDOB());
        holder.setPassengerGender(temPassengerDetails.getGender());
    }

    @Override
    public int getItemCount() {
        return passengerDetails.size();
    }

    public static class PassengerListRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        Button checkInBtn, checkOutBtn;
        public PassengerListRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            checkInBtn = view.findViewById(R.id.chech_in_button);
            checkOutBtn = view.findViewById(R.id.chech_out_button);
        }
        private void setPassengerPhoto(String passengerPhoto)
        {
            CircleImageView passengerPhotoIV = view.findViewById(R.id.passenger_photo);
            Picasso.get().load(passengerPhoto).placeholder(R.drawable.ic_account_circle_black_24dp).into(passengerPhotoIV);
        }
        private void setPassengerName(String passengerName)
        {
            TextView passengerNameTV = view.findViewById(R.id.passenger_name_textview);
            passengerNameTV.setText(passengerName);
        }
        private void setPassengerGender(String passengerGender)
        {
            TextView passengerGenderTV = view.findViewById(R.id.passenger_gender_textview);
            passengerGenderTV.setText("( "+passengerGender+" )");
        }
        private void setPassengerAge(String passengerDOB)
        {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int age = year - Integer.parseInt(passengerDOB);
            TextView passengerAgeTV = view.findViewById(R.id.passenger_age_textview);
            passengerAgeTV.setText(Integer.toString(age));
        }
    }
}
