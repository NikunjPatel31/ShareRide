package com.example.shareride;

public class CarDetails {

    private String Air_Conditioner, Car_Image, Car_Name, Fuel, Model_Year, Vehicle_Number;

    public CarDetails()
    {

    }

    public CarDetails(String air_Conditioner, String car_Image, String car_Name, String fuel, String model_Year, String vehicle_Number) {
        Air_Conditioner = air_Conditioner;
        Car_Image = car_Image;
        Car_Name = car_Name;
        Fuel = fuel;
        Model_Year = model_Year;
        Vehicle_Number = vehicle_Number;
    }

    public String getCar_Name() {
        return Car_Name;
    }

    public void setCar_Name(String car_Name) {
        Car_Name = car_Name;
    }

    public String getModel_Year() {
        return Model_Year;
    }

    public void setModel_Year(String model_Year) {
        Model_Year = model_Year;
    }

    public String getVehicle_Number() {
        return Vehicle_Number;
    }

    public void setVehicle_Number(String vehicle_Number) {
        Vehicle_Number = vehicle_Number;
    }

    public String getAir_Conditioner() {
        return Air_Conditioner;
    }

    public void setAir_Conditioner(String air_Conditioner) {
        Air_Conditioner = air_Conditioner;
    }

    public String getFuel() {
        return Fuel;
    }

    public void setFuel(String fuel) {
        Fuel = fuel;
    }

    public String getCar_Image() {
        return Car_Image;
    }

    public void setCar_Image(String car_Image) {
        Car_Image = car_Image;
    }
}
