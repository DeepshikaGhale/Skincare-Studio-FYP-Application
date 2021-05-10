package com.example.fyp_application;

import android.widget.EditText;
import android.widget.Spinner;

public class AddressesModel {

    private Boolean selected;
    private String city;
    private String locality;
    private String faltNo;
    private String pincode;
    private String landmark;
    private String name;
    private String mobileNo;
    private String alternativeMobileNo;
    private String state;

    public AddressesModel(Boolean selected, String city, String locality, String faltNo, String pincode, String landmark, String name, String mobileNo, String alternativeMobileNo, String state) {
        this.selected = selected;
        this.city = city;
        this.locality = locality;
        this.faltNo = faltNo;
        this.pincode = pincode;
        this.landmark = landmark;
        this.name = name;
        this.mobileNo = mobileNo;
        this.alternativeMobileNo = alternativeMobileNo;
        this.state = state;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFaltNo() {
        return faltNo;
    }

    public void setFaltNo(String faltNo) {
        this.faltNo = faltNo;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAlternativeMobileNo() {
        return alternativeMobileNo;
    }

    public void setAlternativeMobileNo(String alternativeMobileNo) {
        this.alternativeMobileNo = alternativeMobileNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
