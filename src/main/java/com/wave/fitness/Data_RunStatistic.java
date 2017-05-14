package com.wave.fitness;

import android.location.Location;

import com.spotify.sdk.android.player.Metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by s6236422 on 02/05/2017.
 */

public class Data_RunStatistic {
    // Labels table name
    public static final String TABLE = "RunStatistic";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_AVRSPEED = "avrspeed";
    public static final String KEY_CALORIES = "calories";
    public static final String KEY_ROUTE = "route";
    public static final String KEY_SONGS = "songs";


    // property help us to keep data
    public int id;
    public long date;
    public long duration;
    public long distance;
    public double avrspeed;
    public int calories;

    //Need to convert to JSON before storing as String
    public ArrayList<Location> route;
    public ArrayList<Metadata.Track> songs;

    public Data_RunStatistic(){
        route = new ArrayList<Location>();
        songs = new ArrayList<Metadata.Track>();
    }

}
