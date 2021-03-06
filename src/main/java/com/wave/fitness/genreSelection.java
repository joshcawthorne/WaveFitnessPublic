package com.wave.fitness;


import android.app.ProgressDialog;
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

import java.util.ArrayList;

public class genreSelection extends AppCompatActivity {

    /* Class to generate a users genre selection */

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
    ImageView electronicsquare;
    ImageView jazzfusionsquare;
    ImageView indierocksquare;
    ImageView classichitssquare;

    Boolean popSelect = false;
    Boolean rockSelect = false;
    Boolean classicalSelect = false;
    Boolean funkSelect = false;
    Boolean electronicSelect = false;
    Boolean jazzfusionSelect = false;
    Boolean indierockSelect = false;
    Boolean classichitsSelect = false;

    int selected = 0;

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
                core.dashboardCard = false;
                final ProgressDialog progressDialog = new ProgressDialog(genreSelection.this,
                        R.style.DialogBox);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Building your mixtape...");
                progressDialog.show();

                startActivity(new Intent(genreSelection.this, fragmentBaseActivity.class));
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
                    if(selected > 0){
                        selected --;
                    }
                    checkFab();
                }else{
                    Log.e("SELECTION", "SELECTED");
                    popSquare.setImageResource(R.drawable.popbannersquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.POP, true);
                    popSelect = true;
                    selected ++;
                    checkFab();
                }
            }
        });

        this.rockSquare = (ImageView) findViewById(R.id.rockSquare);
        this.rockSquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(core.selectedGenre.get(SpotifyPlaylists.Genre.ROCK)){
                    Log.e("SELECTION", "UNSELECTED");
                    rockSquare.setImageResource(R.drawable.rocksquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.ROCK, false);
                    rockSelect = true;
                    if(selected > 0){
                        selected --;
                    }
                    checkFab();
                }else{
                    Log.e("SELECTION", "SELECTED");
                    rockSquare.setImageResource(R.drawable.rocksquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.ROCK, true);
                    rockSelect = false;
                    selected ++;
                    checkFab();
                }
            }
        });

        this.funksquare = (ImageView) findViewById(R.id.funksquare);
        this.funksquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (core.selectedGenre.get(SpotifyPlaylists.Genre.FUNK)) {
                    Log.e("SELECTION", "SELECTED");
                    funksquare.setImageResource(R.drawable.funksquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.FUNK, false);
                    funkSelect = true;
                    if(selected > 0){
                        selected --;
                    }
                    checkFab();
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    funksquare.setImageResource(R.drawable.funksquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.FUNK, true);
                    funkSelect = false;
                    selected ++;
                    checkFab();
                }
            }
        });

        this.classicalsquare = (ImageView) findViewById(R.id.classicalsquare);
        this.classicalsquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (core.selectedGenre.get(SpotifyPlaylists.Genre.CLASSICAL)) {
                    Log.e("SELECTION", "SELECTED");
                    classicalsquare.setImageResource(R.drawable.classicalsquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.CLASSICAL, false);
                    classicalSelect = true;
                    if(selected > 0){
                        selected --;
                    }
                    checkFab();
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    classicalsquare.setImageResource(R.drawable.classicalsquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.CLASSICAL, true);
                    classicalSelect = false;
                    selected ++;
                    checkFab();
                }
            }
        });

        this.electronicsquare = (ImageView) findViewById(R.id.electronicsquare);
        this.electronicsquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (core.selectedGenre.get(SpotifyPlaylists.Genre.ELECTRONIC)) {
                    Log.e("SELECTION", "SELECTED");
                    electronicsquare.setImageResource(R.drawable.electronicsquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.ELECTRONIC, false);
                    electronicSelect = true;
                    if(selected > 0){
                        selected --;
                    }
                    checkFab();
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    electronicsquare.setImageResource(R.drawable.electronicsquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.ELECTRONIC, true);
                    electronicSelect = false;
                    selected ++;
                    checkFab();
                }
            }
        });

        this.jazzfusionsquare = (ImageView) findViewById(R.id.jazzfusionsquare);
        this.jazzfusionsquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (core.selectedGenre.get(SpotifyPlaylists.Genre.JAZZFUSION)) {
                    Log.e("SELECTION", "SELECTED");
                    jazzfusionsquare.setImageResource(R.drawable.jazzfusionsquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.JAZZFUSION, false);
                    jazzfusionSelect = true;
                    if(selected > 0){
                        selected --;
                    }
                    checkFab();
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    jazzfusionsquare.setImageResource(R.drawable.jazzfusionsquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.JAZZFUSION, true);
                    jazzfusionSelect = false;
                    selected ++;
                    checkFab();
                }
            }
        });

        this.indierocksquare = (ImageView) findViewById(R.id.indierocksquare);
        this.indierocksquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (core.selectedGenre.get(SpotifyPlaylists.Genre.INDIEROCK)) {
                    Log.e("SELECTION", "SELECTED");
                    indierocksquare.setImageResource(R.drawable.indierocksquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.INDIEROCK, false);
                    indierockSelect = true;
                    if(selected > 0){
                        selected --;
                    }
                    checkFab();
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    indierocksquare.setImageResource(R.drawable.indierocksquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.INDIEROCK, true);
                    indierockSelect = false;
                    selected ++;
                    checkFab();
                }
            }
        });

        this.classichitssquare = (ImageView) findViewById(R.id.classichitssquare);
        this.classichitssquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (core.selectedGenre.get(SpotifyPlaylists.Genre.HITS)) {
                    Log.e("SELECTION", "SELECTED");
                    classichitssquare.setImageResource(R.drawable.classichitssquare);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.HITS, false);
                    classichitsSelect = true;
                    if(selected > 0){
                        selected --;
                    }
                    checkFab();
                }
                else {
                    Log.e("SELECTION", "UNSELECTED");
                    classichitssquare.setImageResource(R.drawable.classichitssquareselected);
                    core.selectedGenre.put(SpotifyPlaylists.Genre.HITS, true);
                    classichitsSelect = false;
                    selected ++;
                    checkFab();
                }
            }
        });
    }

    private void checkFab() {
        FloatingActionButton continueFAB = (FloatingActionButton) findViewById(R.id.continuefab);
        Log.e("FAB", selected + "Run");
        if(selected == 0) {
            continueFAB.hide();
        }
        else {
            continueFAB.show();
        }
    }

    @Override
    protected  void onResume() {
        super.onResume();

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



