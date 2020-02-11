package com.example.shareride;

import android.util.Log;

public class OfferedRideDetails {

    private static final String TAG = "OfferedRideDetails";

    private String Car_id, Cost_Per_Seat, Date, Destination_Location, Num_Seats, Source_Location, Time;

    OfferedRideDetails()
    {
        Log.d(TAG, "OfferedRideDetails: inside the default constructor.");
    }

    public OfferedRideDetails(String car_id, String cost_Per_Seat, String date, String destination_Location, String num_Seats, String source_Location, String time) {
        Log.d(TAG, "OfferedRideDetails: inside the parameterized constructor.");
        Car_id = car_id;
        Cost_Per_Seat = cost_Per_Seat;
        Date = date;
        Destination_Location = destination_Location;
        Num_Seats = num_Seats;
        Source_Location = source_Location;
        Time = time;
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
}
