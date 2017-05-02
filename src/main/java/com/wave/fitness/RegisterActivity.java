package com.wave.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();


        Button button = (Button) findViewById(R.id.continueBttn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));}
        });
    }

}