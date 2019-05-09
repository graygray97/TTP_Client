package com.ttp.ttp_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.ttp.ttp_commons.Path;
import com.ttp.ttp_commons.TourInput;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.ksoap2.*;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class TourResults extends AppCompatActivity {

    String TAG = "TourResults";
    Gson gson = new Gson();
    String input;
    TextView textView;
    String url;
    List<Polyline> polylines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tourresults);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        input = intent.getStringExtra(TravelMode.TravelMode);
        textView = findViewById(R.id.textViewResults);
        textView.setMovementMethod(new ScrollingMovementMethod());
        createTour task = new createTour();
        SharedPreferences settings = getSharedPreferences(HomePage.HomePage, 0);
        url = settings.getString(HomePage.homePageKey, "");
        task.execute(input);
    }

    private class createTour extends AsyncTask<String, Void, ResultAndInput> {
        @Override
        protected ResultAndInput doInBackground(String... input) {
            AppTourInput inputObject = gson.fromJson(input[0], AppTourInput.class);
            SoapObject request = new SoapObject(getString(R.string.name_space),getString(R.string.createTour_method));
            request.addProperty("modeAndLocations", gson.toJson(inputObject.getTourInput()));
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(url + getString(R.string.wsdl_url));
            String calcRes = "Error: no known error";
            try {
                androidHttpTransport.call(getString(R.string.createTour_method), envelope);
                if (envelope.bodyIn instanceof SoapFault) {
                    Log.e(TAG, ((SoapFault) envelope.bodyIn).getMessage());
                } else {
                    SoapObject obj = (SoapObject) envelope.bodyIn;
                    calcRes = obj.getProperty(0).toString();
                }
            }catch(Exception ex) {
                calcRes = "Error:" + ex.getMessage();
            }
            return new ResultAndInput(calcRes, inputObject);
        }

        protected void onPostExecute(ResultAndInput result) {
            try {
                Type collectionType = TypeToken.getParameterized(List.class, Path.class).getType();
                List<Path> output = gson.fromJson(result.getResult(), collectionType);
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        for (Path path : output) {
                            LatLng latLng = new LatLng(path.getFrom().getLat(), path.getFrom().getLng());
                            if (path.getFrom().isStartingLoc()) {
                                googleMap.addMarker(new MarkerOptions().position(latLng).title("Start")).showInfoWindow();
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                            } else {
                                googleMap.addMarker(new MarkerOptions().position(latLng));
                            }
                            getFullPath task = new getFullPath();
                            com.google.maps.model.LatLng[] fromTo = new com.google.maps.model.LatLng[]{path.getFrom().getLocation(),path.getTo().getLocation()};
                            TourInput input = new TourInput(result.getTourInput().getTourInput().getMode(), fromTo);
                            task.execute(gson.toJson(input));
                        }
                        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                            @Override
                            public void onPolylineClick(Polyline polyline) {
                                for(Polyline p : polylines){
                                    p.setZIndex(0);
                                    p.setColor(getColor(R.color.colorSecondaryVariant));
                                }
                                polyline.setZIndex(1);
                                polyline.setColor(getColor(R.color.colorSecondary));
                                textView.setText(polyline.getTag().toString());
                            }
                        });
                    }
                });
            } catch(Exception e) {
                Toast.makeText(getApplicationContext(), "Error: Tour could not be found",  Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class getFullPath extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... pathParams) {
            SoapObject request = new SoapObject(getString(R.string.name_space),getString(R.string.getFullPath_method));
            request.addProperty("pathParams", pathParams[0]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(url + getString(R.string.wsdl_url));
            String calcRes;
            try {
                androidHttpTransport.call(getString(R.string.getFullPath_action), envelope);
                if (envelope.bodyIn instanceof SoapFault) {
                    calcRes = "Connection Error, Try again";
                    Log.e(TAG, ((SoapFault) envelope.bodyIn).getMessage());
                } else {
                    SoapObject obj = (SoapObject) envelope.bodyIn;
                    calcRes = obj.getProperty(0).toString();
                }
            }catch(Exception ex) {
                calcRes = "Error occured, Try again";
                Log.e(TAG, ex.getMessage());
            }
            return calcRes;
        }

        protected void onPostExecute(String result) {
            try {
                Path output = gson.fromJson(result, Path.class);
                DirectionsRoute[] direc = output.getDirection().routes;
                List<LatLng> decPath = getDirectionPolylines(Arrays.asList(direc));
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        PolylineOptions polylineOptions = new PolylineOptions()
                                .color(getColor(R.color.colorSecondaryVariant))
                                .clickable(true);
                        Polyline polyline = googleMap.addPolyline(polylineOptions.addAll(decPath));
                        polyline.setTag(buildRouteDesc(direc));
                        polylines.add(polyline);
                    }
                });
            } catch(Exception e) {
                if(result.startsWith("Error")){
                    Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: Tour could not be found",
                        Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private String buildRouteDesc(DirectionsRoute[] direc) {
        String routeDesc = "";
        for(DirectionsRoute route : direc){
            for(DirectionsLeg leg : route.legs){
                for(DirectionsStep step : leg.steps){
                    routeDesc += "\n"+Html.fromHtml(step.htmlInstructions);
                }
            }
        }
        return routeDesc;
    }

    private List<LatLng> getDirectionPolylines(List<DirectionsRoute> routes){
        List<LatLng> directionList = new ArrayList<LatLng>();
        for(DirectionsRoute route : routes){
            for(DirectionsLeg leg : route.legs){
                for(DirectionsStep step : leg.steps){
                    directionList.add(new LatLng(step.startLocation.lat, step.startLocation.lng));
                    String polyline = step.polyline.getEncodedPath();
                    directionList.addAll(PolyUtil.decode(polyline));
                    directionList.add(new LatLng(step.endLocation.lat, step.endLocation.lng));
                }
            }
        }
        return directionList;
    }

}
