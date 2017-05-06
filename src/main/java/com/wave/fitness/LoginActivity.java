package com.wave.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ImageButton ibutt = (ImageButton) findViewById(R.id.imageButton);
        ibutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class ));
            }
        });

        Button button = (Button) findViewById(R.id.registerBttn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(LoginActivity.this, signupActivity.class));}
            });
    }

}
