package com.firstprojects.thetravelsbook_try1.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;


public class AddressRegister implements Serializable {
         private Double latitude;
         private Double longitude;
         private String placeName;

   public AddressRegister(Double latitude, Double longitude, String placeName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getPlaceName() {
        return placeName;
    }
}

