package com.wave.fitness;

import com.squareup.otto.Bus;

/**
 * Created by s6236422 on 13/05/2017.
 */

public class BusProvider {
    public static Bus BUS;

    public static Bus getInstance() {
        return BUS;
    }

}
