package com.wave.fitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.util.Log;

import com.rogalabs.lib.Callback;
import com.rogalabs.lib.LoginView;
import com.rogalabs.lib.model.SocialUser;

import butterknife.ButterKnife;
import butterknife.InjectView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class setupActivity extends LoginView {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    SharedPreferences prefs = null;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.login) Button _loginButton;
    @InjectView(R.id.facebook) Button _fbloginButton;
    @InjectView(R.id.signup) TextView _signupLink;

    String fontPath = "assets/fonts/";

    private SpotifyCore core;

    @Override
    protected void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);

        setContentView(R.layout.activity_signup);

        core = ((SpotifyCore)getApplicationContext());

        prefs = getSharedPreferences("com.wave.fitness", MODE_PRIVATE);

        //Initialize Calligraphy
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("nexabold.oft")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

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

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), OLDsignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
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

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}