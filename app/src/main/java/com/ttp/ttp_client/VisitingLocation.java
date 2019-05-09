package com.ttp.ttp_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisitingLocation extends AppCompatActivity {

    public static final String VisitingLocations = "com.ttp.ttp_client.VisitingLocations";
    public static final String visitingLocationsKey = "com.ttp.ttp_client.VisitingLocations.Key";
    private InputLocation startLoc;
    private List<InputLocation> locations = new ArrayList<>();
    private InputLocation current = null;
    String TAG = "placeautocomplete";
    RecyclerView recyclerView;
    Adapter rvAdapter;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitinglocation);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gson = new Gson();

        Intent intent = getIntent();
        startLoc = gson.fromJson(intent.getStringExtra(StartLocation.StartingLocation), InputLocation.class);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new RVDivider(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvAdapter = new Adapter(locations);
        recyclerView.setAdapter(rvAdapter);

        setUpAutocomplete();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(VisitingLocations, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.clear();
        editor.putString(visitingLocationsKey, gson.toJson(locations));
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences(VisitingLocations, 0);
        Type collectionType = TypeToken.getParameterized(List.class, InputLocation.class).getType();
        String locJson = settings.getString(visitingLocationsKey, "");
        try {
            List<InputLocation> locs = gson.fromJson(locJson, collectionType);
            if(startLoc == null) {
                startLoc = locs.get(0);
                locs.remove(0);
            }
            for(int i = 0; i < locs.size(); i++){
                current = locs.get(i);
                addLoc(getCurrentFocus());
            }
        } catch(Exception e) {

        }
    }

    public void setUpAutocomplete(){
        Places.initialize(getApplicationContext(), getString(R.string.google_map_api_key));

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                current = new InputLocation(place.getName(), place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(), "An error occurred: " + status, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void travelMode(View view) {
        if(locations.size() > 0){
            locations.add(0, startLoc);
            startLoc = null;
            Gson gson = new Gson();

            Intent intent = new Intent(this, TravelMode.class);
            intent.putExtra(VisitingLocations, gson.toJson(locations));
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Add Locations",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void addLoc(View view){
        try {
            if(current != null) {
                if(!locations.contains(current) && !current.getLatLng().equals(startLoc.getLatLng())) {
                    if (locations.size() < getResources().getInteger(R.integer.max_Visit)) {
                        locations.add(current);
                        rvAdapter.notifyItemInserted(locations.size() - 1);
                        current = null;
                    } else {
                        Toast.makeText(getApplicationContext(), "The max amount of locations has been added", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"That location has already been added", Toast.LENGTH_LONG).show();
                }
            } else{
                Toast.makeText(getApplicationContext(), "The field is empty",
                        Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "The field is empty",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
