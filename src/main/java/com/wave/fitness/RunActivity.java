package com.wave.fitness;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class RunActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Handler uiHandler = new Handler();
    private Runnable uiRunnable;
    private Location lastKnownLocation = null;
    private ArrayList<RouteNode> route;
    private boolean tracking = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    private final int ACCESS_FINE_LOCATION_REQUEST = 0;

    Chronometer chrono;
    //Pedometer pedo;
    TextView speedView;

    protected void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                //TODO: Display a snackbar explaining the requirement of this permission

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_REQUEST);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("MAP", "Created");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Log.d("API", "Google API Client Created");

        chrono = (Chronometer) findViewById(R.id.chronometer);
        chrono.setFormat("Time Running - %s");
        Log.d("APP", "Chrono Setup");

        //pedo = new Pedometer();
        speedView = (TextView) findViewById(R.id.speed);
        Log.d("APP", "Pedometer Setup");
    }

    protected void onStart() {
        super.onStart();
        requestPermissions();
        Log.d("APP", "Application Started");
        mGoogleApiClient.connect();


    }

    protected void onStop() {
        Log.d("APP", "Application Stopping");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //to be adjusted later, maybe as a setting
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d("API", "Location Request Created");
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d("API", "Location Request Applied");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("MAP", "Map Ready");

        uiRunnable = new Runnable() {
            @Override
            public void run() {
                if (tracking) {
                    mMap.clear();
                    PolylineOptions lineOpt = new PolylineOptions();
                    for (int i = 0; i < route.size() - 1; i++) {
                        Location loc = route.get(i).location;
                        lineOpt.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
                    }
                    lineOpt.color(Color.BLUE);
                    lineOpt.width(5.0f);
                    lineOpt.visible(true);
                    mMap.addPolyline(lineOpt);

                    //For testing only
                    //speedView.setText("Speed: " + pedo.getSpeed());

                } else {
                    mMap.clear();
                }
                uiHandler.postDelayed(uiRunnable, 500);
            }
        };
        uiRunnable.run();
    }

    @Override
    public void onLocationChanged(Location _location) {
        Location location = _location;
        Log.d("LOC", "Getting Location");
        if (location != null) {
            Log.d("LOC", String.format("Lat:%f, Lat:%f", location.getLatitude(), location.getLongitude()));
            lastKnownLocation = location;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 19f);
            mMap.animateCamera(cameraUpdate);
            Log.d("MAP", "Camera Moved To Current Location");

            if (tracking) {
                route.add(new RouteNode(location));
                Log.d("RUN", "Tracked A Running Node");
            }

        } else {
            Log.d("LOC", "NULL Location");
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 19f);
            mMap.animateCamera(cameraUpdate);
            Log.d("MAP", "Camera Moved To Last Known Location");
        }
    }

    public void toggleTracking(View _view) {
        if (tracking) {
            Toast.makeText(this, "Run Finished!", Toast.LENGTH_LONG).show();
            chrono.stop();
            Log.d("RUN", "Run Tracking Stopped");

            //setContentView(R.layout.post_run); //Start the Post Run Screen (Just displays the layout, doesn't change to the PostRun activity)

        } else {
            Toast.makeText(this, "Run Started!",
                    Toast.LENGTH_LONG).show();

            route = new ArrayList<RouteNode>();
            route.add(new RouteNode(lastKnownLocation));

            chrono.setBase(SystemClock.elapsedRealtime());
            chrono.start();

            Log.d("RUN", "Run Tracking Started");
        }
        tracking = !tracking;
    }
}