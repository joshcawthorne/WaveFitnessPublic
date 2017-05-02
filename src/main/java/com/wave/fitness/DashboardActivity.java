package com.wave.fitness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Created by Aaron on 02/05/2017.
 */

public class DashboardActivity extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }


    public void onMusicPlayerButtonClick(View view)
    {
        Intent intent = new Intent(DashboardActivity.this, DemoActivity.class);
        startActivity(intent);
    }

}
