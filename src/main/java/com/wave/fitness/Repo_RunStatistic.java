package com.wave.fitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spotify.sdk.android.player.Metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by s6236422 on 10/05/2017.
 */

public class Repo_RunStatistic {
    private DBHelper dbHelper;

    public Repo_RunStatistic(Context context) {
        dbHelper = new DBHelper(context);
    }

    private Gson gson = new Gson();

    public int insert(Data_RunStatistic data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Data_RunStatistic.KEY_ID, data.id);
        values.put(Data_RunStatistic.KEY_DATE, data.date);
        values.put(Data_RunStatistic.KEY_DURATION, data.duration);
        values.put(Data_RunStatistic.KEY_DISTANCE, data.distance);
        values.put(Data_RunStatistic.KEY_AVRSPEED, data.avrspeed);
        values.put(Data_RunStatistic.KEY_CALORIES, data.calories);
        values.put(Data_RunStatistic.KEY_STEPPERMIN, data.totalStep);
        values.put(Data_RunStatistic.KEY_ROUTE, gson.toJson(data.route));
        values.put(Data_RunStatistic.KEY_SONGS, gson.toJson(data.songs));

        long id = db.insert(Data_RunStatistic.TABLE, null, values);
        db.close();
        return (int) id;
    }

    public Data_RunStatistic getEntrybyID(int _id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  " +
                Data_RunStatistic.KEY_ID + "," +
                Data_RunStatistic.KEY_DATE + "," +
                Data_RunStatistic.KEY_DURATION + "," +
                Data_RunStatistic.KEY_DISTANCE + "," +
                Data_RunStatistic.KEY_AVRSPEED + "," +
                Data_RunStatistic.KEY_CALORIES + "," +
                Data_RunStatistic.KEY_STEPPERMIN + "," +
                Data_RunStatistic.KEY_ROUTE + "," +
                Data_RunStatistic.KEY_SONGS +
                " FROM " + Data_RunStatistic.TABLE
                + " WHERE " +
                Data_RunStatistic.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        Data_RunStatistic data = new Data_RunStatistic();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(_id)});

        if (cursor.moveToFirst()) {
            do {
                data.id = cursor.getInt(cursor.getColumnIndex(Data_RunStatistic.KEY_ID));
                data.date = cursor.getLong(cursor.getColumnIndex(Data_RunStatistic.KEY_DATE));
                data.duration = cursor.getLong(cursor.getColumnIndex(Data_RunStatistic.KEY_DURATION));
                data.distance = cursor.getFloat(cursor.getColumnIndex(Data_RunStatistic.KEY_DISTANCE));
                data.avrspeed = cursor.getInt(cursor.getColumnIndex(Data_RunStatistic.KEY_AVRSPEED));
                data.calories = cursor.getInt(cursor.getColumnIndex(Data_RunStatistic.KEY_CALORIES));
                data.totalStep = cursor.getInt(cursor.getColumnIndex(Data_RunStatistic.KEY_STEPPERMIN));
                data.route = stringToArray(cursor.getString(cursor.getColumnIndex(Data_RunStatistic.KEY_ROUTE)), Location[].class);
                data.songs = stringToArray(cursor.getString(cursor.getColumnIndex(Data_RunStatistic.KEY_SONGS)), Metadata.Track[].class);


            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return data;

    }
    private static <T> List<T> stringToArray(String s, Class<T[]> clazz){
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr);
    }
}
