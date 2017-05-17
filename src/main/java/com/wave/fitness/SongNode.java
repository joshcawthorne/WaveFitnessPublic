package com.wave.fitness;

import com.spotify.sdk.android.player.Metadata;

public class SongNode {
    private Metadata.Track trackdata;
    private long timeStamp;

    public SongNode(Metadata.Track _trackdata){
        trackdata = _trackdata;
        timeStamp = System.currentTimeMillis();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Metadata.Track getTrackdata() {
        return trackdata;
    }
}
