/*

    Wave Fitness Version 1.0

    This is version 1.0 of Wave's Spotify implementation. It utilises the Spotify Android SDK, and it's example project.

    This is merely a foundation to the app, and should be heavily adopted and changed.

    ~ Josh Cawthorne.

    IMPORTANT: DUE TO A BUG WITH THE SPOTIFY SDK, IF A USER HAS THE SPOTIFY APP INSTALLED ON THEIR PHONE, AND THEY TRY TO SIGN IN,
    AUTHENTICATION WILL FAIL.

 */
package com.wave.fitness;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackBitrate;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DemoActivity extends AppCompatActivity implements
        Player.NotificationCallback, ConnectionStateCallback, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Constants

    //Required to make the app work - this is our public key and callback URL!
    //Also included suppress warnings.
    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "0389348b1134489d870dc8730a7fe33a";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "testschema://callback";

    //Tempoary strings containing the test_song_uri's - at the moment the app only calls these strings.
    //// TODO: 26/04/2017 Imppliment genre/search to place tracks into these strings, instead of having static music.
    @SuppressWarnings("SpellCheckingInspection")
    private static String TEST_SONG_URI = "spotify:track:7v9qEzfnEuJDjBg8B7GppL";
    @SuppressWarnings("SpellCheckingInspection")
    private static String TEST_SONG_48kHz_URI = "spotify:track:3wxTNS3aqb9RbBLZgJdZgH";
    @SuppressWarnings("SpellCheckingInspection")
    //NOTE: It took me an age to work out that playlists require both the playlist creator (User) AND the ID!
    private static String TEST_PLAYLIST_URI = "spotify:user:4joshua-cawthorne:playlist:4vr1l7iKUXfsmxEFlQabwG";
    @SuppressWarnings("SpellCheckingInspection")
    private static String TEST_ALBUM_URI = "spotify:album:4ei0RkOn29dn174wallj5w";
    @SuppressWarnings("SpellCheckingInspection")
    private static String TEST_QUEUE_SONG_URI = "spotify:track:5EEOjaJyWvfMglmEwf9bG3";

    //Request code that will be passed together with authentication result to the onAuthenticationResult
    private static final int REQUEST_CODE = 1337;

    //These are UI controls, which can only be used after a user has logged into spotify.
    private static final int[] REQUIRES_INITIALIZED_STATE = {
            R.id.pause_button,
    };

    //These are UI controls which can only be used once a song is playing.
    private static final int[] REQUIRES_PLAYING_STATE = {
            //R.id.skip_next_button,
            //R.id.skip_prev_button,
    };
    public static final String TAG = "SpotifySdkDemo";

    //Fields
    private SpotifyPlayer mPlayer;
    private PlaybackState mCurrentPlaybackState;
    private BroadcastReceiver mNetworkStateReceiver;
    private TextView mMetadataText;
    private EditText mSeekEditText;
    private ScrollView mStatusTextScrollView;
    private Metadata mMetadata;
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
    //Pedometer pedo;
    TextView speedView;

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            logStatus("OK!");
        }

        @Override
        public void onError(Error error) {
            logStatus("ERROR:" + error);
        }
    };

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

    //Initialization
    Drawer menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        onLoginButtonClicked(null);

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

        //chrono = (Chronometer) findViewById(R.id.chronometer);
        //chrono.setFormat("Time Running - %s");
        Log.d("APP", "Chrono Setup");

        //pedo = new Pedometer();
        speedView = (TextView) findViewById(R.id.speed);
        Log.d("APP", "Pedometer Setup");

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                //.withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Josh Cawthorne").withEmail("joshcawthorne97@gmail.com")/*.withIcon(getResources().getDrawable(R.drawable.profile))*/
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem music = new PrimaryDrawerItem().withIdentifier(1).withName("Music");

        menu = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        music, new SecondaryDrawerItem().withName("Start A Run"), new SecondaryDrawerItem().withName("Past Runs"),
                        new DividerDrawerItem(), new SecondaryDrawerItem().withName("Settings"), new SecondaryDrawerItem().withName("Logout")
                )
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener(){
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                // do something with the clicked item :D
                                startActivity(new Intent(DemoActivity.this, DashboardActivity.class));
                                return true;
                            }
                        }
                )
                .build();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        menu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        // Get a reference to any UI widgets that will be needed.
        mMetadataText = (TextView) findViewById(R.id.metadata);
        mStatusTextScrollView = (ScrollView) findViewById(R.id.status_text_container);

        updateView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up the broadcast receiver for network events.
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPlayer != null) {
                    Connectivity connectivity = getNetworkConnectivity(getBaseContext());
                    logStatus("Network state changed: " + connectivity.toString());
                    mPlayer.setConnectivityStatus(mOperationCallback, connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);

        if (mPlayer != null) {
            mPlayer.addNotificationCallback(DemoActivity.this);
            mPlayer.addConnectionStateCallback(DemoActivity.this);
        }
    }

    private Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }

    }

    //Authentication

    private void openLoginWindow() {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming"})
                .build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    onAuthenticationComplete(response);
                    break;

                // Auth flow returned an error
                case ERROR:
                    logStatus("Auth error: " + response.getError());
                    break;
                // Most likely auth flow was cancelled
                default:
                    logStatus("Auth result: " + response.getType());
            }
        }
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        // Once we have obtained an authorization token, we can proceed with creating a Player.
        logStatus("Got authentication token");
        if (mPlayer == null) {
            Config playerConfig = new Config(getApplicationContext(), authResponse.getAccessToken(), CLIENT_ID);
            mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {
                    logStatus("-- Player initialized --");
                    mPlayer.setConnectivityStatus(mOperationCallback, getNetworkConnectivity(DemoActivity.this));
                    mPlayer.addNotificationCallback(DemoActivity.this);
                    mPlayer.addConnectionStateCallback(DemoActivity.this);
                    // Trigger UI refresh
                    updateView();
                }

                @Override
                public void onError(Throwable error) {
                    logStatus("Error in initialization: " + error.getMessage());
                }
            });
        } else {
            mPlayer.login(authResponse.getAccessToken());
        }
    }

    //UI

    private void updateView() {
        boolean loggedIn = isLoggedIn();

        // Set enabled for all widgets which depend on initialized state
        for (int id : REQUIRES_INITIALIZED_STATE) {
            findViewById(id).setEnabled(loggedIn);
        }

        // Same goes for the playing state
        boolean playing = loggedIn && mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying;
        for (int id : REQUIRES_PLAYING_STATE) {
            findViewById(id).setEnabled(playing);
        }

        if (mMetadata != null) {
            //findViewById(R.id.skip_next_button).setEnabled(mMetadata.nextTrack != null);
            //findViewById(R.id.skip_prev_button).setEnabled(mMetadata.prevTrack != null);
            findViewById(R.id.pause_button).setEnabled(mMetadata.currentTrack != null);
        }

        final ImageView coverArtView = (ImageView) findViewById(R.id.cover_art);
        if (mMetadata != null && mMetadata.currentTrack != null) {
            //Set the metadata from song length to Minutes:Seconds, rather than milliseconds.
            final String durationStr =
                    String.format("\n %02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(mMetadata.currentTrack.durationMs),
                    TimeUnit.MILLISECONDS.toSeconds(mMetadata.currentTrack.durationMs) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mMetadata.currentTrack.durationMs))
            );
            //mMetadataText.setText(mMetadata.contextName + "\n" + mMetadata.currentTrack.name + " - " + mMetadata.currentTrack.artistName + durationStr);
            mMetadataText.setText(mMetadata.currentTrack.name + "\n" + mMetadata.currentTrack.artistName);
            mMetadataText.setMovementMethod(new ScrollingMovementMethod());

            Picasso.with(this)
                    .load(mMetadata.currentTrack.albumCoverWebUrl)
                    .transform(new Transformation() {
                        @Override
                        public Bitmap transform(Bitmap source) {
                            final Bitmap copy = source.copy(source.getConfig(), true);
                            source.recycle();
                            final Canvas canvas = new Canvas(copy);
                            return copy;
                        }

                        @Override
                        public String key() {
                            return "darken";
                        }
                    })
                    .into(coverArtView);
        } else {
            mMetadataText.setText("You're not playing anything yet! " +
                                    "\n" +
                                    "Are you signed in?");
            coverArtView.setBackground(null);
        }

    }

    private boolean isLoggedIn() {
        return mPlayer != null && mPlayer.isLoggedIn();
    }

    public void onLoginButtonClicked(View view) {
        if (!isLoggedIn()) {
            logStatus("Logging in");
            openLoginWindow();
            showAlertbox(null);
        } else {
            //mPlayer.logout();
        }
    }

    public void onPlayButtonClicked(View view) {

        String uri = TEST_SONG_URI;

        logStatus("Starting playback for " + uri);
        mPlayer.playUri(mOperationCallback, uri, 0, 0);
    }

    private static boolean genreSwitchResume = false;

    public void onGenreButtonClicked(View view) {
        //Array that contains all potential playlists.
        String[] playlists = {"spotify:user:4joshua-cawthorne:playlist:4vr1l7iKUXfsmxEFlQabwG",
                "spotify:user:spotify:playlist:37i9dQZF1DX6VdMW310YC7"};
        //Create random
        Random random = new Random();
        //Pause Music if user is playing some.
        if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
            onPauseButtonClicked(view);
            genreSwitchResume = true;
        }
        //Set genre to be random selection from above array
        int index = random.nextInt(playlists.length);
        TEST_PLAYLIST_URI = playlists[index];

        if(genreSwitchResume = true){
            onPauseButtonClicked(view);
            mPlayer.playUri(mOperationCallback, TEST_PLAYLIST_URI, 0, 0);
        }
        Log.e("CREATION", playlists[index]);
    }

    AlertDialog alert;
    String selectedFromList = "null";

    final public void showAlertbox(View view) {
        {String names[] ={"Pop","Classical","Electronic","Funk"};
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DemoActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.musicplayer_list, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle("Choose a genre:");
            final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
            lv.setAdapter(adapter);
            alert = alertDialog.create();
            alert.show();

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                    selectedFromList =(String) (lv.getItemAtPosition(myItemInt));
                    Log.e("PLAYLISTS", selectedFromList);
                    setGenre();
                    alert.dismiss();
                }
            });
        }
    }

    public void setGenre() {
        if (selectedFromList == "Pop") {
            String[] popGenre = {
                    "spotify:user:spotify:playlist:37i9dQZF1DWY4lFlS4Pnso",
                    "spotify:user:spotify:playlist:37i9dQZF1DWSVtp02hITpN",
                    "spotify:user:spotify:playlist:37i9dQZF1DXdc6Ams1C6tL",
            };
            //Create random
            Random random = new Random();
            //Pause Music if user is playing some.
            if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
                genreSwitchResume = true;
            }
            //Set genre to be random selection from above array
            int index = random.nextInt(popGenre.length);
            TEST_PLAYLIST_URI = popGenre[index];

            if(genreSwitchResume = true){
                mPlayer.playUri(mOperationCallback, TEST_PLAYLIST_URI, 0, 0);
            }
        }
        else if (selectedFromList == "Classical") {
            String[] classicalGenre = {
                    "spotify:user:spotify:playlist:7MizIujRqHWLFVZAfQ21h4",
                    "spotify:user:spotify:playlist:37i9dQZF1DX561TxkFttR4",
                    "spotify:user:spotify:playlist:37i9dQZF1DXah8e1pvF5oE",
            };
            //Create random
            Random random = new Random();
            //Pause Music if user is playing some.
            if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
                genreSwitchResume = true;
            }
            //Set genre to be random selection from above array
            int index = random.nextInt(classicalGenre.length);
            TEST_PLAYLIST_URI = classicalGenre[index];

            if(genreSwitchResume = true){
                mPlayer.playUri(mOperationCallback, TEST_PLAYLIST_URI, 0, 0);
            }

        }
        else if(selectedFromList == "Electronic") {
            String[] electronicGenre = {
                    "spotify:user:spotify:playlist:37i9dQZF1DX5uokaTN4FTR",
                    "spotify:user:spotify:playlist:37i9dQZF1DWSqPHam7LOqC",
                    "spotify:user:spotify:playlist:37i9dQZF1DWSrVdvTl1tVY",
            };
            //Create random
            Random random = new Random();
            //Pause Music if user is playing some.
            if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
                genreSwitchResume = true;
            }
            //Set genre to be random selection from above array
            int index = random.nextInt(electronicGenre.length);
            TEST_PLAYLIST_URI = electronicGenre[index];

            if(genreSwitchResume = true){
                mPlayer.playUri(mOperationCallback, TEST_PLAYLIST_URI, 0, 0);
            }
        }
        else if(selectedFromList == "Funk") {
            String[] funkyGenre = {
                    "spotify:user:spotify:playlist:37i9dQZF1DX23YPJntYMnh",
                    "spotify:user:spotify:playlist:37i9dQZF1DX6drTZKzZwSo",
                    "spotify:user:spotify:playlist:37i9dQZF1DWSrVdvTl1tVY",
            };
            //Create random
            Random random = new Random();
            //Pause Music if user is playing some.
            if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
                genreSwitchResume = true;
            }
            //Set genre to be random selection from above array
            int index = random.nextInt(funkyGenre.length);
            TEST_PLAYLIST_URI = funkyGenre[index];

            if(genreSwitchResume = true){
                mPlayer.playUri(mOperationCallback, TEST_PLAYLIST_URI, 0, 0);
            }
        }
        else if(selectedFromList == "Not") {
            String[] funkGenre = {
                    "spotify:user:spotify:playlist:4Ebgfxsss10NoZbpRq1E06",
                    "spotify:user:spotify:playlist:37i9dQZF1DX23YPJntYMnh",
                    "spotify:user:spotify:playlist:37i9dQZF1DX7Q7o98uPeg1",
            };
            //Create random
            Random random = new Random();
            //Pause Music if user is playing some.
            if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
                genreSwitchResume = true;
            }
            //Set genre to be random selection from above array
            int index = random.nextInt(funkGenre.length);
            TEST_PLAYLIST_URI = funkGenre[index];

            if(genreSwitchResume = true){
                mPlayer.playUri(mOperationCallback, TEST_PLAYLIST_URI, 0, 0);
            }
        }
    }

    public void onPauseButtonClicked(View view) {
        if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
            mPlayer.pause(mOperationCallback);
        } else {
            mPlayer.resume(mOperationCallback);
        }
    }

    public void onSkipToPreviousButtonClicked(View view) {
        mPlayer.skipToPrevious(mOperationCallback);
    }

    public void onSkipToNextButtonClicked(View view) {
        mPlayer.skipToNext(mOperationCallback);
    }

    public void onQueueSongButtonClicked(View view) {
        mPlayer.queue(mOperationCallback, TEST_QUEUE_SONG_URI);
        Toast toast = Toast.makeText(this, R.string.song_queued_toast, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onToggleShuffleButtonClicked(View view) {
        mPlayer.setShuffle(mOperationCallback, !mCurrentPlaybackState.isShuffling);
    }

    public void onToggleRepeatButtonClicked(View view) {
        mPlayer.setRepeat(mOperationCallback, !mCurrentPlaybackState.isRepeating);
    }

    public void onSeekButtonClicked(View view) {
        final Integer seek = Integer.valueOf(mSeekEditText.getText().toString());
        mPlayer.seekToPosition(mOperationCallback, seek);
    }

    public void onLowBitrateButtonPressed(View view) {
        mPlayer.setPlaybackBitrate(mOperationCallback, PlaybackBitrate.BITRATE_LOW);
    }

    public void onNormalBitrateButtonPressed(View view) {
        mPlayer.setPlaybackBitrate(mOperationCallback, PlaybackBitrate.BITRATE_NORMAL);
    }

    public void onHighBitrateButtonPressed(View view) {
        mPlayer.setPlaybackBitrate(mOperationCallback, PlaybackBitrate.BITRATE_HIGH);
    }

    //Callback Methods

    @Override
    public void onLoggedIn() {
        logStatus("Login complete");
        updateView();
    }

    @Override
    public void onLoggedOut() {
        logStatus("Logout complete");
        updateView();
    }

    public void onLoginFailed(Error error) {
        logStatus("Login error "+ error);
    }

    @Override
    public void onTemporaryError() {
        logStatus("Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(final String message) {
        logStatus("Incoming connection message: " + message);
    }

    // Errors and stuff a lot like, but not identical to errors.
    private void logStatus(String status) {

    }

    // Destruction.

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNetworkStateReceiver);

        if (mPlayer != null) {
            mPlayer.removeNotificationCallback(DemoActivity.this);
            mPlayer.removeConnectionStateCallback(DemoActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent event) {
        logStatus("Event: " + event);
        mCurrentPlaybackState = mPlayer.getPlaybackState();
        mMetadata = mPlayer.getMetadata();
        Log.i(TAG, "Player state: " + mCurrentPlaybackState);
        Log.i(TAG, "Metadata: " + mMetadata);
        updateView();
    }

    @Override
    public void onPlaybackError(Error error) {
        logStatus("Err: " + error);
    }

    protected void onStart() {
        super.onStart();
        requestPermissions();
        Log.d("APP", "Application Started");
        mGoogleApiClient.connect();


    }

    protected void onStop() {
        Log.d("APP", "Application Stopping");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //to be adjusted later, maybe as a setting
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

                    //For testing only
                    //speedView.setText("Speed: " + pedo.getSpeed());

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

            //setContentView(R.layout.post_run); //Start the Post Run Screen (Just displays the layout, doesn't change to the PostRun activity)

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


}
