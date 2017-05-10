package com.wave.fitness;

import android.app.Application;

import com.rogalabs.lib.LoginApplication;
import com.rogalabs.lib.model.SocialUser;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.SpotifyPlayer;


/**
 * Created by s6236422 on 08/05/2017.
 */

public class SpotifyCore extends Application {
    public SpotifyPlayer mPlayer;
    public PlaybackState mCurrentPlaybackState;
    public Metadata mMetadata;
    public boolean isLoggedIn = false;
    public AuthenticationResponse authResponse;
    public SocialUser user = new SocialUser("0", "Joe Doe", "test@test.com", null, null, null, null);

    @Override
    public void onCreate() {
        super.onCreate();
        LoginApplication.startSocialLogin(this);
    }
}
