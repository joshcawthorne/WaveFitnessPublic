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

import butterknife.ButterKnife;
import butterknife.InjectView;


import studios.codelight.smartloginlibrary.LoginType;
import studios.codelight.smartloginlibrary.SmartLogin;
import studios.codelight.smartloginlibrary.SmartLoginCallbacks;
import studios.codelight.smartloginlibrary.SmartLoginConfig;
import studios.codelight.smartloginlibrary.SmartLoginFactory;
import studios.codelight.smartloginlibrary.users.SmartUser;
import studios.codelight.smartloginlibrary.util.SmartLoginException;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class setupActivity extends AppCompatActivity implements SmartLoginCallbacks{

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    SharedPreferences prefs = null;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.login) Button _loginButton;
    @InjectView(R.id.facebook) Button _fbloginButton;
    @InjectView(R.id.signup) TextView _signupLink;

    String fontPath = "assets/fonts/";

    SmartLoginConfig config;
    SmartLogin smartLogin;
    private SpotifyCore core;

    @Override
    protected void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);

        setContentView(R.layout.activity_signup);

        core = ((SpotifyCore)getApplicationContext());

        config = new SmartLoginConfig(this /* Context */, this /* SmartLoginCallbacks */);
        config.setFacebookAppId(getString(R.string.facebook_app_id));

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
                smartLogin = SmartLoginFactory.build(LoginType.Google);
                smartLogin.login(config);
            }
        });

        _fbloginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /* Facebook Login */
                smartLogin = SmartLoginFactory.build(LoginType.Facebook);
                smartLogin.login(config);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        smartLogin.onActivityResult(requestCode, resultCode, data, config);
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

    @Override
    public void onLoginSuccess(SmartUser user) {
        prefs.edit().putBoolean("firstrun", false).commit();

        String firstName = user.getFirstName();

        core.firstName = firstName;

        Intent startDashboard = new Intent(setupActivity.this, DashboardActivity.class);
        setupActivity.this.startActivity(startDashboard);


    }

    @Override
    public void onLoginFailure(SmartLoginException e) {
        Log.e("Login", "Failed");
    }

    @Override
    public SmartUser doCustomLogin() {
        return null;
    }

    @Override
    public SmartUser doCustomSignup() {
        return null;
    }
}