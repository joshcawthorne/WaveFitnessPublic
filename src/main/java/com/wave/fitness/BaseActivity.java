package com.wave.fitness;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Josh Cawthorne on 03/05/2017.
 */

public class BaseActivity extends MainActivity {

    // Activity code here

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_camera:
                // do what you want here
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
