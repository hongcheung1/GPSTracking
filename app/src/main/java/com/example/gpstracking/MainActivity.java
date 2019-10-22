package com.example.gpstracking;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

@SuppressWarnings("ResourceType")
public class MainActivity extends AppCompatActivity implements LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private MapView mMapView;

    private Marker myLocationMarker;
    GPSTracker gps;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    private double latitude, longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gps = new GPSTracker(this);
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            mMapView = (MapView) findViewById(R.id.mMap);
            mMapView.onCreate(savedInstanceState);

            MapsInitializer.initialize(this);

            if (mMap == null) {
                mMap = mMapView.getMap();
            }

            mMap.setMyLocationEnabled(false);
            mMap.clear();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark)).position(new LatLng(latitude, longitude));
            myLocationMarker = mMap.addMarker(markerOptions);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(15).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMapView != null) {
            mMapView.onResume();
        }
startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMapView != null) {
            mMapView.onPause();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMapView != null) {
            mMap = null;
            mMapView.onDestroy();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mMapView != null) {
            mMap = null;
            mMapView.onDestroy();
        }


    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng latlong = getLatLng(location);
        if(latlong != null){
            if (mMap != null) {
                if (myLocationMarker == null) {
                    myLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(latlong)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark)));
                } else {
                    myLocationMarker.setPosition(latlong);
                }
            }
        }

    }

    public LatLng getLatLng(Location currentLocation) {
        // If the location is valid
        if (currentLocation != null) {
            // Return the latitude and longitude as strings
            LatLng latLong = new LatLng(currentLocation.getLatitude(),
                    currentLocation.getLongitude());

            return latLong;
        } else {
            // Otherwise, return the empty string
            return null;
        }
    }

    public void startTimer(){
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 10000, 3000);
    }

    public void stopTimerTask(View view){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            LatLng latlong = new LatLng(latitude, longitude);
                            if(latlong != null){
                                if (mMap != null) {
                                    if (myLocationMarker == null) {
                                        myLocationMarker = mMap.addMarker(new MarkerOptions()
                                                .position(latlong)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark)));
                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                                new LatLng(latitude, longitude)).zoom(15).build();

                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    } else {
                                        myLocationMarker.setPosition(latlong);
                                    }
                                }
                            }

                    }
                });
            }
        };
    }
}
