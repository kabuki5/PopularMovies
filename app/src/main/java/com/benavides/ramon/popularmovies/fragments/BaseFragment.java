package com.benavides.ramon.popularmovies.fragments;


import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    static final String MOVIE_PARAM = "movie_id";

    public abstract String getTitle();
}
