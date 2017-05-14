package com.wave.fitness;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.rogalabs.lib.model.SocialUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class genreSelection extends AppCompatActivity {

    SpotifyCore core;
    private HamburgerMenu menu;
    Toolbar toolbar;
    SharedPreferences prefs = null;
    Gson gson;

    GridLayout mGrid;

    ImageView popSquare;
    ImageView rockSquare;
    ImageView classicalsquare;
    ImageView funksquare;
    ImageView rapsquare;
    ImageView electronicsquare;
    ImageView jazzfusionsquare;
    ImageView discosquare;
    ImageView indierocksquare;
    ImageView soulsquare;
    ImageView classichitssquare;
    ImageView bigbandsquare;


    Boolean popSelect = false;
    Boolean rockSelect = false;
    Boolean classicalSelect = false;
    Boolean funkSelect = false;
    Boolean rapSelect = false;
    Boolean electronicSelect = false;
    Boolean jazzfusionSelect = false;
    Boolean discoSelect = false;
    Boolean indierockSelect = false;
    Boolean soulSelect = false;
    Boolean classichitsSelect = false;
    Boolean bigbandSelect = false;

    ArrayList<String> selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);
        core = ((SpotifyCore)getApplicationContext());
        gson = new Gson();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Choose a Genre");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences("com.wave.fitness", MODE_PRIVATE);

        FloatingActionButton continueFAB = (FloatingActionButton) findViewById(R.id.continuefab);
        continueFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(genreSelection.this, spotifyActivity.class));

                String test = "";
                for(SpotifyPlaylists.Genre g : SpotifyPlaylists.Genre.values()){
                    test = test + core.selectedGenre.get(g).toString() + ", ";
                }
                Log.e("CHOSEN GENRES",test);
            }
        });

        this.popSquare = (ImageView) findViewById(R.id.popSquare);
        this.popSquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(core.selectedGenre.get(SpotifyPlaylists.Genre.POP)){
                    Log.e("SELECTION", "UNSELECTED");
                    popSquare.setImageResource(R.drawable.popbannersquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.POP, false);
                    popSelect = false;
                }else{
                    Log.e("SELECTION", "SELECTED");
                    popSquare.setImageResource(R.drawable.popbannersquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.POP, true);
                    popSelect = true;
                }
            }
        });

        this.rockSquare = (ImageView) findViewById(R.id.rockSquare);
        this.rockSquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!rockSelect){
                    Log.e("SELECTION", "UNSELECTED");
                    rockSquare.setImageResource(R.drawable.rocksquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.ROCK, true);
                    rockSelect = true;
                }else{
                    Log.e("SELECTION", "SELECTED");
                    rockSquare.setImageResource(R.drawable.rocksquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.ROCK, false);
                    rockSelect = false;
                }
            }
        });

        this.funksquare = (ImageView) findViewById(R.id.funksquare);
        this.funksquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!funkSelect) {
                    Log.e("SELECTION", "SELECTED");
                    funksquare.setImageResource(R.drawable.funksquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.FUNK, true);
                    funkSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    funksquare.setImageResource(R.drawable.funksquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.FUNK, false);
                    funkSelect = false;
                }
            }
        });

        this.classicalsquare = (ImageView) findViewById(R.id.classicalsquare);
        this.classicalsquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!classicalSelect) {
                    Log.e("SELECTION", "SELECTED");
                    classicalsquare.setImageResource(R.drawable.classicalsquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.CLASSICAL, true);
                    classicalSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    classicalsquare.setImageResource(R.drawable.classicalsquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.CLASSICAL, false);
                    classicalSelect = false;
                }
            }
        });

        this.rapsquare = (ImageView) findViewById(R.id.rapsquare);
        this.rapsquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!rapSelect) {
                    Log.e("SELECTION", "SELECTED");
                    rapsquare.setImageResource(R.drawable.rapsquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.RAP, true);
                    rapSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    rapsquare.setImageResource(R.drawable.rapsquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.RAP, false);
                    rapSelect = false;
                }
            }
        });

        this.electronicsquare = (ImageView) findViewById(R.id.electronicsquare);
        this.electronicsquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!electronicSelect) {
                    Log.e("SELECTION", "SELECTED");
                    electronicsquare.setImageResource(R.drawable.electronicsquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.ELECTRONIC, true);
                    electronicSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    electronicsquare.setImageResource(R.drawable.electronicsquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.ELECTRONIC, false);
                    electronicSelect = false;
                }
            }
        });

        this.jazzfusionsquare = (ImageView) findViewById(R.id.jazzfusionsquare);
        this.jazzfusionsquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!jazzfusionSelect) {
                    Log.e("SELECTION", "SELECTED");
                    jazzfusionsquare.setImageResource(R.drawable.jazzfusionsquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.JAZZFUSION, true);
                    jazzfusionSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    jazzfusionsquare.setImageResource(R.drawable.jazzfusionsquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.JAZZFUSION, false);
                    jazzfusionSelect = false;
                }
            }
        });

        this.discosquare = (ImageView) findViewById(R.id.discosquare);
        this.discosquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!discoSelect) {
                    Log.e("SELECTION", "SELECTED");
                    discosquare.setImageResource(R.drawable.discosquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.DISCO, true);
                    discoSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    discosquare.setImageResource(R.drawable.discosquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.DISCO, false);
                    discoSelect = false;
                }
            }
        });

        this.indierocksquare = (ImageView) findViewById(R.id.indierocksquare);
        this.indierocksquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!indierockSelect) {
                    Log.e("SELECTION", "SELECTED");
                    indierocksquare.setImageResource(R.drawable.indierocksquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.INDIEROCK, true);
                    indierockSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    indierocksquare.setImageResource(R.drawable.indierocksquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.INDIEROCK, false);
                    indierockSelect = false;
                }
            }
        });

        this.soulsquare = (ImageView) findViewById(R.id.soulsquare);
        this.soulsquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!soulSelect) {
                    Log.e("SELECTION", "SELECTED");
                    soulsquare.setImageResource(R.drawable.soulsquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.SOUL, true);
                    soulSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    soulsquare.setImageResource(R.drawable.soulsquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.SOUL, false);
                    soulSelect = false;
                }
            }
        });

        this.classichitssquare = (ImageView) findViewById(R.id.classichitssquare);
        this.classichitssquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!classichitsSelect) {
                    Log.e("SELECTION", "SELECTED");
                    classichitssquare.setImageResource(R.drawable.classichitssquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.HITS, true);
                    classichitsSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    classichitssquare.setImageResource(R.drawable.classichitssquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.HITS, false);
                    classichitsSelect = false;
                }
            }
        });

        this.bigbandsquare = (ImageView) findViewById(R.id.bigbandsquare);
        this.bigbandsquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!bigbandSelect) {
                    Log.e("SELECTION", "SELECTED");
                    bigbandsquare.setImageResource(R.drawable.bigbandsquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.BIGBAND, true);
                    bigbandSelect = true;
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    bigbandsquare.setImageResource(R.drawable.bigbandsquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.BIGBAND, false);
                    bigbandSelect = false;
                }
            }
        });

    }

    @Override
    protected  void onResume() {
        super.onResume();
        menu = new HamburgerMenu(this, gson.fromJson(prefs.getString("user", ""), SocialUser.class), toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}



