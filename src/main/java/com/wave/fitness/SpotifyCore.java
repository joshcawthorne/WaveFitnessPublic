package com.wave.fitness;

import android.app.Application;

import com.rogalabs.lib.LoginApplication;
import com.rogalabs.lib.model.SocialUser;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.squareup.otto.Bus;

import java.sql.Array;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import static com.wave.fitness.SpotifyPlaylists.*;

/**
 * Created by s6236422 on 08/05/2017.
 */

public class SpotifyCore extends Application {
    public SpotifyPlayer mPlayer;
    public PlaybackState mCurrentPlaybackState;
    public Metadata mMetadata;
    public boolean isLoggedIn = false;
    public AuthenticationResponse authResponse;
    public SocialUser user;
    public String testString;
    public String firstName;
    public EnumMap<SpotifyPlaylists.Genre, Boolean> selectedGenre = new EnumMap<SpotifyPlaylists.Genre, Boolean>(SpotifyPlaylists.Genre.class);
    public boolean isRunning = false;

    public boolean dashboardCard = false;
    public String chosenPlaylist = "";

    @Override
    public void onCreate() {
        super.onCreate();
        LoginApplication.startSocialLogin(this);

        BusProvider.BUS = new Bus();

        SpotifyPlaylists.allGenre = new EnumMap<SpotifyPlaylists.Genre, String[]>(SpotifyPlaylists.Genre.class){{
            put(SpotifyPlaylists.Genre.POP, popGenre);
            put(SpotifyPlaylists.Genre.CLASSICAL, classicalGenre);
            put(SpotifyPlaylists.Genre.ELECTRONIC, electronicGenre);
            put(SpotifyPlaylists.Genre.FUNK, funkGenre);
            put(SpotifyPlaylists.Genre.ROCK, rockGenre);
            put(SpotifyPlaylists.Genre.JAZZFUSION, jazzfusionGenre);
            put(SpotifyPlaylists.Genre.INDIEROCK, indierockGenre);
            put(SpotifyPlaylists.Genre.HITS, hitsGenre);
        }};
    }
}
