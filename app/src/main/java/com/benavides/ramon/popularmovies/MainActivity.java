package com.benavides.ramon.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.fragments.MovieDetailFragment;
import com.benavides.ramon.popularmovies.interfaces.MovieSelectorInterface;

public class MainActivity extends AppCompatActivity implements MovieSelectorInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onMovieSelected(Movie movie) {
        MovieDetailFragment movieDetailFragment = ((MovieDetailFragment) getFragmentManager().findFragmentById(R.id.detail_movies_frg));

        if (movieDetailFragment != null) {
            movieDetailFragment.setVisibilityLayoutItems(true);
            movieDetailFragment.updateContent(movie);
        }
    }
}
