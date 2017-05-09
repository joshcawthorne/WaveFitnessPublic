package com.wave.fitness;

import android.app.Application;

import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.SpotifyPlayer;

import studios.codelight.smartloginlibrary.users.SmartUser;

/**
 * Created by s6236422 on 08/05/2017.
 */

public class SpotifyCore extends Application {
    public SpotifyPlayer mPlayer;
    public PlaybackState mCurrentPlaybackState;
    public Metadata mMetadata;
    public boolean isLoggedIn = false;
    public AuthenticationResponse authResponse;
    public String firstName;
}
