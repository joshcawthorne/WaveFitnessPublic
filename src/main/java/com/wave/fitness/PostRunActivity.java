package com.wave.fitness;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.robertsimoes.shareable.Shareable;
import com.rogalabs.lib.model.SocialUser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.google.gson.Gson;

import static com.google.android.gms.R.id.toolbar;


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
    @InjectView(R.id.steps_value)
    TextView _steps;

    private HamburgerMenu menu;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postrun);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        createHamburgerMenu();

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

        injectStats();
    }

    public void createHamburgerMenu(){
        menu = new HamburgerMenu(this, new Gson().fromJson(getSharedPreferences("com.wave.fitness", MODE_PRIVATE).getString("user", ""), SocialUser.class), toolbar);
    }


    @Override
    public void onMapReady(GoogleMap map) {

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(data.route.get(data.route.size()-1).getLatitude()
                        ,data.route.get(data.route.size()-1).getLongitude()), 16));
        map.addPolyline(line);
    }

    protected void onPostToFacebook() {
        Shareable shareAction = new Shareable.Builder(this)
                .message("I've finished a run")
                .socialChannel(Shareable.Builder.FACEBOOK)
                .build();
        shareAction.share();
    }

    private void injectStats() {
        final String timeform =
                String.format("\n %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(data.duration),
                        TimeUnit.MILLISECONDS.toSeconds(data.duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(data.duration))
        );

        _time.setText(timeform);
        _calories.setText(String.valueOf(data.calories));
        _distance.setText(String.valueOf(data.distance));
        _steps.setText(String.valueOf(data.totalStep));
    }
}