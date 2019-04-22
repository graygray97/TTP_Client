package com.ttp.ttp_client;

import com.google.android.gms.maps.model.LatLng;
import com.ttp.ttp_commons.TourInput;

import java.util.List;

public class AppTourInput {
    private TourInput tourInput;
    private List<String> tourNames;

    public AppTourInput(TourInput _tourInput, List<String> _tourNames){
        tourInput = _tourInput;
        tourNames = _tourNames;
    }

    public TourInput getTourInput() {
        return tourInput;
    }

    public List<String> getTourNames() {
        return tourNames;
    }

    public String getNameForLatLng(LatLng loc){
        String[] locations = tourInput.getLocations();
        for(int i = 0; i < locations.length; i++){
            String[] latlng = locations[i].split(",");
            if(loc.latitude == Double.parseDouble(latlng[0]) && loc.longitude == Double.parseDouble(latlng[1])){
                return tourNames.get(i);
            }
        }
        return null;
    }
}
