package com.benavides.ramon.popularmovies.fragments;

import android.os.Bundle;

/**
 * Created by Ramon on 30/08/2016.
 */
public class ReviewsFragment extends BaseFragment  {
    public static ReviewsFragment newInstance(int movieId) {

        Bundle args = new Bundle();
        args.putInt(MOVIE_PARAM, movieId);

        ReviewsFragment fragment = new ReviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public String getTitle() {
        return null;
    }
}
