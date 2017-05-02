/*

    Wave Fitness Version 1.0

    This is version 1.0 of Wave's Spotify implementation. It utilises the Spotify Android SDK, and it's example project.

    This is merely a foundation to the app, and should be heavily adopted and changed.

    ~ Josh Cawthorne.

    IMPORTANT: DUE TO A BUG WITH THE SPOTIFY SDK, IF A USER HAS THE SPOTIFY APP INSTALLED ON THEIR PHONE, AND THEY TRY TO SIGN IN,
    AUTHENTICATION WILL FAIL.

 */
package com.wave.fitness;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DemoActivity extends Activity implements
        Player.NotificationCallback, ConnectionStateCallback {

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
    private static final String TEST_SONG_URI = "spotify:track:7v9qEzfnEuJDjBg8B7GppL";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_SONG_48kHz_URI = "spotify:track:3wxTNS3aqb9RbBLZgJdZgH";
    @SuppressWarnings("SpellCheckingInspection")
    //NOTE: It took me an age to work out that playlists require both the playlist creator (User) AND the ID!
    private static String TEST_PLAYLIST_URI = "spotify:user:4joshua-cawthorne:playlist:4vr1l7iKUXfsmxEFlQabwG";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_ALBUM_URI = "spotify:album:4ei0RkOn29dn174wallj5w";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String TEST_QUEUE_SONG_URI = "spotify:track:5EEOjaJyWvfMglmEwf9bG3";

    //Generate bool for genre switch
    boolean genreSwitchResume = false;

    //Request code that will be passed together with authentication result to the onAuthenticationResult
    private static final int REQUEST_CODE = 1337;

    //These are UI controls, which can only be used after a user has logged into spotify.
    private static final int[] REQUIRES_INITIALIZED_STATE = {
            R.id.pause_button,
            R.id.genre_switch_button,
    };

    //These are UI controls which can only be used once a song is playing.
    private static final int[] REQUIRES_PLAYING_STATE = {
            R.id.skip_next_button,
            R.id.skip_prev_button,
            R.id.queue_song_button,
            R.id.toggle_shuffle_button,
            R.id.toggle_repeat_button,
    };
    public static final String TAG = "SpotifySdkDemo";

    //Fields

    private SpotifyPlayer mPlayer;

    private PlaybackState mCurrentPlaybackState;

    //Used to recieve info on a user's current network status (IE: Wireless, Offline)
    private BroadcastReceiver mNetworkStateReceiver;

    private TextView mMetadataText;

    private EditText mSeekEditText;

    private ScrollView mStatusTextScrollView;
    private Metadata mMetadata;

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

    //Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

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

        // Login button should be the inverse of the logged in state
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setText(loggedIn ? R.string.logout_button_label : R.string.login_button_label);

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
            findViewById(R.id.skip_next_button).setEnabled(mMetadata.nextTrack != null);
            findViewById(R.id.skip_prev_button).setEnabled(mMetadata.prevTrack != null);
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
            mMetadataText.setText(mMetadata.contextName + "\n" + mMetadata.currentTrack.name + " - " + mMetadata.currentTrack.artistName + durationStr);
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
        } else {
            mPlayer.logout();
        }
    }

    public void onPlayButtonClicked(View view) {
        String uri = TEST_PLAYLIST_URI;
        logStatus("Starting playback for " + uri);
        mPlayer.playUri(mOperationCallback, uri, 0, 0);
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
            View convertView = (View) inflater.inflate(R.layout.list, null);
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
}
