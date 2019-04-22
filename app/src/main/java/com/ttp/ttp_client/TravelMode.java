package com.ttp.ttp_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttp.ttp_commons.TourInput;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TravelMode extends AppCompatActivity implements View.OnClickListener {

    private List<InputLocation> locations = new ArrayList<>();
    public static final String TravelMode = "com.ttp.ttp_client.TravelMode";
    public static final String travelModeKey = "com.ttp.ttp_client.TravelMode.Key";

    int[] buttonsIds = {R.id.button_walk,R.id.button_cycle,R.id.button_drive, R.id.button_transit};
    Button[] buttons = new Button[buttonsIds.length];
    Button focus;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travelmode);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String jsonLocs = intent.getStringExtra(VisitingLocation.VisitingLocations);
        Type collectionType = TypeToken.getParameterized(List.class, InputLocation.class).getType();
        locations = gson.fromJson(jsonLocs, collectionType);
        for(int i = 0; i < buttons.length; i++){
            buttons[i] = findViewById(buttonsIds[i]);
            buttons[i].setOnClickListener(this);
        }
        setFocus(buttons[0]);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(locations == null) {
            SharedPreferences settings = getSharedPreferences(TravelMode, 0);
            Type collectionType = TypeToken.getParameterized(List.class, InputLocation.class).getType();
            String locJson = settings.getString(travelModeKey, "");
            try {
                locations = gson.fromJson(locJson, collectionType);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(TravelMode, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.clear();
        editor.putString(travelModeKey, gson.toJson(locations));
        editor.commit();
    }

    public void onClick(View view) {
        setFocus(focus, (Button) findViewById(view.getId()));
    }

    private void setFocus(Button btn_unfocus, Button btn_focus){
        btn_unfocus.setTextColor(Color.rgb(50,50,50));
        btn_focus.setTextColor(Color.rgb(3, 106, 150));
        this.focus = btn_focus;
    }

    private void setFocus(Button focus){
        focus.setTextColor(Color.rgb(3, 106, 150));
        this.focus = focus;
    }

    public void getResults(View view)
    {
        Intent intent = new Intent(this, TourResults.class);
        List<String> locs = new ArrayList<>();
        List<String> locsNames = new ArrayList<>();
        try{
            for(InputLocation loc : locations){
                locs.add(String.valueOf(loc.getLatLng().latitude)+","+String.valueOf(loc.getLatLng().longitude));
                locsNames.add(loc.getLocation());
            }
            AppTourInput input = new AppTourInput(new TourInput(focus.getText().toString(), locs.toArray(new String[0])), locsNames);
            intent.putExtra(TravelMode, gson.toJson(input));
            startActivity(intent);
        } catch(Exception e){
            Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_LONG);
        }

    }

}
