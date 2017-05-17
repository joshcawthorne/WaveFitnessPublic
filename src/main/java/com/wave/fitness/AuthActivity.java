package com.wave.fitness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;


public class AuthActivity extends AppCompatActivity implements
        Player.NotificationCallback, ConnectionStateCallback{

    /* Authenticates a users Spotify Account */

    private static final String TAG = null;



    //Constants
    //Required to make the app work - this is our public key and callback URL!
    //Also included suppress warnings.
    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "0389348b1134489d870dc8730a7fe33a";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "testschema://callback";

    private SpotifyCore core;

    //Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_dashboard);

        core = ((SpotifyCore)getApplicationContext());

        logStatus("Logging in");
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming"})
                .build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        logStatus("Checking response");
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    logStatus("Auth OK");
                    core.isLoggedIn = true;
                    core.authResponse = response;
                    this.setResult(RESULT_OK);
                    this.finish();
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


    //Callback Methods

    @Override
    public void onLoggedIn() {
        logStatus("Login complete");
    }

    @Override
    public void onLoggedOut() {
        finish();
        System.exit(0);
        logStatus("Logout complete");
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

    @Override
    public void onPlaybackEvent(PlayerEvent event) {
        logStatus("Event: " + event);
        core.mCurrentPlaybackState = core.mPlayer.getPlaybackState();
        core.mMetadata = core.mPlayer.getMetadata();
        Log.i(TAG, "Player state: " + core.mCurrentPlaybackState);
        Log.i(TAG, "Metadata: " + core.mMetadata);
    }

    @Override
    public void onPlaybackError(Error error) {
        logStatus("Err: " + error);
    }

    // Errors and stuff a lot like, but not identical to errors.
    private void logStatus(String status) {
        Log.e("Auth", status);
    }
}