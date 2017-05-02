package com.wave.fitness;

import java.util.ArrayList;
import java.util.Date;

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
    public static final String KEY_ROUTE = "route";
    public static final String KEY_SONGS = "songs";


    // property help us to keep data
    public int id;
    public Date date;
    public long duration;
    public long distance;
    public double avrspeed;

    //Need to convert to JSON before storing as String
    public ArrayList route;
    public ArrayList songs;

}
