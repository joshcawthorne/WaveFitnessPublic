package com.wave.fitness.runningEvent;

import com.wave.fitness.RouteNode;

import java.util.ArrayList;

/**
 * Created by s6236422 on 13/05/2017.
 */

public class LocationChangedEvent {
    public ArrayList<RouteNode> route;
    public LocationChangedEvent(ArrayList<RouteNode> data){
        route = data;
    }
}
