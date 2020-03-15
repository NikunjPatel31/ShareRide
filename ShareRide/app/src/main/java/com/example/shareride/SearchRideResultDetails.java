package com.example.shareride;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchRideResultDetails {

    private static final String TAG = "SearchRideResultDetails";
    
    String Destination_Location_Name, Source_Location_Name, Car_id, Cost_Per_Seat
            , Date, Destination_Location, Num_Seats, Source_Location
            , Time;
    String userID, riderImage;
    UserDetails riderDetails;

    public SearchRideResultDetails()
    {

    }

    public SearchRideResultDetails(String destination_Location_Name
            , String source_Location_Name
            , String car_id
            , String cost_Per_Seat
            , String date
            , String destination_Location
            , String num_Seats
            , String source_Location
            , String time
            , String userID
            , String riderImage) {
        Destination_Location_Name = destination_Location_Name;
        Source_Location_Name = source_Location_Name;
        Car_id = car_id;
        Cost_Per_Seat = cost_Per_Seat;
        Date = date;
        Destination_Location = destination_Location;
        Num_Seats = num_Seats;
        Source_Location = source_Location;
        Time = time;
        this.userID = userID;
        this.riderImage = riderImage;
    }

    public void setDestination_Location_Name(String destination_Location_Name) {
        Destination_Location_Name = destination_Location_Name;
    }

    public void setSource_Location_Name(String source_Location_Name) {
        Source_Location_Name = source_Location_Name;
    }

    public void setCar_id(String car_id) {
        Car_id = car_id;
    }

    public void setCost_Per_Seat(String cost_Per_Seat) {
        Cost_Per_Seat = cost_Per_Seat;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setDestination_Location(String destination_Location) {
        Destination_Location = destination_Location;
    }

    public void setNum_Seats(String num_Seats) {
        Num_Seats = num_Seats;
    }

    public void setSource_Location(String source_Location) {
        Source_Location = source_Location;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDestination_Location_Name() {
        return Destination_Location_Name;
    }

    public String getSource_Location_Name() {
        return Source_Location_Name;
    }

    public String getCar_id() {
        return Car_id;
    }

    public String getCost_Per_Seat() {
        return Cost_Per_Seat;
    }

    public String getDate() {
        return Date;
    }

    public String getDestination_Location() {
        return Destination_Location;
    }

    public String getNum_Seats() {
        return Num_Seats;
    }

    public String getSource_Location() {
        return Source_Location;
    }

    public String getTime() {
        return Time;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public String getUserID()
    {
        return userID;
    }

    public UserDetails getRiderDetails()
    {
        return riderDetails;
    }
    public void setRiderDetails()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(getUserID());
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

                riderDetails = userDetails;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
