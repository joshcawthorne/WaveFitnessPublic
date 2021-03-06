/*

    Wave Fitness Version 1.0

    This is version 1.0 of Wave's Spotify implementation. It utilises the Spotify Android SDK, and it's example project.

    This is merely a foundation to the app, and should be heavily adopted and changed.

    ~ Josh Cawthorne.

    IMPORTANT: DUE TO A BUG WITH THE SPOTIFY SDK, IF A USER HAS THE SPOTIFY APP INSTALLED ON THEIR PHONE, AND THEY TRY TO SIGN IN,
    AUTHENTICATION WILL FAIL.

 */
package com.wave.fitness.fragments;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackBitrate;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.wave.fitness.AuthActivity;
import com.wave.fitness.BusProvider;
import com.wave.fitness.R;

import com.wave.fitness.SpotifyCore;
import com.wave.fitness.SpotifyPlaylists;
import com.wave.fitness.runningEvent.TrackChangedEvent;
import com.wave.fitness.runningEvent.UpdateRunStatEvent;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class SpotifyFragmentActivity extends Fragment implements
        Player.NotificationCallback, ConnectionStateCallback{

    /* Creates the entire music player fragment. */

    //Constants

    //Required to make the app work - this is our public key and callback URL!
    //Also included suppress warnings.
    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "0389348b1134489d870dc8730a7fe33a";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "testschema://callback";

    public String TEST_PLAYLIST_URI = "";

    //Generate bool for genre switch
    boolean genreSwitchResume = false;

    //Request code that will be passed together with authentication result to the onAuthenticationResult
    private static final int REQUEST_CODE = 1337;

    //These are UI controls, which can only be used after a user has logged into spotify.
    private static final int[] REQUIRES_INITIALIZED_STATE = {
            R.id.pause_button,
            //R.id.genre_switch_button,
    };

    //These are UI controls which can only be used once a song is playing.
    private static final int[] REQUIRES_PLAYING_STATE = {
            R.id.skip_next_button,
            R.id.skip_prev_button,
    };

    public static final String TAG = "Music";

    //Used to recieve info on a user's current network status (IE: Wireless, Offline)
    private BroadcastReceiver mNetworkStateReceiver;

    //UI
    private TextView mMetadataText;
    private TextView mMetaDataSubtext;
    private TextView mMetaDataTime;
    private TextView mMusicTitle;
    private CircleImageView profile_image;
    private String currentPlaylist = "null";

    public String oldURL = "";
    private ArrayList<Metadata.Track> songHistory;
    private void addSongToHistory(){
        songHistory.add(core.mMetadata.prevTrack);
        if(songHistory.size()>10){
            songHistory.remove(0);
            for(int i = 1; i<12; i++){
                songHistory.add(i-1, songHistory.get(i));
            }
        }
        Log.e("History", songHistory.toString());
    }

    private View v;

    private SpotifyCore core;
    private static final int SPOTIFY_LOGIN = 87;
    private boolean isFirstSong = true;

    CollapsingToolbarLayout collapsingToolbarLayout;

    long songLength = 0;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_musicplayer, container, false);

        ImageView playfab = (ImageView) v.findViewById(R.id.pause_button);
        ImageView prevImg = (ImageView) v.findViewById(R.id.skip_prev_button);
        ImageView skipImg = (ImageView) v.findViewById(R.id.skip_next_button);
        skipImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSkipToNextButtonClicked();
            }
        });
        prevImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSkipToPreviousButtonClicked();
            }
        });

        playfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseButtonClicked();
            }
        });
        FloatingActionButton shuffleGenre = (FloatingActionButton) v.findViewById(R.id.switch_genre);

        shuffleGenre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeGenre();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        core = ((SpotifyCore)getApplicationContext());
        songHistory = new ArrayList<Metadata.Track>();
        profile_image = (CircleImageView) v.findViewById(R.id.profile_image);

        if(!core.isLoggedIn){
            startActivityForResult(new Intent(getActivity(), AuthActivity.class), SPOTIFY_LOGIN);

        }else {
            createPlayer();

        }
        updateProfilePic();

        // Get a reference to any UI widgets that will be needed.
        mMetadataText = (TextView) getView().findViewById(R.id.metadataTitle);
        mMetaDataSubtext = (TextView) getView().findViewById(R.id.metadataSubTitle);
        mMetaDataTime = (TextView) getView().findViewById(R.id.metaDataTime);
        mMetadataText.setSelected(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Error - No data supplied");
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.black));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.black));
        collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.wavePrimary));

        updateView();

        final ImageView fab = (ImageView) getView().findViewById(R.id.pause_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (core.mCurrentPlaybackState != null && core.mCurrentPlaybackState.isPlaying) {
                    core.mPlayer.pause(mOperationCallback);
                    fab.setBackgroundResource(R.drawable.playic);
                } else {
                    core.mPlayer.resume(mOperationCallback);
                    fab.setBackgroundResource(R.drawable.pauseic);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);

        // Set up the broadcast receiver for network events.
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (core.mPlayer != null) {
                    Connectivity connectivity = getNetworkConnectivity(getActivity().getBaseContext());
                    logStatus("Network state changed: " + connectivity.toString());
                    core.mPlayer.setConnectivityStatus(mOperationCallback, connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mNetworkStateReceiver, filter);

        if (core.mPlayer != null) {
            core.mPlayer.addNotificationCallback(SpotifyFragmentActivity.this);
            core.mPlayer.addConnectionStateCallback(SpotifyFragmentActivity.this);
        }

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
                if (keyguardManager.isKeyguardSecure()) {
                    updateView();
                    Log.d("PHONE STATUS", "USER UNLOCKED");

                }
            }
        },new IntentFilter("android.intent.action.USER_PRESENT"));
        updateView();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == SPOTIFY_LOGIN){
            if(resultCode == RESULT_OK){
                createPlayer();
            }
        }

    }

    private void createPlayer() {

        // Once we have obtained an authorization token, we can proceed with creating a Player.

        logStatus("Got authentication token");
            Config playerConfig = new Config(getApplicationContext(), core.authResponse.getAccessToken(), CLIENT_ID);
            core.mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {
                    logStatus("-- Player initialized --");
                    core.mPlayer.setConnectivityStatus(mOperationCallback, getNetworkConnectivity(getActivity()));
                    core.mPlayer.addNotificationCallback(SpotifyFragmentActivity.this);
                    core.mPlayer.addConnectionStateCallback(SpotifyFragmentActivity.this);
                    setGenre();
                    // Trigger UI refresh
                    updateView();

                }

                @Override
                public void onError(Throwable error) {
                    logStatus("Error in initialization: " + error.getMessage());
                }
            });
    }

    //UI

    private void updateView() {
        boolean loggedIn = isLoggedIn();

        final ImageView coverArtView = (ImageView) getView().findViewById(R.id.cover_art);
        final ImageView coverArtSmall = (ImageView) getView().findViewById(R.id.cover_art_small);
        CircleImageView roundcoveart = (CircleImageView) getView().findViewById(R.id.roundart);

        boolean playing = loggedIn && core.mCurrentPlaybackState != null && core.mCurrentPlaybackState.isPlaying;
        for (int id : REQUIRES_PLAYING_STATE) {
            getView().findViewById(id).setEnabled(playing);
        }

        if (core.mMetadata != null) {
            getView().findViewById(R.id.skip_next_button).setEnabled(core.mMetadata.nextTrack != null);
            getView().findViewById(R.id.skip_prev_button).setEnabled(core.mMetadata.prevTrack != null);
            getView().findViewById(R.id.pause_button).setEnabled(core.mMetadata.currentTrack != null);
        }

        if( coverArtView.getDrawable() != null){
            coverArtView.setVisibility(View.INVISIBLE);  // make image visible
        }else{
            coverArtView.setVisibility(View.VISIBLE);
        }

        if (core.mMetadata != null && core.mMetadata.currentTrack != null) {
            //Set the metadata from song length to Minutes:Seconds, rather than milliseconds.
            final String durationStr =
                    String.format("\n %02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(core.mMetadata.currentTrack.durationMs),
                    TimeUnit.MILLISECONDS.toSeconds(core.mMetadata.currentTrack.durationMs) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(core.mMetadata.currentTrack.durationMs))
            );

            songLength = core.mMetadata.currentTrack.durationMs;

            mMetadataText.setText(core.mMetadata.currentTrack.name);
            mMetaDataSubtext.setText(core.mMetadata.currentTrack.artistName);
            mMetaDataTime.setText(durationStr);

            mMusicTitle = (TextView) v.findViewById(R.id.tracktitle);
            mMusicTitle.setText(core.mMetadata.currentTrack.artistName);
            collapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar);
            collapsingToolbarLayout.setTitle(core.mMetadata.currentTrack.name);
            collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.black));
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
            collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.wavePrimary));
            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

            coverArtView.setVisibility(View.INVISIBLE);

            Picasso.with(getActivity())
                    .load(core.mMetadata.currentTrack.albumCoverWebUrl)
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

            Picasso.with(getActivity())
                    .load(core.mMetadata.currentTrack.albumCoverWebUrl)
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
                    .into(coverArtSmall);

            Picasso.with(getActivity())
                    .load(core.mMetadata.currentTrack.albumCoverWebUrl)
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
                    .into(roundcoveart);

            coverArtView.setVisibility(View.VISIBLE);

        } else {
            mMetadataText.setText(" ");
            coverArtView.setVisibility(View.INVISIBLE);
            coverArtSmall.setBackground(null);
        }
    }

    private boolean isLoggedIn() {
        return core.mPlayer != null && core.mPlayer.isLoggedIn();
    }

    public void onPlayButtonClicked(FloatingActionButton fab) {
        String uri = "spotify:user:spotify:playlist:7MizIujRqHWLFVZAfQ21h4";
        logStatus("Starting playback for " + uri);
        core.mPlayer.playUri(mOperationCallback, uri, 0, 0);

        fab.setImageDrawable(getResources().getDrawable(R.drawable.pause));
    }

    public void onPauseButtonClicked() {
        if (core.mCurrentPlaybackState != null && core.mCurrentPlaybackState.isPlaying) {
            core.mPlayer.pause(mOperationCallback);
        } else {
            core.mPlayer.resume(mOperationCallback);
        }
    }

    public void onSkipToPreviousButtonClicked() {
        core.mPlayer.skipToPrevious(mOperationCallback);
    }

    public void onSkipToNextButtonClicked() {
        core.mPlayer.skipToNext(mOperationCallback);
    }

    public void setGenre() {

        if(core.dashboardCard) {
            TEST_PLAYLIST_URI = core.chosenPlaylist;
            checkMusic();
        }

        else {
            ArrayList<String> selectedSongs = new ArrayList<String>();
            for(SpotifyPlaylists.Genre genre : SpotifyPlaylists.Genre.values()){
                if(core.selectedGenre.get(genre)){
                    selectedSongs.addAll(Arrays.asList(SpotifyPlaylists.allGenre.get(genre)));
                }
            }

            if (core.mCurrentPlaybackState != null && core.mCurrentPlaybackState.isPlaying) {
                genreSwitchResume = true;
            }
            TEST_PLAYLIST_URI = selectedSongs.get(new Random().nextInt(selectedSongs.size()));
            checkMusic();
        }
    }

    private void updateProfilePic(){
        Picasso.with(getActivity())
                .load(core.user.getPhotoUrl())
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
                .into(profile_image);
    }

    public void changeGenre() {
       oldURL = TEST_PLAYLIST_URI;

        if(core.dashboardCard) {
            onSkipToNextButtonClicked();
        }

        else {
            ArrayList<String> selectedSongs = new ArrayList<String>();
            for(SpotifyPlaylists.Genre genre : SpotifyPlaylists.Genre.values()){
                if(core.selectedGenre.get(genre)){
                    selectedSongs.addAll(Arrays.asList(SpotifyPlaylists.allGenre.get(genre)));
                }
            }

            if (core.mCurrentPlaybackState != null && core.mCurrentPlaybackState.isPlaying) {
                genreSwitchResume = true;
            }
            TEST_PLAYLIST_URI = selectedSongs.get(new Random().nextInt(selectedSongs.size()));

            stopRepeatGenre();
        }
    }

    public void stopRepeatGenre() {
        if (oldURL == TEST_PLAYLIST_URI) {
            changeGenre();
        }
        else{
            checkMusic();
        }
    }

    public void checkMusic() {
        while(!core.mPlayer.isLoggedIn()){

        }
        if (currentPlaylist == "null") {
            core.mPlayer.playUri(mOperationCallback, TEST_PLAYLIST_URI, 0, 0);
            currentPlaylist = TEST_PLAYLIST_URI;
        }

        else if(currentPlaylist == TEST_PLAYLIST_URI) {
            setGenre();
        }

        else if(currentPlaylist != TEST_PLAYLIST_URI) {
            core.mPlayer.playUri(mOperationCallback, TEST_PLAYLIST_URI, 0, 0);

            currentPlaylist = TEST_PLAYLIST_URI;
        }

    }


    public void onToggleShuffleButtonClicked(View view) {
        Log.e("SHUFFLING", "TRUE");
        core.mPlayer.setShuffle(mOperationCallback, !core.mCurrentPlaybackState.isShuffling);
    }

    public void onToggleRepeatButtonClicked(View view) {
        core.mPlayer.setRepeat(mOperationCallback, !core.mCurrentPlaybackState.isRepeating);
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
        Log.e("Player", status);
    }

    // Destruction.

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
        getActivity().unregisterReceiver(mNetworkStateReceiver);

        if (core.mPlayer != null) {
            core.mPlayer.removeNotificationCallback(SpotifyFragmentActivity.this);
            core.mPlayer.removeConnectionStateCallback(SpotifyFragmentActivity.this);
        }
    }

    @Override
    public void onDestroy() {
        if (core.mPlayer != null) {
            core.mPlayer.removeNotificationCallback(SpotifyFragmentActivity.this);
            core.mPlayer.removeConnectionStateCallback(SpotifyFragmentActivity.this);
        }
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }


    @Override
    public void onPlaybackEvent(PlayerEvent event) {
        if(event == PlayerEvent.kSpPlaybackNotifyBecameActive){
            core.mPlayer.setShuffle(mOperationCallback, true);
        }
        if(event == PlayerEvent.kSpPlaybackNotifyShuffleOff){
            core.mPlayer.setShuffle(mOperationCallback, true);
        }
        if(isFirstSong && event == PlayerEvent.kSpPlaybackNotifyMetadataChanged){
            core.mPlayer.skipToNext(mOperationCallback);
            isFirstSong = false;
            return;
        }
        if(event == PlayerEvent.kSpPlaybackNotifyTrackChanged){
            BusProvider.getInstance().post(new TrackChangedEvent());
            addSongToHistory();
        }
        if(!isFirstSong) {
            logStatus("Event: " + event);
            core.mCurrentPlaybackState = core.mPlayer.getPlaybackState();
            core.mMetadata = core.mPlayer.getMetadata();
            Log.i(TAG, "Player state: " + core.mCurrentPlaybackState);
            Log.i(TAG, "Metadata: " + core.mMetadata);
            updateView();
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        logStatus("Err: " + error);
    }

    @Subscribe
    public void onRunDataUpdate(UpdateRunStatEvent event){

        TextView stattitle = (TextView) v.findViewById(R.id.stattitle);
        stattitle.setText(core.firstName + "'s Stats:");

        TextView runlength = (TextView) v.findViewById(R.id.runlength);
        runlength.setText(String.valueOf(event.mDistanceValue));

        TextView speedcur = (TextView) v.findViewById(R.id.runspeed);
        TextView calcur = (TextView) v.findViewById(R.id.calburnt);
        TextView distancerun = (TextView) v.findViewById(R.id.distancerun);
        TextView curspeedcur = (TextView) v.findViewById(R.id.runspeed);
        speedcur.setText(String.valueOf(event.mPaceValue));
        calcur.setText(String.valueOf(event.mCaloriesValue));
        curspeedcur.setText(String.valueOf(event.mSpeedValue));
        distancerun.setText(String.valueOf(event.mStepValue));
    }
}
