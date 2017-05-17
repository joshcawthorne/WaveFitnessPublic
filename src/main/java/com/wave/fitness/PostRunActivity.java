package com.wave.fitness;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.spotify.sdk.android.player.Metadata;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import static com.google.android.gms.R.id.toolbar;


public class PostRunActivity extends AppCompatActivity implements OnMapReadyCallback {

    /* Class to generate previous runs */

    private SupportMapFragment map;
    private SharedPreferences prefs = null;
    private Repo_RunStatistic repo;
    private Data_RunStatistic data;

    private int runId;
    private PolylineOptions line;

    @InjectView(R.id.time) TextView _time;
    @InjectView(R.id.calories) TextView _calories;
    @InjectView(R.id.distance) TextView _distance;
    @InjectView(R.id.steps_value) TextView _steps;

    @InjectView(R.id.shareFB) ImageView _shareFB;
    @InjectView(R.id.shareTwitter) ImageView _shareTwitter;

    //Songlist
    @InjectView(R.id.playBar) LinearLayout _playBar1;
    @InjectView(R.id.cover_art_small) ImageView _art1;
    @InjectView(R.id.metadataTitle) TextView _title1;
    @InjectView(R.id.metadataSubTitle) TextView _subTitle1;
    @InjectView(R.id.metaDataTime) TextView _time1;

    @InjectView(R.id.playBarTwo) LinearLayout _playBar2;
    @InjectView(R.id.cover_art_smallTwo) ImageView _art2;
    @InjectView(R.id.metadataTitleTwo) TextView _title2;
    @InjectView(R.id.metadataSubTitleTwo) TextView _subTitle2;
    @InjectView(R.id.metaDataTimeTwo) TextView _time2;

    @InjectView(R.id.playBarThree) LinearLayout _playBar3;
    @InjectView(R.id.cover_art_smallThree) ImageView _art3;
    @InjectView(R.id.metadataTitleThree) TextView _title3;
    @InjectView(R.id.metadataSubTitleThree) TextView _subTitle3;
    @InjectView(R.id.metaDataTimeThree) TextView _time3;

    @InjectView(R.id.playBarFour) LinearLayout _playBar4;
    @InjectView(R.id.cover_art_smallFour) ImageView _art4;
    @InjectView(R.id.metadataTitleFour) TextView _title4;
    @InjectView(R.id.metadataSubTitleFour) TextView _subTitle4;
    @InjectView(R.id.metaDataTimeFour) TextView _time4;

    @InjectView(R.id.playBarFive) LinearLayout _playBar5;
    @InjectView(R.id.cover_art_smallFive) ImageView _art5;
    @InjectView(R.id.metadataTitleFive) TextView _title5;
    @InjectView(R.id.metadataSubTitleFive) TextView _subTitle5;
    @InjectView(R.id.metaDataTimeFive) TextView _time5;


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
        line.width(6).color(Color.RED);

        ButterKnife.inject(this);
        _shareFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostToFacebook();
            }
        });
        _shareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostToTwitter();
            }
        });


        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        injectStats();
        populateSongList();
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
                .message("I've finished a " + String.valueOf(data.distance) + "Km run and burned " + String.valueOf(data.calories)+ "Calories! \n" +
                            "Total Steps " + String.valueOf(data.totalStep) + "\n" +
                            "Average Speed" + String.valueOf(data.avrspeed) + "\n" +
                            "Average Step Per Minute" + String.valueOf(data.avrspeed) + "#WaveFitness")
                .socialChannel(Shareable.Builder.FACEBOOK)
                .build();
        shareAction.share();
    }
    protected void onPostToTwitter() {
        Shareable shareAction = new Shareable.Builder(this)
                .message("I've finished a " + String.valueOf(data.distance) + "Km run and burned " + String.valueOf(data.calories)+ "Calories! \n" +
                        "Total Steps " + String.valueOf(data.totalStep) + "\n" +
                        "Average Speed" + String.valueOf(data.avrspeed) + "\n" +
                        "Average Step Per Minute" + String.valueOf(data.avrspeed) + "#WaveFitness")
                .socialChannel(Shareable.Builder.TWITTER)
                .build();
        shareAction.share();
    }

    private void populateSongList(){
        _playBar1.setVisibility(LinearLayout.GONE);
        _playBar2.setVisibility(LinearLayout.GONE);
        _playBar3.setVisibility(LinearLayout.GONE);
        _playBar4.setVisibility(LinearLayout.GONE);
        _playBar5.setVisibility(LinearLayout.GONE);

        for(int i = 0; i < data.songs.size(); i++){
            Metadata.Track track = data.songs.get(i);
            final String durationStr =
                    String.format("\n %02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(track.durationMs),
                            TimeUnit.MILLISECONDS.toSeconds(track.durationMs) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(track.durationMs))
                    );
            if(track != null){
                switch (i){
                    case 0:
                        _playBar1.setVisibility(LinearLayout.VISIBLE);
                        Picasso.with(this)
                                .load(track.albumCoverWebUrl)
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        // really ugly darkening trick
                                        final Bitmap copy = source.copy(source.getConfig(), true);
                                        source.recycle();
                                        final Canvas canvas = new Canvas(copy);
                                        //canvas.drawColor(0xbb000000);
                                        return copy;
                                    }

                                    @Override
                                    public String key() {
                                        return "darken";
                                    }
                                })
                                .into(_art1);
                        _title1.setText(track.name);
                        _subTitle1.setText(track.artistName);
                        _time1.setText(durationStr);
                        break;
                    case 1:
                        _playBar2.setVisibility(LinearLayout.VISIBLE);
                        Picasso.with(this)
                                .load(track.albumCoverWebUrl)
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        // really ugly darkening trick
                                        final Bitmap copy = source.copy(source.getConfig(), true);
                                        source.recycle();
                                        final Canvas canvas = new Canvas(copy);
                                        //canvas.drawColor(0xbb000000);
                                        return copy;
                                    }

                                    @Override
                                    public String key() {
                                        return "darken";
                                    }
                                })
                                .into(_art2);
                        _title2.setText(track.name);
                        _subTitle2.setText(track.artistName);
                        _time2.setText(durationStr);
                        break;
                    case 2:
                        _playBar3.setVisibility(LinearLayout.VISIBLE);
                        Picasso.with(this)
                                .load(track.albumCoverWebUrl)
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        // really ugly darkening trick
                                        final Bitmap copy = source.copy(source.getConfig(), true);
                                        source.recycle();
                                        final Canvas canvas = new Canvas(copy);
                                        //canvas.drawColor(0xbb000000);
                                        return copy;
                                    }

                                    @Override
                                    public String key() {
                                        return "darken";
                                    }
                                })
                                .into(_art3);
                        _title3.setText(track.name);
                        _subTitle3.setText(track.artistName);
                        _time3.setText(durationStr);
                        break;
                    case 3:
                        _playBar4.setVisibility(LinearLayout.VISIBLE);
                        Picasso.with(this)
                                .load(track.albumCoverWebUrl)
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        // really ugly darkening trick
                                        final Bitmap copy = source.copy(source.getConfig(), true);
                                        source.recycle();
                                        final Canvas canvas = new Canvas(copy);
                                        //canvas.drawColor(0xbb000000);
                                        return copy;
                                    }

                                    @Override
                                    public String key() {
                                        return "darken";
                                    }
                                })
                                .into(_art4);
                        _title4.setText(track.name);
                        _subTitle4.setText(track.artistName);
                        _time4.setText(durationStr);
                        break;
                    case 4:
                        _playBar5.setVisibility(LinearLayout.VISIBLE);
                        Picasso.with(this)
                                .load(track.albumCoverWebUrl)
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        // really ugly darkening trick
                                        final Bitmap copy = source.copy(source.getConfig(), true);
                                        source.recycle();
                                        final Canvas canvas = new Canvas(copy);
                                        //canvas.drawColor(0xbb000000);
                                        return copy;
                                    }

                                    @Override
                                    public String key() {
                                        return "darken";
                                    }
                                })
                                .into(_art5);
                        _title5.setText(track.name);
                        _subTitle5.setText(track.artistName);
                        _time5.setText(durationStr);
                        break;
                }
            }
        }

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