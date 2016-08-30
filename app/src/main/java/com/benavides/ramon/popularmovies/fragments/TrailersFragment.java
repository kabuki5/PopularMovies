package com.benavides.ramon.popularmovies.fragments;

import android.os.Bundle;

/**
 * Created by Ramon on 30/08/2016.
 */
public class TrailersFragment extends BaseFragment  {

    public static TrailersFragment newInstance(int movieId) {

        Bundle args = new Bundle();
        args.putInt(MOVIE_PARAM, movieId);

        TrailersFragment fragment = new TrailersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getTitle() {
        return null;
    }
}
