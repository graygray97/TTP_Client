package com.ttp.ttp_client;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class InputLocation {
    private String location;
    private LatLng latLng;

    public InputLocation(String Location, LatLng LatLong){
        location = Location;
        latLng = LatLong;
    }

    public String getLocation() {
        return location;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getLatLngString() {
        return latLng.toString();
    }
}
