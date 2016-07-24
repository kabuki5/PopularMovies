package com.benavides.ramon.popularmovies.interfaces;

import com.benavides.ramon.popularmovies.data.Movie;

import java.util.ArrayList;

/**
 * Created by ramon on 15/7/16.
 */
public interface TmdbApiTaskListener {

    void onRequestSuccess(ArrayList<Movie> movies);

    void onRequestError();


}
