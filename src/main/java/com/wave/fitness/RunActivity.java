package com.wave.fitness;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import com.wave.fitness.pedometer.PedometerSettings;
import com.wave.fitness.pedometer.StepService;
import com.wave.fitness.pedometer.Utils;

import java.util.ArrayList;

public class RunActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Pedometer code from package name.begi.levente.pedometer
    private static final String TAG = "Run";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;

    private TextView mStepValueView;
    private TextView mPaceValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;
    TextView mDesiredPaceView;
    private int mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private float mSpeedValue;
    private int mCaloriesValue;
    private float mDesiredPaceOrSpeed;
    private int mMaintain;
    private boolean mIsMetric;
    private float mMaintainInc;
    private boolean mQuitting = false; // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;

    //Legacy code from Lewis for GoogleMapAPI
    private Handler uiHandler = new Handler();
    private Runnable uiRunnable;
    private Location lastKnownLocation = null;
    private ArrayList<RouteNode> route;
    private boolean tracking = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    private final int ACCESS_FINE_LOCATION_REQUEST = 0;

    Chronometer chrono;
    TextView speedView;

    protected void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                //TODO: Display a snackbar explaining the requirement of this permission

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_REQUEST);
            }
        }
    }

    Drawer menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("MAP", "Created");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Log.d("API", "Google API Client Created");

        chrono = (Chronometer) findViewById(R.id.chronometer);
        chrono.setFormat("Time Running - %s");
        Log.d("APP", "Chrono Setup");

        speedView = (TextView) findViewById(R.id.speed);
        Log.d("APP", "Pedometer Setup");

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withName("Test").withEmail("test@gmail.com")
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem music = new PrimaryDrawerItem().withIdentifier(1).withName("music");

        menu = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        music, new DividerDrawerItem(), new SecondaryDrawerItem().withName("test")
                )
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener(){
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                // do something with the clicked item :D
                                startActivity(new Intent(RunActivity.this, genreSelection.class));
                                return true;
                            }
                        }
                )
                .build();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        menu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        mStepValue = 0;
        mPaceValue = 0;
        mUtils = Utils.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermissions();
        Log.d("APP", "Application Started");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }

        super.onPause();
        savePaceSetting();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);

        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();

        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        }
        else if (mIsRunning) {
            bindStepService();
        }

        mPedometerSettings.clearServiceRunning();

        mStepValueView     = (TextView) findViewById(R.id.step_value);
        mPaceValueView     = (TextView) findViewById(R.id.pace_value);
        mDistanceValueView = (TextView) findViewById(R.id.distance_value);
        mSpeedValueView    = (TextView) findViewById(R.id.speed_value);
        mCaloriesValueView = (TextView) findViewById(R.id.calories_value);
        mDesiredPaceView   = (TextView) findViewById(R.id.desired_pace_value);

        mIsMetric = mPedometerSettings.isMetric();
        ((TextView) findViewById(R.id.distance_units)).setText(getString(
                mIsMetric
                        ? R.string.kilometers
                        : R.string.miles
        ));
        ((TextView) findViewById(R.id.speed_units)).setText(getString(
                mIsMetric
                        ? R.string.kilometers_per_hour
                        : R.string.miles_per_hour
        ));

        mMaintain = mPedometerSettings.getMaintainOption();
        ((LinearLayout) this.findViewById(R.id.desired_pace_control)).setVisibility(
                mMaintain != PedometerSettings.M_NONE
                        ? View.VISIBLE
                        : View.GONE
        );
        if (mMaintain == PedometerSettings.M_PACE) {
            mMaintainInc = 5f;
            mDesiredPaceOrSpeed = (float)mPedometerSettings.getDesiredPace();
        }
        else
        if (mMaintain == PedometerSettings.M_SPEED) {
            mDesiredPaceOrSpeed = mPedometerSettings.getDesiredSpeed();
            mMaintainInc = 0.1f;
        }
        Button button1 = (Button) findViewById(R.id.button_desired_pace_lower);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDesiredPaceOrSpeed -= mMaintainInc;
                mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
                displayDesiredPaceOrSpeed();
                setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
            }
        });
        Button button2 = (Button) findViewById(R.id.button_desired_pace_raise);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDesiredPaceOrSpeed += mMaintainInc;
                mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
                displayDesiredPaceOrSpeed();
                setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
            }
        });
        if (mMaintain != PedometerSettings.M_NONE) {
            ((TextView) findViewById(R.id.desired_pace_label)).setText(
                    mMaintain == PedometerSettings.M_PACE
                            ? R.string.desired_pace
                            : R.string.desired_speed
            );
        }


        displayDesiredPaceOrSpeed();
    }

    private void displayDesiredPaceOrSpeed() {
        if (mMaintain == PedometerSettings.M_PACE) {
            mDesiredPaceView.setText("" + (int)mDesiredPaceOrSpeed);
        }
        else {
            mDesiredPaceView.setText("" + mDesiredPaceOrSpeed);
        }
    }

    @Override
    protected void onStop() {
        Log.d("APP", "Application Stopping");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d("API", "Location Request Created");
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d("API", "Location Request Applied");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("MAP", "Map Ready");

        uiRunnable = new Runnable() {
            @Override
            public void run() {
                if (tracking) {
                    mMap.clear();
                    PolylineOptions lineOpt = new PolylineOptions();
                    for (int i = 0; i < route.size() - 1; i++) {
                        Location loc = route.get(i).location;
                        lineOpt.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
                    }
                    lineOpt.color(Color.BLUE);
                    lineOpt.width(5.0f);
                    lineOpt.visible(true);
                    mMap.addPolyline(lineOpt);

                } else {
                    mMap.clear();
                }
                uiHandler.postDelayed(uiRunnable, 500);
            }
        };
        uiRunnable.run();
    }

    @Override
    public void onLocationChanged(Location _location) {
        Location location = _location;
        Log.d("LOC", "Getting Location");
        if (location != null) {
            Log.d("LOC", String.format("Lat:%f, Lat:%f", location.getLatitude(), location.getLongitude()));
            lastKnownLocation = location;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 19f);
            mMap.animateCamera(cameraUpdate);
            Log.d("MAP", "Camera Moved To Current Location");

            if (tracking) {
                route.add(new RouteNode(location));
                Log.d("RUN", "Tracked A Running Node");
            }

        } else {
            Log.d("LOC", "NULL Location");
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 19f);
            mMap.animateCamera(cameraUpdate);
            Log.d("MAP", "Camera Moved To Last Known Location");
        }
    }

    public void toggleTracking(View _view) {
        if (tracking) {
            Toast.makeText(this, "Run Finished!", Toast.LENGTH_LONG).show();
            chrono.stop();
            Log.d("RUN", "Run Tracking Stopped");
        } else {
            Toast.makeText(this, "Run Started!",
                    Toast.LENGTH_LONG).show();

            route = new ArrayList<RouteNode>();
            route.add(new RouteNode(lastKnownLocation));

            chrono.setBase(SystemClock.elapsedRealtime());
            chrono.start();

            Log.d("RUN", "Run Tracking Started");
        }
        tracking = !tracking;
    }

    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        super.onDestroy();
    }

    protected void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onDestroy();
    }

    private void setDesiredPaceOrSpeed(float desiredPaceOrSpeed) {
        if (mService != null) {
            if (mMaintain == PedometerSettings.M_PACE) {
                mService.setDesiredPace((int)desiredPaceOrSpeed);
            }
            else
            if (mMaintain == PedometerSettings.M_SPEED) {
                mService.setDesiredSpeed(desiredPaceOrSpeed);
            }
        }
    }

    private void savePaceSetting() {
        mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed);
    }

    private StepService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };


    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(this,
                    StepService.class));
        }
    }

    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(this,
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }

    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(this,
                    StepService.class));
        }
        mIsRunning = false;
    }

    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();
        }
        else {
            mStepValueView.setText("0");
            mPaceValueView.setText("0");
            mDistanceValueView.setText("0");
            mSpeedValueView.setText("0");
            mCaloriesValueView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    private static final int MENU_SETTINGS = 8;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;

    /* Creates the menu items */
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mIsRunning) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
                    .setIcon(android.R.drawable.ic_media_pause)
                    .setShortcut('1', 'p');
        }
        else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
                    .setIcon(android.R.drawable.ic_media_play)
                    .setShortcut('1', 'p');
        }
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setShortcut('2', 'r');
        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
                .setIcon(android.R.drawable.ic_menu_preferences)
                .setShortcut('8', 's')
                .setIntent(new Intent(this, Settings.class));
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                unbindStepService();
                stopStepService();
                return true;
            case MENU_RESUME:
                startStepService();
                bindStepService();
                return true;
            case MENU_RESET:
                resetValues(true);
                return true;
        }
        return false;
    }

    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };

    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
                case PACE_MSG:
                    mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) {
                        mPaceValueView.setText("0");
                    }
                    else {
                        mPaceValueView.setText("" + (int)mPaceValue);
                    }
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) {
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) {
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) {
                        mCaloriesValueView.setText("0");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };
}