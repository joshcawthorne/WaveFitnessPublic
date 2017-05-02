package com.wave.fitness;

import android.location.Location;

public class RouteNode {
    public Location location;//TODO: move these to proper accessors
    public long timeStamp; //timestamp is system time in milliseconds

    public RouteNode(Location _location, long _timestamp){
        location = _location;
        timeStamp = _timestamp;
    }

    public RouteNode(Location _location){
        location = _location;
        timeStamp = System.currentTimeMillis();
    }
}
