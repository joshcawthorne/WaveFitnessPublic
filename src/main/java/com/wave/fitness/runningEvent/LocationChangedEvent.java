package com.wave.fitness.runningEvent;

import android.location.Location;

import com.wave.fitness.RouteNode;

import java.util.ArrayList;

/**
 * Created by s6236422 on 13/05/2017.
 */

public class LocationChangedEvent {
    public ArrayList<Location> route;
    public LocationChangedEvent(ArrayList<Location> data){
        route = data;
    }
}
