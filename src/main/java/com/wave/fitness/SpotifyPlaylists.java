package com.wave.fitness;

import java.util.EnumMap;
import java.util.List;

/**
 * Created by s6236422 on 14/05/2017.
 */

public class SpotifyPlaylists {

    public enum Genre{
        POP, CLASSICAL, ELECTRONIC, FUNK
    }

    public static EnumMap<Genre, String[]> allGenre = new EnumMap<Genre, String[]>(Genre.class){{
        put(Genre.POP, popGenre);
        put(Genre.CLASSICAL, classicalGenre);
        put(Genre.ELECTRONIC, electronicGenre);
        put(Genre.FUNK, funkGenre);

    }};

    public static final String[] popGenre = {
            "spotify:user:spotify:playlist:37i9dQZF1DWY4lFlS4Pnso",
            "spotify:user:spotify:playlist:37i9dQZF1DWSVtp02hITpN",
            "spotify:user:spotify:playlist:37i9dQZF1DXdc6Ams1C6tL",
    };

    public static final String[] classicalGenre = {
            "spotify:user:spotify:playlist:7MizIujRqHWLFVZAfQ21h4",
            "spotify:user:spotify:playlist:37i9dQZF1DX561TxkFttR4",
            "spotify:user:spotify:playlist:37i9dQZF1DXah8e1pvF5oE",
    };

    public static final String[] electronicGenre = {
            "spotify:user:spotify:playlist:37i9dQZF1DX5uokaTN4FTR",
            "spotify:user:spotify:playlist:37i9dQZF1DWSqPHam7LOqC",
            "spotify:user:spotify:playlist:37i9dQZF1DWSrVdvTl1tVY",
    };

    public static final String[] funkGenre = {
            "spotify:user:spotify:playlist:37i9dQZF1DX23YPJntYMnh",
            "spotify:user:spotify:playlist:37i9dQZF1DX6drTZKzZwSo",
            "spotify:user:spotify:playlist:37i9dQZF1DWSrVdvTl1tVY",
    };
}
