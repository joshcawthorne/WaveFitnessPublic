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
            }
        });

        this.funksquare = (ImageView) findViewById(R.id.funksquare);
        this.funksquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!funkSelect) {
                    Log.e("SELECTION", "SELECTED");
                    funksquare.setImageResource(R.drawable.funksquareselected);
                    funkSelect = true;
                }
                else if(funkSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    funksquare.setImageResource(R.drawable.funksquare);
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
                    classicalSelect = true;
                }
                else if(classicalSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    classicalsquare.setImageResource(R.drawable.classicalsquare);
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
                    rapSelect = true;
                }
                else if(rapSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    rapsquare.setImageResource(R.drawable.rapsquare);
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
                    electronicSelect = true;
                }
                else if(electronicSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    electronicsquare.setImageResource(R.drawable.electronicsquare);
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
                    jazzfusionSelect = true;
                }
                else if(jazzfusionSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    jazzfusionsquare.setImageResource(R.drawable.jazzfusionsquare);
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
                    discoSelect = true;
                }
                else if(discoSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    discosquare.setImageResource(R.drawable.discosquare);
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
                    indierockSelect = true;
                }
                else if(indierockSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    indierocksquare.setImageResource(R.drawable.indierocksquare);
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
                    soulSelect = true;
                }
                else if(soulSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    soulsquare.setImageResource(R.drawable.soulsquare);
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
                    classichitsSelect = true;
                }
                else if(classichitsSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    classichitssquare.setImageResource(R.drawable.classichitssquare);
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
                    bigbandSelect = true;
                }
                else if(bigbandSelect) {
                    Log.e("SELECTION", "UNSELECTED");
                    bigbandsquare.setImageResource(R.drawable.bigbandsquare);
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



