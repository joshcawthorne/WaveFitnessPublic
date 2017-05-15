package com.wave.fitness;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.robertsimoes.shareable.Shareable;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PostRunActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment map;
    private SharedPreferences prefs = null;
    private Repo_RunStatistic repo;
    private Data_RunStatistic data;

    private int runId;
    private PolylineOptions line;

    @InjectView(R.id.time)
    TextView _time;
    @InjectView(R.id.calories)
    TextView _calories;
    @InjectView(R.id.distance)
    TextView _distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postrun);

        runId = getIntent().getIntExtra("runID", 0);
        repo = new Repo_RunStatistic(getApplicationContext());
        data = repo.getEntrybyID(runId);
        if (data == null) {
            data = new Data_RunStatistic();
        }

        line = new PolylineOptions();
        for (Location location : data.route){
            line.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        line.width(5).color(Color.RED);

        ButterKnife.inject(this);
        _distance.setText(String.valueOf(data.distance));

        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(data.route.get(data.route.size()-1).getLatitude()
                        ,data.route.get(data.route.size()-1).getLongitude()), 16)); //This can be changed to last geopoint of run

        PolylineOptions line=
                new PolylineOptions().add(new LatLng(54.5695968,-1.2339262),
                        new LatLng(54.569985,-1.2294937)) //Add more location points bellow
                        .width(5).color(Color.RED);
        map.addPolyline(line);


    }

    protected void onPostToFacebook() {
        Shareable shareAction = new Shareable.Builder(this)
                .message("I've finished a run")
                .socialChannel(Shareable.Builder.FACEBOOK)
                .build();
        shareAction.share();
    }
}