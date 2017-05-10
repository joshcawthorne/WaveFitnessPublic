package com.wave.fitness;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by AaronWalker on 10/05/2017.
 */

public class PostRunActivity extends ActionBarActivity implements OnMapReadyCallback {

    private SupportMapFragment map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postrun);
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(54.5695968,-1.2339262), 16)); //This can be changed to last geopoint of run

        PolylineOptions line=
                new PolylineOptions().add(new LatLng(54.5695968,-1.2339262),
                        new LatLng(54.569985,-1.2294937)) //Add more location points bellow
                        .width(5).color(Color.RED);

        map.addPolyline(line);

        //The following is used to add an icon on each geopoint on the map, this can be removed if we do not want this feature, Could be used to signal start and end points
        //map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_notification //This is where you select an icon to be displayed
        // )).anchor(0.0f, 1.0f).position(new LatLng(54.5695968,-1.2339262))); //Test location - University

    }
}
