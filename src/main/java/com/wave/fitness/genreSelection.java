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

    Boolean popSelect = false;
    Boolean rockSelect = false;

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



