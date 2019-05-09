package com.ttp.ttp_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    public static final String HomePage = "com.ttp.ttp_client.HomePage";
    public static final String homePageKey = "com.ttp.ttp_client.HomePage.Key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences settings = getSharedPreferences(HomePage, 0);
        EditText editText = findViewById(R.id.editText);
        String url = settings.getString(homePageKey, "");
        if(url != ""){
            editText.setText(url);
        }
    }

    public void newSearch(View view) {
        EditText editText = findViewById(R.id.editText);
        String pattern = "http://\\w+.ngrok.io";
        String url = editText.getText().toString();
        if(url.matches(pattern) ) {
            Intent intent = new Intent(this, StartLocation.class);
            SharedPreferences settings = getSharedPreferences(StartLocation.StartingLocation, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
            settings = getSharedPreferences(VisitingLocation.VisitingLocations, 0);
            editor = settings.edit();
            editor.clear();
            editor.commit();
            settings = getSharedPreferences(TravelMode.TravelMode, 0);
            editor = settings.edit();
            editor.clear();
            editor.commit();

            settings = getSharedPreferences(HomePage, 0);
            editor = settings.edit();
            editor.clear();
            editor.putString(homePageKey, url);
            editor.commit();

            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Error: URL given does not fit ngrok pattern", Toast.LENGTH_SHORT).show();
        }
    }

}
