package com.example.shareride;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetails implements Parcelable {

    private static final String TAG = "UserDetails";
    String firstName, lastName, gender, contact, city, pincode, profilePicture, DOB, userID, profilePhoto;

    public UserDetails()
    {

    }

    public UserDetails(String firstName, String lastName, String gender, String contact, String city, String pincode, String profilePicture, String DOB, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.contact = contact;
        this.city = city;
        this.pincode = pincode;
        this.profilePicture = profilePicture;
        this.DOB = DOB;
        this.userID = userID;
    }

    protected UserDetails(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        gender = in.readString();
        contact = in.readString();
        city = in.readString();
        pincode = in.readString();
        profilePicture = in.readString();
        DOB = in.readString();
        userID = in.readString();
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(gender);
        dest.writeString(contact);
        dest.writeString(city);
        dest.writeString(pincode);
        dest.writeString(profilePicture);
        dest.writeString(DOB);
        dest.writeString(userID);
    }
}
