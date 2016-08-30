package com.benavides.ramon.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.benavides.ramon.popularmovies.data.Movie;
import com.benavides.ramon.popularmovies.fragments.MovieDetailFragment;
import com.benavides.ramon.popularmovies.interfaces.MovieSelectorInterface;
import com.benavides.ramon.popularmovies.sync.PopularmoviesSyncAdapter;
import com.benavides.ramon.popularmovies.utils.Utils;

public class MainActivity extends AppCompatActivity implements MovieSelectorInterface {

    private static final String DETAIL_FRG_TAG = "detail_frg";
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PopularmoviesSyncAdapter.initSyncAdapter(this);
    }

//TODO => pass movieID instead of Movie object
    @Override
    public void onMovieSelected(Movie movie) {

        //If it's tablet at landscape orientation
        twoPane = findViewById(R.id.detail_frg_container) != null;

        if(twoPane){
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movie, twoPane);
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_frg_container, movieDetailFragment, DETAIL_FRG_TAG)
                    .commit();

        }else{  //If it's mobile or tablet at portrait
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(getString(R.string.movie_intent_tag), movie);
            startActivity(detailIntent);
        }
    }
}
