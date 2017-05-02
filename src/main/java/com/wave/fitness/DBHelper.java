package com.wave.fitness;

/**
 * Created by IT001 on 23-Jun-16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.wave.fitness.Data_RunStatistic.TABLE;

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "crud.db";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here

        String CREATE_TABLE_RUNSTATISTIC = "CREATE TABLE " + Data_RunStatistic.TABLE  + "("
                + Data_RunStatistic.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Data_RunStatistic.KEY_DATE + " LONG, "
                + Data_RunStatistic.KEY_DURATION + " LONG, "
                + Data_RunStatistic.KEY_DISTANCE + " LONG, "
                + Data_RunStatistic.KEY_AVRSPEED + " DOUBLE, "
                + Data_RunStatistic.KEY_ROUTE + " STRING, "
                + Data_RunStatistic.KEY_SONGS + " STRING)";

        db.execSQL(CREATE_TABLE_RUNSTATISTIC);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Data_RunStatistic.TABLE);

        // Create tables again
        onCreate(db);

    }

}