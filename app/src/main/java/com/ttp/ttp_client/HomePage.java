package com.ttp.ttp_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void newSearch(View view) {
        Intent intent = new Intent(this, StartLocation.class);
        SharedPreferences settings = getSharedPreferences(StartLocation.StartingLocation, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        settings = getSharedPreferences(VisitingLocation.VisitingLocations, 0);
        editor = settings.edit();
        editor.clear();
        editor.commit();

        startActivity(intent);
    }

    public void previousSearch(View view){
        Intent intent = new Intent(this, SavedResults.class);
        startActivity(intent);
    }

}
