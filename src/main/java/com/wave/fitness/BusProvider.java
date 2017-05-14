package com.wave.fitness;

import com.squareup.otto.Bus;

/**
 * Created by s6236422 on 13/05/2017.
 */

public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
