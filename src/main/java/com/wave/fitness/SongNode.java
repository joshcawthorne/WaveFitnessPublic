package com.wave.fitness;

import com.spotify.sdk.android.player.Metadata;
/**
 * Created by s6236422 on 10/05/2017.
 */

public class SongNode {
    private Metadata metadata;
    private long timeStamp;

    public SongNode(Metadata _metadata){
        metadata = _metadata;
        timeStamp = System.currentTimeMillis();
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
