package com.wave.fitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rogalabs.lib.Callback;
import com.rogalabs.lib.LoginView;
import com.rogalabs.lib.model.SocialUser;

import butterknife.ButterKnife;
import butterknife.InjectView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class setupActivity extends LoginView {

    /* Class that allows auser to login via Facebook */

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    SharedPreferences prefs = null;

    @InjectView(R.id.facebook) Button _fbloginButton;

    String fontPath = "assets/fonts/";

    private SpotifyCore core;

    @Override
    protected void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);

        setContentView(R.layout.activity_login);

        core = ((SpotifyCore)getApplicationContext());

        final Gson gson = new Gson();

        prefs = getSharedPreferences("com.wave.fitness", MODE_PRIVATE);

        //Initialize Calligraphy
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("nexabold.oft")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        ButterKnife.inject(this);

        _fbloginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginWithGoogle(new Callback() {
                    @Override
                    public void onSuccess(SocialUser socialUser) {
                        prefs.edit().putBoolean("firstrun", false).commit();

                        core.user = socialUser;
                        Intent startDashboard = new Intent(setupActivity.this, DashboardActivity.class);
                        setupActivity.this.startActivity(startDashboard);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

            }
        });

        _fbloginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /* Facebook Login */
                loginWithFacebook(new Callback() {
                    @Override
                    public void onSuccess(SocialUser socialUser) {
                        prefs.edit().putBoolean("firstrun", false).commit();
                        prefs.edit().putString("user", gson.toJson(socialUser)).commit();
                        core.user = socialUser;

                        String name = socialUser.getName();

                        String arr[] = name.split(" ", 2);

                        String firstName = arr[0];

                        core.firstName = firstName;

                        Intent startDashboard = new Intent(setupActivity.this, DashboardActivity.class);
                        setupActivity.this.startActivity(startDashboard);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }
}