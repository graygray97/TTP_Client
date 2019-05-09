package com.ttp.ttp_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;

import java.util.Arrays;

public class StartLocation extends AppCompatActivity {

    public static final String StartingLocation = "com.ttp.ttp_client.StartingLocation";
    public static final String startingLocationKey = "com.ttp.ttp_client.StartingLocation.Key";
    String startLoc = "";
    String TAG = "placeautocomplete";
    TextView textView;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startlocation);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpAutoComplete();
        gson = new Gson();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(StartingLocation, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.clear();
        editor.putString(startingLocationKey, startLoc);
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences(StartingLocation, 0);
        startLoc = settings.getString(startingLocationKey, "");
        try {
            InputLocation place = gson.fromJson(startLoc, InputLocation.class);
            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            autocompleteFragment.setText(place.getLocation());
        } catch(Exception e) {
        }
    }


    public void setUpAutoComplete(){
        Places.initialize(getApplicationContext(), getString(R.string.google_map_api_key));

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                startLoc = gson.toJson(new InputLocation(place.getName(), place.getLatLng()));
            }
            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(), "An error occurred: " + status, Toast.LENGTH_LONG);
            }
        });
    }

    public void newSearch(View view) {
        if(startLoc != ""){
            Intent intent = new Intent(this, VisitingLocation.class);
            intent.putExtra(StartingLocation, startLoc);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Error: A starting location is needed",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
